package com.schneeloch.transitlib;

import com.almworks.sqlite4java.SQLiteException;
import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.common.util.concurrent.*;
import com.schneeloch.outside.DatabaseProvider;
import com.schneeloch.schema.Schema;

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
    private final ConcurrentMap<String, Stop> _stops = Maps.newConcurrentMap();
    private final ConcurrentMap<String, PredictionForStop> _predictions = Maps.newConcurrentMap();
    private ImmutableTable<String, Integer, Route> _routes = ImmutableTable.of();

    public List<Stop> readStops(Providers providers, List<String> stopIds) throws Exception {
        final List<Stop> ret = Lists.newArrayList();

        List<String> toRead = Lists.newArrayList();
        for (String stopId : stopIds) {
            Stop stop = _stops.get(stopId);
            if (stop != null) {
                ret.add(stop);
            } else {
                toRead.add(stopId);
            }
        }

        IDatabaseProvider databaseProvider = providers.getDatabaseProvider();
        List<Stop> read = databaseProvider.readStops(toRead);

        for (Stop stop : read) {
            _stops.put(stop.getStopId(), stop);
            ret.add(stop);
        }

        return ret;
    }

    public List<Stop> readStopsNear(Providers providers, float lat, float lon) throws Exception {
        IDatabaseProvider databaseProvider = providers.getDatabaseProvider();
        List<String> stopIds = databaseProvider.getStopIdsNear(lat, lon);

        return readStops(providers, stopIds);
    }

    public ImmutableTable<String, Integer, Route> readRoutesBySourceId(Providers providers, List<Schema.Routes.SourceId> sourceIds) throws Exception {
        if (_routes.size() > 0) {
            return _routes;
        }

        IDatabaseProvider provider = providers.getDatabaseProvider();
        List<Route> routes = provider.getRoutesBySourceId(sourceIds);
        ImmutableTable.Builder<String, Integer, Route> builder = ImmutableTable.builder();
        for (Route route : routes) {
            builder.put(route.getRoute(), route.getSourceId(), route);
        }

        _routes = builder.build();
        return _routes;
    }

    public Map<String, ImmutableList<IPrediction>> getCachedPredictionsForStops(List<Stop> stopIds, long fetchDelay) throws Exception {
        Map<String, ImmutableList<IPrediction>> ret = Maps.newHashMap();

        long currentMillis = System.currentTimeMillis();
        for (Stop stop : stopIds) {
            PredictionForStop predictionForStop = _predictions.get(stop.getStopId());
            if (predictionForStop != null && predictionForStop.getLastUpdate() + fetchDelay >= currentMillis) {
                ret.put(stop.getStopId(), predictionForStop.getPredictionList());
            }
        }

        return ret;
    }

    public void updateStops(Map<String, PredictionForStop> stops) throws Exception {
        for (Map.Entry<String, PredictionForStop> entry : stops.entrySet()) {
            PredictionForStop predictionsForStop = entry.getValue();
            String stopId = entry.getKey();

            _predictions.put(stopId, predictionsForStop);
        }
    }

}
