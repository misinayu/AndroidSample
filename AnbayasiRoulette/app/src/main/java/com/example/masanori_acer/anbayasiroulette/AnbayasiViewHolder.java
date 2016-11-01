package com.example.masanori_acer.anbayasiroulette;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by MASANORI on 2016/10/28.
 */

public class AnbayasiViewHolder extends RecyclerView.ViewHolder {
    View base;
    TextView textViewNumber;
    TextView textViewComment;

    public AnbayasiViewHolder(View view){
        super(view);
        this.base = view;
        this.textViewNumber = (TextView) view.findViewById(R.id.number);
        this.textViewComment = (TextView) view.findViewById(R.id.comment);
    }
}
