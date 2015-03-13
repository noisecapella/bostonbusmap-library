package com.schneeloch.transitlib;

import com.almworks.sqlite4java.SQLiteException;
import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.common.util.concurrent.*;
import com.schneeloch.outside.DatabaseProvider;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by george on 3/1/15.
 */
public class TransitCache {
    private final ConcurrentMap<String, Stop> stops = Maps.newConcurrentMap();
    private final ConcurrentMap<String, PredictionForStop> predictions = Maps.newConcurrentMap();
    private ImmutableTable<String, Integer, Route> routes = ImmutableTable.of();

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

    private static <T> ListenableFutureTask<T> createTask(final T t) {
        ListenableFutureTask<T> task = ListenableFutureTask.create(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return t;
            }
        });
        MoreExecutors.directExecutor().execute(task);
        return task;
    }

    public ListenableFuture<ImmutableTable<String, Integer, Route>> readRoutesBySourceId(DatabaseProvider provider, List<Integer> sourceIds) throws Exception {
        if (routes.size() > 0) {
            return createTask(routes);
        }

        return Futures.transform(provider.getRoutesBySourceId(sourceIds), new Function<List<Route>, ImmutableTable<String, Integer, Route>>() {
            @Nullable
            @Override
            public ImmutableTable<String, Integer, Route> apply(List<Route> input) {
                ImmutableTable.Builder<String, Integer, Route> builder = ImmutableTable.builder();
                for (Route route : input) {
                    builder.put(route.getRoute(), route.getSourceId(), route);
                }

                routes = builder.build();
                return routes;
            }
        });
    }

    public Map<String, ImmutableList<IPrediction>> getCachedPredictionsForStops(List<Stop> stopIds, long fetchDelay) {
        Map<String, ImmutableList<IPrediction>> ret = Maps.newHashMap();

        long currentMillis = System.currentTimeMillis();
        for (Stop stop : stopIds) {
            PredictionForStop predictionForStop = predictions.get(stop.getStopId());
            if (predictionForStop != null && predictionForStop.getLastUpdate() + fetchDelay >= currentMillis) {
                ret.put(stop.getStopId(), predictionForStop.getPredictionList());
            }
        }

        return ret;
    }

    public void updateStops(Map<String, PredictionForStop> stops) {
        for (Map.Entry<String, PredictionForStop> entry : stops.entrySet()) {
            PredictionForStop predictionsForStop = entry.getValue();
            String stopId = entry.getKey();

            predictions.put(stopId, predictionsForStop);
        }
    }
}
