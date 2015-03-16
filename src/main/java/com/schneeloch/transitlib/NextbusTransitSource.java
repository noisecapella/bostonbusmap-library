package com.schneeloch.transitlib;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.schneeloch.outside.DatabaseProvider;
import com.schneeloch.schema.Schema;
import com.schneeloch.transitlib.parser.NextbusPredictionsRequester;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by george on 3/1/15.
 */
public class NextbusTransitSource implements ITransitSource {
    public static final int fetchDelay = 13000;

    private final String _agency;

    public NextbusTransitSource(String agency) {
        _agency = agency;
    }

    @Override
    public List<Stop> readStops(Providers providers, TransitCache transitCache, List<String> toRead) throws Exception {
        return transitCache.readStops(providers, toRead);
    }

    @Override
    public List<Stop> getStopsNear(Providers providers, TransitCache transitCache, float lat, float lon) throws Exception {
        return transitCache.readStopsNear(providers, lat, lon);
    }

    @Override
    public ImmutableTable<String, Integer, Route> getRoutesBySourceId(Providers providers, TransitCache transitCache, Schema.Routes.SourceId sourceId) throws Exception {
        return transitCache.readRoutesBySourceId(providers, Lists.newArrayList(sourceId));
    }

    @Override
    public Map<String, ImmutableList<IPrediction>> getPredictionsByStop(Providers providers, final TransitCache transitCache, List<Stop> stops) throws Exception {
        Map<String, ImmutableList<IPrediction>> cachedPredictionsForStops = transitCache.getCachedPredictionsForStops(stops, fetchDelay);

        List<Stop> toRead = Lists.newArrayList();
        for (Stop stop : stops) {
            ImmutableList<IPrediction> predictions = cachedPredictionsForStops.get(stop.getStopId());
            if (predictions == null) {
                toRead.add(stop);
            }
        }

        NextbusPredictionsRequester requester = new NextbusPredictionsRequester(providers, transitCache, this, _agency);
        Map<String, PredictionForStop> predictions = requester.getPredictionsByStop(toRead);

        transitCache.updateStops(predictions);
        Map<String, ImmutableList<IPrediction>> ret = Maps.newHashMap();
        for (Map.Entry<String, PredictionForStop> entry : predictions.entrySet()) {
            ret.put(entry.getKey(), entry.getValue().getPredictionList());
        }
        return ret;
    }



    @Override
    public List<Schema.Routes.SourceId> getSourceIds() {
        return Lists.newArrayList(Schema.Routes.SourceId.Bus);
    }

}
