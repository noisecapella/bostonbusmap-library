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

    private final String agency;

    public NextbusTransitSource(String agency) {
        this.agency = agency;
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
    public ListenableFuture<ImmutableTable<String, Integer, Route>> getRoutes(DatabaseProvider provider, TransitCache transitCache) throws Throwable {
        return transitCache.readRoutesBySourceId(provider, getSourceIds());
    }

    @Override
    public ListenableFuture<Map<String, ImmutableList<IPrediction>>> getPredictionsByStop(final TransitCache transitCache, IDownloader downloader, ExecutorService executorService, DatabaseProvider provider, List<Stop> stops) {
        Map<String, ImmutableList<IPrediction>> cachedPredictionsForStops = transitCache.getCachedPredictionsForStops(stops, fetchDelay);

        List<Stop> toRead = Lists.newArrayList();
        for (Stop stop : stops) {
            ImmutableList<IPrediction> predictions = cachedPredictionsForStops.get(stop.getStopId());
            if (predictions == null) {
                toRead.add(stop);
            }
        }

        NextbusPredictionsRequester requester = new NextbusPredictionsRequester(agency, downloader, executorService, transitCache, provider);
        ListenableFuture<Map<String, PredictionForStop>> predictions = requester.getPredictionsByStop(this, toRead);

        return Futures.transform(predictions, new Function<Map<String,PredictionForStop>, Map<String, ImmutableList<IPrediction>>>() {
            @Nullable
            @Override
            public Map<String, ImmutableList<IPrediction>> apply(Map<String, PredictionForStop> input) {
                transitCache.updateStops(input);
                Map<String, ImmutableList<IPrediction>> ret = Maps.newHashMap();
                for (Map.Entry<String, PredictionForStop> entry : input.entrySet()) {
                    ret.put(entry.getKey(), entry.getValue().getPredictionList());
                }
                return ret;
            }
        });
    }



    @Override
    public List<Integer> getSourceIds() {
        return Lists.newArrayList(Schema.Routes.enumagencyidBus);
    }

}
