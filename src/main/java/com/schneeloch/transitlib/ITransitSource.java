package com.schneeloch.transitlib;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.ListenableFuture;
import com.schneeloch.outside.DatabaseProvider;
import com.schneeloch.schema.Schema;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by george on 3/1/15.
 */
public interface ITransitSource {
    List<Stop> readStops(Providers providers, TransitCache transitCache, List<String> toRead) throws Exception;

    List<Stop> getStopsNear(Providers providers, TransitCache transitCache, float lat, float lon) throws Exception;

    Map<String, ImmutableList<IPrediction>> getPredictionsByStop(Providers providers, TransitCache transitCache, List<Stop> stops) throws Exception;

    List<Schema.Routes.SourceId> getSourceIds();

    ImmutableTable<String, Integer, Route> getRoutesBySourceId(Providers providers, TransitCache transitCache, Schema.Routes.SourceId sourceId) throws Exception;
}
