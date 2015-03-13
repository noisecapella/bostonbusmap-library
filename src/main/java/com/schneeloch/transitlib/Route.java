package com.schneeloch.transitlib;

import java.io.IOException;

/**
 * Created by george on 3/1/15.
 */
public class Route {
    private final String route;
    private final String routeTitle;
    private final int sourceId;

    public Route(String route, String routeTitle, int sourceId) throws IOException {

        this.route = route;
        this.routeTitle = routeTitle;
        this.sourceId = sourceId;
    }

    public String getRouteTitle() {
        return routeTitle;
    }

    public String getRoute() {
        return route;
    }

    public Integer getSourceId() {
        return sourceId;
    }
}
