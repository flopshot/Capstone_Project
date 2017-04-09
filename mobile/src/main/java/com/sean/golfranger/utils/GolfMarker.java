package com.sean.golfranger.utils;

/**
 * Marker Objects to Hold hash, elevation, latlon
 */
public class GolfMarker {
    public Double elevation;
    public Double[] latlon;

    public GolfMarker(Double[] latlon) {
        this.latlon = latlon;

    }

}
