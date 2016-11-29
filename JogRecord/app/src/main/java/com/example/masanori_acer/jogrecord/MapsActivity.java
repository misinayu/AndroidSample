package com.example.masanori_acer.jogrecord;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, LoaderManager.LoaderCallbacks<Address> {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int ADDRESSLOADER_ID = 0;
    private static final int INTERVAL = 500;
    private static final int FASTESTINTERVAL = 16;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(INTERVAL)
            .setFastestInterval(FASTESTINTERVAL)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // 位置情報取得要求の優先順位
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    private List<LatLng> mRunList = new ArrayList<LatLng>();
    private WifiManager mWifi;
    private boolean mWifiOff = false;
    private long mStartTimeMillis;
    private double mMeter = 0.0;
    private double elapsedTime = 0.0;
    private double mSpeed = 0.0;
    private DatabaseHelper mDbHelper;
    private boolean mStart = false;
    private boolean mFirst = false;
    private boolean mStop = false;
    private boolean mAsked = false;
    private Chronometer mChronometer;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // メンバ変数が初期化されることへの対処
        outState.putBoolean("ASKED", mAsked);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAsked = savedInstanceState.getBoolean("ASKED");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 画面をスリープにしない
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mDbHelper = new DatabaseHelper(this);
        ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton);
        tb.setChecked(false);

        // ToggleのCheckが変更したタイミングで呼び出されるリスナー
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // トグルキーが変更された際に呼び出される
                if (isChecked) {
                    startChronometer();
                    mStart = true;
                    mFirst = true;
                    mStop = false;
                    mMeter = 0.0;
                    mRunList.clear();
                } else {
                    stopChronometer();
                    mStop = true;
                    calcSpeed();
                    saveConfirm();
                    mStart = false;
                }
            }
        });
    }

    private void startChronometer() {
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        // 電源ON時からの経過時間の値をベースに
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mStartTimeMillis = System.currentTimeMillis();
    }

    private void stopChronometer() {
        mChronometer.stop();
        // ミリ秒
        elapsedTime = SystemClock.elapsedRealtime() - mChronometer.getBase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mAsked) {
            wifiConfirm();
            mAsked = !mAsked;
        }

        mGoogleApiClient.connect();
    }

    private void wifiConfirm() {
        mWifi = (WifiManager) getSystemService(WIFI_SERVICE);

        if (mWifi.isWifiEnabled()) {
            wifiConfirmDialog();
        }
    }

    private void wifiConfirmDialog() {
        DialogFragment newFragment = WifiConfirmDialogFragment.newInstance(
                R.string.wifi_confirm_dialog_title, R.string.wifi_confirm_dialog_message);
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void wifiOff() {
        mWifi.setWifiEnabled(false);
        mWifiOff = true;
    }

    private void calcSpeed() {
        sumDistance();
        mSpeed = (mMeter / 1000) / (elapsedTime / 1000) * 60 * 60;
    }

    private void saveConfirm(){
        // DangerousなPermissionはリクエストして許可をもらわないと使えない
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                // 一度拒否された時、Rationale（理論的根拠）を説明して、再度許可ダイアログを出すようにしている
                new AlertDialog.Builder(this)
                        .setTitle("許可が必要です")
                        .setMessage("ジョギングの記録を保存するには、WRITE_EXTERNAL_STORAGEを許可して下さい")
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OK button pressed
                                requestWriteExternalStorage();
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_cansel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showToast("外部へのファイルの保存が許可されなかったので、記録できません");
                            }
                        })
                        .show();
            } else {
                // まだ許可を求める前の時、許可を求めるダイアログを表示します。
                requestWriteExternalStorage();
            }
        } else {
            saveConfirmDialog();
        }
    }

    private void requestWriteExternalStorage() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    private void saveConfirmDialog() {
        String message = "時間：";
        TextView disText = (TextView) findViewById(R.id.disText);

        message = message + mChronometer.getText().toString() + " " +
                "距離" + disText.getText() + "\n" +
                "時速" + String.format("%.2f" + " km", mSpeed);

        DialogFragment newFragment = SaveConfirmDialogFragment.newInstance(
                R.string.save_confirm_dialog_title, message);

        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()){
            stopLocationUpdates();
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 自プログラムがオフにした場合はWiFiをオンにする処理
        if (mWifiOff){
            mWifi.setWifiEnabled(true);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Intent intent = new Intent(MapsActivity.this, JogView.class);
                startActivity(intent);
            }
        });

        // DangerousなPermissionはリクエストして許可をもらわないと使えない
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                // 一度拒否された時、Rationale（理論的根拠）を説明して、再度許可ダイアログを出すようにする
                new AlertDialog.Builder(this)
                        .setTitle("許可が必要です")
                        .setMessage("移動に合わせて地図を動かすためには、ACCESS_FINE_LOCATIONを許可して下さい")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OK button pressed
                                requestAccessFineLocation();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showToast("GPS機能が使えないので、地図は動きません");
                            }
                        })
                        .show();
            } else {
                // まだ許可を求める前の時、許可を求めるダイアログを表示します。
                requestAccessFineLocation();
            }
        }
    }

    private void showToast(String s) {
        Toast error = Toast.makeText(this, s, Toast.LENGTH_LONG);
        error.show();
    }

    private void requestAccessFineLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // ユーザーが許可した時
                // 許可が必要な機能を改めて実行する
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                } else {
                    // ユーザーが許可しなかったとき
                    // 許可されなかったため機能が実行出来ないことを表示する
                    showToast("GPS機能が使えないので、地図は動きません");
                }
                return;
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    saveConfirmDialog();
                } else {
                    showToast("外部へのファイルの保存が許可されなかったので、記録できません");
                }
                return;
        }
    }

    @Override
    public Loader<Address> onCreateLoader(int id, Bundle args) {
        double lat = args.getDouble("lat");
        double lng = args.getDouble("lng");
        return new AddressTaskLoader(this, lat, lng);
    }

    @Override
    public void onLoadFinished(Loader<Address> loader, Address result) {
        if (result != null){
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < result.getMaxAddressLineIndex() + 1; i++){
                String item = result.getAddressLine(i);
                if (item == null){
                    break;
                }

                sb.append(item);
            }
            TextView address = (TextView) findViewById(R.id.address);

            address.setText(sb.toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<Address> loader) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // stopは動かさない
        if (mStop){
            return;
        }
        CameraPosition cameraPos = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(19)
                .bearing(0).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

        // マーカー設定
        mMap.clear();
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions();
        options.position(latlng);
        // ランチャーアイコンを使う
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        options.icon(icon);
        mMap.addMarker(options);

        if (mStart){
            if (mFirst){
                Bundle args = new Bundle();
                args.putDouble("lat", location.getLatitude());
                args.putDouble("lng", location.getLongitude());

                getLoaderManager().restartLoader(ADDRESSLOADER_ID, args, this);
                mFirst = !mFirst;
            } else {
                // 移動線を描画
                drawTrace(latlng);
                // 素行距離を累積
                sumDistance();
            }
        }
    }

    private void sumDistance() {
        if (mRunList.size() < 2){
            return;
        }
        mMeter = 0;
        float[] results = new float[3];
        int i = 1;
        while (i < mRunList.size()){
            results[0] = 0;
            Location.distanceBetween(mRunList.get(i-1).latitude, mRunList.get(i-1).longitude,
                    mRunList.get(i).latitude, mRunList.get(i).longitude, results);
            mMeter += results[0];
            i++;
        }
        // distanceDetweenの距離はメートル単位
        double disMeter = mMeter / 1000;
        TextView disText = (TextView) findViewById(R.id.disText);
        disText.setText(String.format("%.2f" + " km", disMeter));
    }

    private void drawTrace(LatLng latlng) {
        mRunList.add(latlng);
        if (mRunList.size() > 2){
            PolylineOptions polylineOptions = new PolylineOptions();
            for (LatLng polyLatLng : mRunList){
                polylineOptions.add(polyLatLng);
            }
            polylineOptions.color(Color.BLUE);
            polylineOptions.width(3);
            polylineOptions.geodesic(false);
            mMap.addPolyline(polylineOptions);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Do nothing
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, REQUEST, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do nothing
    }

    public void savaJogViaCTP(){
        String strDate = new SimpleDateFormat("yyyy/MM/dd").format(mStartTimeMillis);

        TextView txtAddress = (TextView) findViewById(R.id.address);

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DATE, strDate);
        values.put(DatabaseHelper.COLUMN_ELAPSEDTIME, mChronometer.getText().toString());
        values.put(DatabaseHelper.COLUMN_DISTANCE, mMeter);
        values.put(DatabaseHelper.COLUMN_SPEED, mSpeed);
        values.put(DatabaseHelper.COLUMN_ADDRESS, txtAddress.getText().toString());
        Uri uri = getContentResolver().insert(JogRecordContentProvider.CONTENT_URI, values);
        Toast.makeText(this, "データを保存しました", Toast.LENGTH_SHORT).show();
    }

    public void saveJog(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String strDate = new SimpleDateFormat("yyyy/MM/dd").format(mStartTimeMillis);

        TextView txtAddress = (TextView) findViewById(R.id.address);

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DATE, strDate);
        values.put(DatabaseHelper.COLUMN_ELAPSEDTIME, mChronometer.getText().toString());
        values.put(DatabaseHelper.COLUMN_DISTANCE, mMeter);
        values.put(DatabaseHelper.COLUMN_SPEED, mSpeed);
        values.put(DatabaseHelper.COLUMN_ADDRESS, txtAddress.getText().toString());
        try {
            db.insert(DatabaseHelper.TABLE_JOGRECORD, null, values);
        } catch (Exception e){
            Toast.makeText(this, "データの保存に失敗しました", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }
}
