package com.schneeloch.transitlib;

/**
 * Created by george on 3/1/15.
 */
public class Stop {
    private String stopId;
    private String title;
    private float lat;
    private float lon;

    public Stop(String stopId, String title, float lat, float lon) {
        this.stopId = stopId;
        this.title = title;
        this.lat = lat;
        this.lon = lon;
    }

    public String getTitle() {
        return title;
    }

    public String getStopId() {
        return stopId;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
