package com.example.masanori_acer.orientationget;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {
    // センサーマネージャー
    private SensorManager mSensorManager;
    private Sensor mMagField;
    private Sensor mAccelerometer;

    private static final int MATRIX_SIZE = 16;
    // センサーの値
    private float[] mgValues = new float[3];
    private float[] acValues = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagField);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView txt01 = (TextView)findViewById(R.id.txt01);
        float[] inR = new float[MATRIX_SIZE];
        float[] outR = new float[MATRIX_SIZE];
        float[] I = new float[MATRIX_SIZE];
        float[] orValues = new float[3];

        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                acValues = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mgValues = event.values.clone();
                break;
        }

        if (mgValues != null && acValues != null){
            SensorManager.getRotationMatrix(inR, I, acValues, mgValues);

            // 実機を水平に持ち、アクティビティはポートレイト
            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
            SensorManager.getOrientation(outR, orValues);

            StringBuilder strBuild = new StringBuilder();
            strBuild.append("方位角（アジマス）");
            strBuild.append(rad2Deg(orValues[0]));
            strBuild.append("\n");
            strBuild.append("傾斜角（ピッチ）");
            strBuild.append(rad2Deg(orValues[1]));
            strBuild.append("\n");
            strBuild.append("回転角（ロール）");
            strBuild.append(rad2Deg(orValues[2]));
            strBuild.append("\n");

            txt01.setText(strBuild.toString());
        }
    }
    private int rad2Deg(float rad){
        return (int)Math.floor(Math.toDegrees(rad));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
