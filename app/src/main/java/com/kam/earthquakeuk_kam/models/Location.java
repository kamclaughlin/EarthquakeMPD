/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.models;

import android.util.Log;

import java.io.Serializable;

public class Location implements Serializable {

    private String name;
    private double latitude;
    private double longitude;

    public Location() {
        this.name = "";
        this.latitude = 9999d;
        this.longitude = 9999d;
    }

    public Location(String name, double latitude, double longitude) {
        this.setName(name);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public Location(String name, String latlon) {
        this.parseLocation(name, latlon);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double lat) {
        this.latitude = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    private void parseLocation(String name, String latlong) {

        this.setName(name.replace(" ;", "").trim());

        try {
            this.setLatitude(Double.parseDouble(latlong.split(",")[0].replace(" ;", "").trim()));
            this.setLongitude(Double.parseDouble(latlong.split(",")[1].replace(" ;", "").trim()));
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }
}
