package com.example.masanori_acer.anbayasiroulette;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.cardList);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llManager = new LinearLayoutManager(this);
        // 縦スクロール
        llManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llManager);

        ArrayList<AnbayasiData> anbayasi = new ArrayList<AnbayasiData>();
        for(int i = 0; i < MyData.commentArray.length; i++){
            anbayasi.add(new AnbayasiData(
                    MyData.numberArray[i],
                    MyData.additionArray[i],
                    MyData.commentArray[i]
            ));
        }

        RecyclerView.Adapter adapter = new AnbayasiAdapter(anbayasi);
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(anbayasi.size() -1); // 最後までスクロール
    }
}
