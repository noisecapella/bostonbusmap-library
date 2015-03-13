package com.schneeloch.transitlib;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.schneeloch.outside.DatabaseProvider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by george on 3/1/15.
 */
public class TransitSystem {
    private final DatabaseProvider provider;
    private final ExecutorService service;
    private final ImmutableList<ITransitSource> sources;
    private final TransitCache transitCache;
    private final IDownloader downloader;

    public TransitSystem(ExecutorService service, DatabaseProvider provider, ImmutableList<ITransitSource> sources, TransitCache transitCache, IDownloader downloader) {
        this.sources = sources;
        this.transitCache = transitCache;
        this.provider = provider;
        this.service = service;
        this.downloader = downloader;
    }

    public ITransitSource getTransitSource(int sourceId) {
        for (ITransitSource source : sources) {
            for (int transitSourceId : source.getSourceIds()) {
                if (transitSourceId == sourceId) {
                    return source;
                }
            }
        }
        throw new RuntimeException("Unable to find transit source for id " + sourceId);
    }


    public ListenableFuture<Iterable<Stop>> getStopsNear(float lat, float lon) throws Throwable {
        List<ListenableFuture<List<Stop>>> ret = Lists.newArrayList();
        for (ITransitSource source : sources) {
            ret.add(source.getStopsNear(provider, transitCache, lat, lon));
        }

        ListenableFuture<List<List<Stop>>> list = Futures.allAsList(ret);
        return Futures.transform(list, new Function<List<List<Stop>>, Iterable<Stop>>() {
            @Nullable
            @Override
            public Iterable<Stop> apply(List<List<Stop>> input) {
                return Iterables.concat(input);
            }
        });
    }

    public ListenableFuture<Map<String, ImmutableList<IPrediction>>> getPredictionsByStop(List<Stop> stops) {
        List<ListenableFuture<Map<String, ImmutableList<IPrediction>>>> ret = Lists.newArrayList();
        for (ITransitSource source : sources) {
            ret.add(source.getPredictionsByStop(transitCache, downloader, service, provider, stops));
        }

        ListenableFuture<List<Map<String, ImmutableList<IPrediction>>>> list = Futures.allAsList(ret);
        return Futures.transform(list, new Function<List<Map<String, ImmutableList<IPrediction>>>, Map<String, ImmutableList<IPrediction>>>() {
            @Nullable
            @Override
            public Map<String, ImmutableList<IPrediction>> apply(List<Map<String, ImmutableList<IPrediction>>> input) {
                Map<String, ImmutableList<IPrediction>> ret = Maps.newHashMap();

                for (Map<String, ImmutableList<IPrediction>> map : input) {
                    for (Map.Entry<String, ImmutableList<IPrediction>> entry : map.entrySet()) {
                        ret.put(entry.getKey(), entry.getValue());
                    }
                }

                return ret;
            }
        });
    }

    public void stop() throws InterruptedException {
        service.shutdown();
        provider.stop();
    }
}
