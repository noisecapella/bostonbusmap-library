package com.schneeloch.transitlib;

import java.util.Objects;

/**
 * Created by george on 3/9/15.
 */
public class NextbusPrediction implements IPrediction {
    private final String routeId;
    private final String vehicleId;
    private final String directionSnippet;
    private final boolean affectedByLayover;
    private final boolean isDelayed;
    private final String block;
    private final int minutes;

    public NextbusPrediction(String routeId, String vehicleId, String directionSnippet, boolean affectedByLayover, boolean isDelayed, String block, int minutes) {

        this.routeId = routeId;
        this.vehicleId = vehicleId;
        this.directionSnippet = directionSnippet;
        this.affectedByLayover = affectedByLayover;
        this.isDelayed = isDelayed;
        this.block = block;
        this.minutes = minutes;
    }

    public String asHtml() {
        return "Route: " + routeId + ", vehicle: " + vehicleId + "<br />\nIn " + minutes + " minutes";
    }
}
