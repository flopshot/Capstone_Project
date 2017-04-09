package com.sean.golfranger.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Marker Objects to Hold hash, elevation, latlon, distance to user, elevationDelta to user. Needs
 * To be saved in savedinstancestate bundle so we extend parcel to make bundleable
 */
public class GolfMarker implements Parcelable {
    private Double elevation;
    private double lat;
    private double lon;
    private Double elevationDelta;
    private Double distance;

    public GolfMarker(double[] latlon) {
        this.lat = latlon[0];
        this.lon = latlon[1];
    }

    public GolfMarker(Double[] latlon) {
        double[] t = wDoubleToDouble(latlon);
        this.lat = t[0];
        this.lon = t[1];
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    public Double getElevation() {
        return this.elevation;
    }

    public double[] getLatLon() {
        return new double[] {lat,lon};
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getDistance() {
        return this.distance;
    }

    public void setElevationDelta(Double elevationDelta) {
        this.elevationDelta = elevationDelta;
    }

    public Double getElevationDelta() {
        return this.elevationDelta;
    }

    //Private Constructor for internal CREATOR object for self initializing
    // once initialized from bundle from
    private GolfMarker(Parcel in){
        elevation = in.readDouble();
        lat = in.readDouble();
        lon = in.readDouble();
        elevationDelta = in.readDouble();
        distance = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(distance);
        parcel.writeDouble(elevation);
        parcel.writeDouble(elevationDelta);
        //   parcel.writeString(movieCat);
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
    }

    // Methods createFromParcel is the custom, internal
    // constructor method that uses private constructor
    // newArray method persist parcelables in arrays
    public static final Parcelable.Creator<GolfMarker> CREATOR = new Parcelable.Creator<GolfMarker>() {
        @Override
        public GolfMarker createFromParcel(Parcel parcel) {
            return new GolfMarker(parcel);
        }

        @Override
        public GolfMarker[] newArray(int size) {
            return new GolfMarker[size];
        }

    };


    private double[] wDoubleToDouble(Double[] latlon) {
        double[] tempArray = new double[latlon.length];
        int i = 0;
        for(Double d : latlon) {
            tempArray[i] = d;
            i++;
        }
        return tempArray;
    }

}
