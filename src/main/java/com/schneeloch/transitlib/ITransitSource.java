package com.schneeloch.transitlib;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.util.concurrent.ListenableFuture;
import com.schneeloch.outside.DatabaseProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by george on 3/1/15.
 */
public interface ITransitSource {
    List<Stop> readStops(DatabaseProvider provider, TransitCache transitCache, List<String> toRead) throws Exception;

    List<Stop> getStopsNear(DatabaseProvider provider, TransitCache transitCache, float lat, float lon) throws Exception;

    ImmutableTable<String, Integer, Route> getRoutes(DatabaseProvider provider, TransitCache transitCache) throws Exception;

    Map<String, ImmutableList<IPrediction>> getPredictionsByStop(TransitCache transitCache, IDownloader downloader, ExecutorService executorService, DatabaseProvider provider, List<Stop> stops) throws Exception;

    Collection<Integer> getSourceIds();

}
