package com.example.masanori_acer.jogrecord;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by MASANORI on 2016/11/22.
 */

public class AddressTaskLoader extends AsyncTaskLoader {
    private Geocoder mGeocoder = null;
    private double mLat;
    private double mLng;

    public AddressTaskLoader(Context context, double lat, double lng) {
        super(context);

        mGeocoder = new Geocoder(context, Locale.getDefault());
        mLat = lat;
        mLng = lng;
    }

    @Override
    public Object loadInBackground() {
        Address result = null;
        try {
            List<Address> results = mGeocoder.getFromLocation(mLat, mLng, 1);
            if (results != null && !results.isEmpty()){
                result = results.get(0);
            }
        } catch (IOException e){
            Log.e("AddressTaskLoader", e.getMessage());
        }
        return result;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
