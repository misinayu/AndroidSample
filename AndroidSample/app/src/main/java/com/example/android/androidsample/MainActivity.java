package com.example.android.androidsample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
implements View.OnClickListener {
    EditText et_name;
    TextView tv_name, tv_1;
    SharedPreferences pref;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("text", tv_1.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        pref = getSharedPreferences("text_status", Context.MODE_PRIVATE);
        if (pref.contains("text_1")){
            tv_1.setText(pref.getString("text_1", ""));
        }
        if (pref.contains("text_name")){
            tv_name.setText(pref.getString("text_name", ""));
        }
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("text_1", tv_1.getText().toString());
        editor.putString("text_name", tv_name.getText().toString());
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String packageName = getPackageName();

        Button bt_ok = (Button) findViewById(R.id.button_ok);
        tv_1 = (TextView) findViewById(R.id.textView);
        bt_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText et_1 = (EditText) findViewById(R.id.editText);
                String text = et_1.getText().toString();
                tv_1.setText(text);
            }
        });

        Button bt_1 = (Button) findViewById(R.id.button_1);
        bt_1.setOnClickListener(this);

        Button bt_2 = (Button) findViewById(R.id.button_2);
        bt_2.setOnClickListener(this);

        Button bt_3 = (Button) findViewById(R.id.button_3);
        bt_3.setOnClickListener(this);

        Button bt_toSub = (Button) findViewById(R.id.bt_toSub);
        bt_toSub.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(packageName, packageName + ".SubActivity");
//                startActivity(intent);
                startActivityForResult(intent, 0);
            }
        });

        Button bt_toSubWithData = (Button) findViewById(R.id.button_toSubWithData);
        bt_toSubWithData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(packageName, packageName + ".SubActivity");

                EditText et_message = (EditText) findViewById(R.id.editText);
                String message = et_message.getText().toString();
                intent.putExtra("message", message);
                startActivity(intent);
            }
        });

        et_name = (EditText) findViewById(R.id.editText_name);
        tv_name = (TextView) findViewById(R.id.textView_name);

        if (savedInstanceState != null) {
            String text = savedInstanceState.getString("text");
            tv_1.setText(text);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                String button = data.getStringExtra("button_text");
                Toast.makeText(this, button+" ボタンが押されました", Toast.LENGTH_LONG).show();
            }else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "キャンセルされました", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        ImageView iv_droid1 = (ImageView) findViewById(R.id.imageView_droid1);

        switch (v.getId()){
            case R.id.button_1:
                iv_droid1.setImageResource(R.drawable.android_logo);
                break;
            case R.id.button_2:
                iv_droid1.setImageResource(R.drawable.android_logo_red);
                break;
            case R.id.button_3:
                iv_droid1.setImageResource(R.drawable.android_logo_blue);
                break;
        }
    }

//    class OkButtonClickListener implements View.OnClickListener{
//
//        @Override
//        public void onClick(View v){
//            EditText et_1 = (EditText) findViewById(R.id.editText);
//            TextView tv_1 = (TextView) findViewById(R.id.textView);
//            String text = et_1.getText().toString();
//            tv_1.setText(text);
//        }
//    }

    public void inputName(View v){
        String name = et_name.getText().toString();
        tv_name.setText(name);
    }
}
