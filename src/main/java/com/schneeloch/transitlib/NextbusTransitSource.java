package com.schneeloch.transitlib;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.schneeloch.outside.DatabaseProvider;
import com.schneeloch.schema.Schema;

import java.util.List;

/**
 * Created by george on 3/1/15.
 */
public class NextbusTransitSource implements ITransitSource {
    public NextbusTransitSource() {

    }

    @Override
    public ListenableFuture<List<Stop>> readStops(DatabaseProvider provider, TransitCache transitCache, List<String> toRead) throws Throwable {
        return transitCache.readStops(provider, toRead);
    }

    @Override
    public ListenableFuture<List<Stop>> getStopsNear(DatabaseProvider provider, TransitCache transitCache, float lat, float lon) throws Throwable {
        return transitCache.readStopsNear(provider, lat, lon);
    }

    @Override
    public ListenableFuture<List<Route>> getRoutes(DatabaseProvider provider, TransitCache transitCache) throws Throwable {
        return transitCache.readRoutesBySourceId(provider, getSourceIds());
    }

    @Override
    public List<Integer> getSourceIds() {
        return Lists.newArrayList(Schema.Routes.enumagencyidBus);
    }
}
