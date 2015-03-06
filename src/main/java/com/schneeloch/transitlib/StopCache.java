package com.schneeloch.transitlib;

import com.almworks.sqlite4java.SQLiteException;
import com.google.common.collect.Lists;
import com.schneeloch.outside.DatabaseProvider;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by george on 3/1/15.
 */
public class StopCache {
    private final ConcurrentHashMap<String, Stop> stops;

    public StopCache() {
        stops = new ConcurrentHashMap<>();
    }

    public List<Stop> readStops(DatabaseProvider provider, List<String> stopIds) throws SQLiteException, ExecutionException, InterruptedException {
        List<Stop> ret = Lists.newArrayList();

        List<String> toRead = Lists.newArrayList();
        for (String stopId : stopIds) {
            Stop stop = stops.get(stopId);
            if (stop != null) {
                ret.add(stop);
            }
            else {
                toRead.add(stopId);
            }
        }

        List<Stop> read = provider.readStops(toRead).get();
        for (Stop stop : read) {
            stops.put(stop.getStopId(), stop);
        }
        ret.addAll(read);

        return ret;
    }

    public List<Stop> readStopsNear(DatabaseProvider provider, float lat, float lon) throws SQLiteException, ExecutionException, InterruptedException {
        List<String> stopIds = provider.getStopIdsNear(lat, lon).get();
        return readStops(provider, stopIds);
    }
}
