package com.schneeloch.transitlib;

import java.io.IOException;

/**
 * Created by george on 3/1/15.
 */
public class Route {
    private final String route;
    private final String routeTitle;
    private final Path path;

    public Route(String route, String routeTitle, int color, byte[] blob) throws IOException {

        this.route = route;
        this.routeTitle = routeTitle;
        Box box = new Box(blob, 1);
        this.path = new Path(box, color);
    }

    public String getRouteTitle() {
        return routeTitle;
    }

    public String getRoute() {
        return route;
    }

    public Path getPath() {
        return path;
    }
}
