package com.schneeloch.outside;

import com.almworks.sqlite4java.SQLite;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.schneeloch.schema.Schema;
import com.schneeloch.transitlib.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        SQLite.setLibraryPath("/home/georgeandroid/sqlite4java");

        DatabaseProvider databaseProvider = new DatabaseProvider();
        TransitSystem transitSystem = null;
        try {
            ITransitSource mbta = new NextbusTransitSource("mbta");

            // TODO: organize this mess
            ImmutableList<ITransitSource> sources = ImmutableList.of(mbta);
            TransitCache transitCache = new TransitCache();
            ExecutorService service = Executors.newCachedThreadPool();
            Downloader downloader = new Downloader();
            transitSystem = new TransitSystem(service, databaseProvider, sources, transitCache, downloader);
            List<Stop> nearbyStops = Lists.newArrayList(transitSystem.getStopsNear(42.3601f, -71.0589f));

            for (Stop stop : nearbyStops) {
                // System.out.println(stop.toString());
                System.out.println(stop.getTitle());
            }

            ImmutableTable<String, Integer, Route> routes = transitSystem.getTransitSource(Schema.Routes.enumagencyidBus).getRoutes(databaseProvider, transitCache);
            for (Table.Cell<String, Integer, Route> route : routes.cellSet()) {
                System.out.println(route.getValue().getRouteTitle());
            }

            Map<String, ImmutableList<IPrediction>> entries = transitSystem.getPredictionsByStop(nearbyStops);
            for (Map.Entry<String, ImmutableList<IPrediction>> entry : entries.entrySet()) {
                System.out.println(entry.getKey());
                for (IPrediction prediction : entry.getValue()) {
                    System.out.println(prediction.asHtml() + "\n");
                }
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        finally {
            try {
                if (transitSystem != null) {
                    transitSystem.stop();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
