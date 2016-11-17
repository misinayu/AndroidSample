package com.example.masanori_acer.parsexml;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv01 = (TextView)findViewById(R.id.txt01);
        String str = parseSabae() + parseNonoichi();
        tv01.setText(str);
    }

    // 鯖江市の避難所のデータを取得
    public String parseSabae(){
        StringBuilder stringBuilder = new StringBuilder();
        String type = "";
        String name = "";
        String lat = "";
        String lng = "";

        AssetManager assetManager = getResources().getAssets();
        try {
            // XMLファイルのストリーム情報を取得
            InputStream is = assetManager.open("sabae.xml");
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStreamReader);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if ("type".equals(tag)){
                            type = parser.nextText();
                        } else if ("name".equals(tag)){
                            name = parser.nextText();
                        } else if ("latitude".equals(tag)){
                            lat = parser.nextText();
                        } else if ("longitude".equals(tag)){
                            lng = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if ("refuge".equals(endTag)){
                            stringBuilder.append(name);
                            stringBuilder.append(" ");
                            stringBuilder.append(type);
                            stringBuilder.append(" ");
                            stringBuilder.append(lat);
                            stringBuilder.append(" ");
                            stringBuilder.append(lng);
                            stringBuilder.append("\n");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                }
                eventType = parser.next();
            }
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    // 野々市の避難所のデータを取得
    public String parseNonoichi(){
        StringBuilder strBuild = new StringBuilder();

        AssetManager assetManager = getResources().getAssets();
        try{
            InputStream is = assetManager.open("nonoichi.xml");
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStreamReader);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if ("marker".equals(tag)){
                            strBuild.append(parser.getAttributeValue(null, "title"));
                            strBuild.append(",");
                            strBuild.append(parser.getAttributeValue(null, "lat"));
                            strBuild.append(",");
                            strBuild.append(parser.getAttributeValue(null, "lng"));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if ("marker".equals(endTag)){
                            strBuild.append("\n");
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return strBuild.toString();
    }
}
