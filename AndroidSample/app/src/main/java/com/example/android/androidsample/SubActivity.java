package com.example.android.androidsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by MASANORI on 2016/09/06.
 */
public class SubActivity extends AppCompatActivity
implements View.OnClickListener{
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        intent = getIntent();
        String message = intent.getStringExtra("message");
        if(message != null){
            if(message.length() > 0){
                Toast.makeText(SubActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        Button bt_a = (Button) findViewById(R.id.button_a);
        bt_a.setOnClickListener(this);

        Button bt_b = (Button) findViewById(R.id.button_b);
        bt_b.setOnClickListener(this);

        Button bt_cancel = (Button) findViewById(R.id.button_cancel);
        bt_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_a:
                intent.putExtra("button_text", "A");
                setResult(Activity.RESULT_OK, intent);
                finish();
            case R.id.button_b:
                intent.putExtra("button_text", "B");
                setResult(Activity.RESULT_OK, intent);
                finish();
            case R.id.button_cancel:
                setResult(Activity.RESULT_CANCELED);
                finish();
        }
    }
}
