package com.schneeloch.transitlib;

import com.almworks.sqlite4java.SQLiteException;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.schneeloch.outside.DatabaseProvider;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by george on 3/1/15.
 */
public class TransitCache {
    private final ConcurrentHashMap<String, Stop> stops;

    public TransitCache() {
        stops = new ConcurrentHashMap<>();
    }

    public ListenableFuture<List<Stop>> readStops(DatabaseProvider provider, List<String> stopIds) throws Throwable {
        final List<Stop> ret = Lists.newArrayList();

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

        ListenableFuture<List<Stop>> read = provider.readStops(toRead);
        return Futures.transform(read, new Function<List<Stop>, List<Stop>>() {
            @Nullable
            @Override
            public List<Stop> apply(List<Stop> input) {
                for (Stop stop : input) {
                    stops.put(stop.getStopId(), stop);
                    ret.add(stop);
                }

                return ret;
            }
        });
    }

    public ListenableFuture<List<Stop>> readStopsNear(DatabaseProvider provider, float lat, float lon) throws Throwable {
        List<String> stopIds = provider.getStopIdsNear(lat, lon).get();

        return readStops(provider, stopIds);
    }

    public ListenableFuture<List<Route>> readRoutesBySourceId(DatabaseProvider provider, List<Integer> sourceIds) throws Throwable {
        return provider.getRoutesBySourceId(sourceIds);
    }
}
