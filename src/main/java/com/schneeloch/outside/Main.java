package com.schneeloch.outside;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteQueue;
import com.google.common.collect.ImmutableList;
import com.schneeloch.transitlib.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        SQLite.setLibraryPath("/home/georgeandroid/sqlite4java");

        DatabaseProvider databaseProvider = new DatabaseProvider();
        try {
            ITransitSource mbta = new NextbusTransitSource();

            ImmutableList<ITransitSource> sources = ImmutableList.of(mbta);
            StopCache stopCache = new StopCache();
            TransitSystem transitSystem = new TransitSystem(sources, stopCache);
            List<Stop> nearbyStops = transitSystem.getStopsNear(databaseProvider, 42.3601f, -71.0589f);

            for (Stop stop : nearbyStops) {
                // System.out.println(stop.toString());
                System.out.println(stop.getTitle());
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
