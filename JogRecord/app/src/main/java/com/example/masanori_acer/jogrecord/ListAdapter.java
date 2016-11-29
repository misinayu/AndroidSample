package com.example.masanori_acer.jogrecord;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by MASANORI on 2016/11/22.
 */

public class ListAdapter extends CursorAdapter {

    public ListAdapter(Context context, Cursor c, int flag) {
        super(context, c, flag);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Cursorからデータを取り出す
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
        String elapsedTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ELAPSEDTIME));
        double distance = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DISTANCE));
        double speed = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SPEED));
        String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));

        TextView tvId = (TextView) view.findViewById(R.id._id);
        TextView tvDate = (TextView) view.findViewById(R.id.tviewDate);
        TextView tvElapsedTime = (TextView) view.findViewById(R.id.tviewElapseTime);
        TextView tvDistance = (TextView) view.findViewById(R.id.tviewDistance);
        TextView tvSpeed = (TextView) view.findViewById(R.id.tviewSpeed);
        TextView tvPlace = (TextView) view.findViewById(R.id.tviewAddress);

        tvId.setText(String.valueOf(id));
        tvDate.setText(date);
        tvElapsedTime.setText(elapsedTime);
        tvDistance.setText(String.format("%.2f", distance/1000));
        tvSpeed.setText(String.format("%.2f", speed));
        tvPlace.setText(address);
    }
}
