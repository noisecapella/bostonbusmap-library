package com.schneeloch.outside;

import com.almworks.sqlite4java.SQLite;
import com.google.common.collect.ImmutableList;
import com.schneeloch.schema.Schema;
import com.schneeloch.transitlib.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        SQLite.setLibraryPath("/home/georgeandroid/sqlite4java");

        DatabaseProvider databaseProvider = new DatabaseProvider();
        try {
            ITransitSource mbta = new NextbusTransitSource();

            ImmutableList<ITransitSource> sources = ImmutableList.of(mbta);
            TransitCache transitCache = new TransitCache();
            TransitSystem transitSystem = new TransitSystem(sources, transitCache);
            Iterable<Stop> nearbyStops = transitSystem.getStopsNear(databaseProvider, 42.3601f, -71.0589f).get();

            for (Stop stop : nearbyStops) {
                // System.out.println(stop.toString());
                System.out.println(stop.getTitle());
            }

            Iterable<Route> routes = transitSystem.getTransitSource(Schema.Routes.enumagencyidBus).getRoutes(databaseProvider, transitCache).get();
            for (Route route : routes) {
                System.out.println(route.getRouteTitle());
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        finally {
            try {
                databaseProvider.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
