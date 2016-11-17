package com.example.masanori_acer.parsecsv;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv01 = (TextView)findViewById(R.id.txt01);
        AssetManager assetManager = getResources().getAssets();
        try{
            InputStream is = assetManager.open("output.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            StringBuilder strBuild = new StringBuilder();
            while ((line =bufferedReader.readLine()) != null){
                StringTokenizer st = new StringTokenizer(line, ",");
                while (st.hasMoreTokens()){
                    strBuild.append(st.nextToken());
                    strBuild.append(",");
                }
                strBuild.append("\n");
            }
            tv01.setText(strBuild.toString());
            bufferedReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
