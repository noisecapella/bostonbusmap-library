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
    public List<Stop> readStops(DatabaseProvider provider, TransitCache transitCache, List<String> toRead) throws Exception {
        return transitCache.readStops(provider, toRead);
    }

    @Override
    public List<Stop> getStopsNear(DatabaseProvider provider, TransitCache transitCache, float lat, float lon) throws Exception {
        return transitCache.readStopsNear(provider, lat, lon);
    }

    @Override
    public ImmutableTable<String, Integer, Route> getRoutes(DatabaseProvider provider, TransitCache transitCache) throws Exception {
        return transitCache.readRoutesBySourceId(provider, getSourceIds());
    }

    @Override
    public Map<String, ImmutableList<IPrediction>> getPredictionsByStop(final TransitCache transitCache, IDownloader downloader, ExecutorService executorService, DatabaseProvider provider, List<Stop> stops) throws Exception {
        Map<String, ImmutableList<IPrediction>> cachedPredictionsForStops = transitCache.getCachedPredictionsForStops(stops, fetchDelay);

        List<Stop> toRead = Lists.newArrayList();
        for (Stop stop : stops) {
            ImmutableList<IPrediction> predictions = cachedPredictionsForStops.get(stop.getStopId());
            if (predictions == null) {
                toRead.add(stop);
            }
        }

        NextbusPredictionsRequester requester = new NextbusPredictionsRequester(_agency, downloader, executorService, transitCache, provider);
        Map<String, PredictionForStop> predictions = requester.getPredictionsByStop(this, toRead);

        transitCache.updateStops(predictions);
        Map<String, ImmutableList<IPrediction>> ret = Maps.newHashMap();
        for (Map.Entry<String, PredictionForStop> entry : predictions.entrySet()) {
            ret.put(entry.getKey(), entry.getValue().getPredictionList());
        }
        return ret;
    }



    @Override
    public List<Integer> getSourceIds() {
        return Lists.newArrayList(Schema.Routes.enumagencyidBus);
    }

}
