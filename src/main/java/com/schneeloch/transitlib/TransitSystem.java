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


    public Iterable<Stop> getStopsNear(float lat, float lon) throws Throwable {
        List<Stop> ret = Lists.newArrayList();
        for (ITransitSource source : sources) {
            ret.addAll(source.getStopsNear(provider, transitCache, lat, lon));
        }

        return ret;
    }

    public Map<String, ImmutableList<IPrediction>> getPredictionsByStop(List<Stop> stops) throws Exception {
        Map<String, ImmutableList<IPrediction>> ret = Maps.newHashMap();
        for (ITransitSource source : sources) {
            Map<String, ImmutableList<IPrediction>> map = source.getPredictionsByStop(transitCache, downloader, service, provider, stops);
            ret.putAll(map);
        }

        return ret;
    }

    public void stop() throws InterruptedException {
        service.shutdown();
        provider.stop();
    }
}
