package com.example.masanori_acer.acceleget;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) { // センサーの値が変更されるたびに呼び出される
        StringBuilder strBuild = new StringBuilder();

        strBuild.append("X軸");
        strBuild.append(event.values[0]);
        strBuild.append("\n");
        strBuild.append("Y軸");
        strBuild.append(event.values[1]);
        strBuild.append("\n");
        strBuild.append("Z軸");
        strBuild.append(event.values[2]);
        strBuild.append("\n");

        TextView txt01 = (TextView)findViewById(R.id.txt01);
        txt01.setText(strBuild.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { // センサーの精度が変更されると呼び出される

    }
}
