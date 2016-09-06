package com.example.android.androidsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
implements View.OnClickListener {
    EditText et_name;
    TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt_ok = (Button) findViewById(R.id.button_ok);
        bt_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText et_1 = (EditText) findViewById(R.id.editText);
                TextView tv_1 = (TextView) findViewById(R.id.textView);
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

        et_name = (EditText) findViewById(R.id.editText_name);
        tv_name = (TextView) findViewById(R.id.textView_name);
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
