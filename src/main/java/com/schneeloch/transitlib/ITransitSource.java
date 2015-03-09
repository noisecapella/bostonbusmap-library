package com.schneeloch.transitlib;

import com.google.common.util.concurrent.ListenableFuture;
import com.schneeloch.outside.DatabaseProvider;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by george on 3/1/15.
 */
public interface ITransitSource {
    ListenableFuture<List<Stop>> readStops(DatabaseProvider provider, TransitCache transitCache, List<String> toRead) throws Throwable;

    ListenableFuture<List<Stop>> getStopsNear(DatabaseProvider provider, TransitCache transitCache, float lat, float lon) throws Throwable;

    ListenableFuture<List<Route>> getRoutes(DatabaseProvider provider, TransitCache transitCache) throws Throwable;

    Collection<Integer> getSourceIds();
}
