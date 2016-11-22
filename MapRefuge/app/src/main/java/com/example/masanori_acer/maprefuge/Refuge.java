package com.example.masanori_acer.maprefuge;

/**
 * Created by MASANORI on 2016/11/17.
 */

public class Refuge {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private boolean near;
    private int distance;

    public Refuge(String name, String address, double latitude, double longitude){
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.near = false;
        this.distance = 0;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return latitude;
    }

    public double getLng() {
        return longitude;
    }

    public boolean isNear(){
        return near;
    }

    public int getDistance() {
        return distance;
    }

    public void setNear(boolean near) {
        this.near = near;
    }

    public void setDistance(float distance) {
        this.distance = (int) distance;
    }
}
