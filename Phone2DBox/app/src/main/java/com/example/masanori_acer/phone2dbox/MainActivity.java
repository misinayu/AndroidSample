package com.example.masanori_acer.phone2dbox;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String APP_KEY = "lz5e9ykipzrm1f1";
    private static final String APP_SECRET = "bg60mxwa6pwygqu";
    private static final String TAG = "Phone2DBox";

    private DropboxAPI<AndroidAuthSession> mApi;
    private boolean mLoggedIn = false;

    private Button mTakePicture;
    private ImageView mImage;
    private Button mSubmit;

    private static final int NEW_PICTURE = 1;
    private String mCameraFileName;
    private File mFile = null;

    private final String PHOTO_DIR = "/Photos/";
    private SwingListener mSwingListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);

        mApi = new DropboxAPI<AndroidAuthSession>(session);

        setContentView(R.layout.activity_main);

        mSubmit = (Button)findViewById(R.id.submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoggedIn){
                    logOut();
                    mSubmit.setText(R.string.login);
                } else {
                    mApi.getSession().startOAuth2Authentication(MainActivity.this);
                }
            }
        });

        mImage = (ImageView)findViewById(R.id.imageView);
        mTakePicture = (Button)findViewById(R.id.photo_button);

        mTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss", Locale.US);

                String newPicFile = df.format(date) + ".jpg";
                String outPath = new File(Environment.getExternalStorageDirectory(), newPicFile).getPath();
                File outFile = new File(outPath);

                mCameraFileName = outFile.toString();
                Uri outuri = Uri.fromFile(outFile);
                Log.i(TAG, "uri: " + outuri);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
                Log.i(TAG, "Importing New Picture " + mCameraFileName);
                try {
                    startActivityForResult(intent, NEW_PICTURE);
                }catch (ActivityNotFoundException e){
                    showToast("There doesn't seem to be a camera.");
                }
            }
        });
        mSwingListener = new SwingListener(this);
        mSwingListener.setOnSwingListener(new SwingListener.OnSwingListener() {
            @Override
            public void onSwing() {
                if (mFile != null){
                    UploadPicture upload = new UploadPicture(MainActivity.this, mApi, PHOTO_DIR, mFile);
                    upload.execute();
                }
            }
        });

        mSwingListener.registSensor();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mCameraFileName", mCameraFileName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCameraFileName = savedInstanceState.getString("mCameraFileName");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSwingListener.unregistSensor();
        mSwingListener = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        if (session.authenticationSuccessful()){
            try {
                session.finishAuthentication();
                String accessToken = session.getOAuth2AccessToken();
                mLoggedIn = true;
                mSubmit.setText(R.string.logout);
            }catch (IllegalStateException e){
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_PICTURE){
            // return from file upload
            if (resultCode == Activity.RESULT_OK){
                Uri uri = null;
                if (data != null){
                    uri = data.getData();
                }
                if (uri == null && mCameraFileName != null){
                    uri = Uri.fromFile(new File(mCameraFileName));
                }
                Bitmap bitmap = BitmapFactory.decodeFile(mCameraFileName);
                mImage.setImageBitmap(bitmap);
                mFile = new File(mCameraFileName);

                if (uri != null){
                    showToast("投げ上げ動作で、Dropboxに写真をアップします");
                }
            }else {
                Log.w(TAG, "Unknown Activity Result from mediaImport: " + resultCode);
            }
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();
        mLoggedIn = false;
        showToast("ログアウトしました");
    }
}
