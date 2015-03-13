package com.schneeloch.transitlib;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by george on 3/1/15.
 */
public class Stop {
    private String stopId;
    private String title;
    private float lat;
    private float lon;
    private final ImmutableList<String> routeIds;

    public static class Builder {
        private String stopId;
        private String title;
        private float lat;
        private float lon;
        private final ImmutableList.Builder<String> routeIds = ImmutableList.builder();

        public Builder stopId(String stopId) {
            this.stopId = stopId;
            return this;
        }

        public Builder stopTitle(String stopTitle) {
            this.title = stopTitle;
            return this;
        }

        public Builder lat(float lat) {
            this.lat = lat;
            return this;
        }

        public Builder lon(float lon) {
            this.lon = lon;
            return this;
        }

        public Builder addRouteId(String routeId) {
            routeIds.add(routeId);
            return this;
        }

        public Stop build() {
            return new Stop(stopId, title, lat, lon, routeIds.build());
        }
    }

    private Stop(String stopId, String title, float lat, float lon, ImmutableList<String> routeIds) {
        this.stopId = stopId;
        this.title = title;
        this.lat = lat;
        this.lon = lon;
        this.routeIds = routeIds;
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

    public ImmutableList<String> getRouteIds() {
        return routeIds;
    }
}
