package com.schneeloch.transitlib;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.schneeloch.outside.DatabaseProvider;
import com.schneeloch.schema.Schema;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by george on 3/1/15.
 */
public class TransitSystem {
    private final ImmutableList<ITransitSource> _sources;
    private final ImmutableMap<Schema.Routes.SourceId, ITransitSource> _sourceLookup;
    private final TransitCache _transitCache;

    public TransitSystem(ImmutableList<ITransitSource> sources, TransitCache transitCache) {
        this._sources = sources;
        this._transitCache = transitCache;

        ImmutableMap.Builder<Schema.Routes.SourceId, ITransitSource> builder = ImmutableMap.builder();
        for (ITransitSource source : sources) {
            for (Schema.Routes.SourceId sourceId : source.getSourceIds()) {
                builder.put(sourceId, source);
            }
        }
        _sourceLookup = builder.build();
    }

    public Iterable<Stop> getStopsNear(Providers providers, float lat, float lon) throws Throwable {
        List<Stop> ret = Lists.newArrayList();
        for (ITransitSource source : _sources) {
            ret.addAll(source.getStopsNear(providers, _transitCache, lat, lon));
        }

        return ret;
    }

    public Map<String, ImmutableList<IPrediction>> getPredictionsByStop(Providers providers, TransitCache transitCache, List<Stop> stops) throws Exception {
        Map<String, ImmutableList<IPrediction>> ret = Maps.newHashMap();
        for (ITransitSource source : _sources) {
            Map<String, ImmutableList<IPrediction>> map = source.getPredictionsByStop(providers, transitCache, stops);
            ret.putAll(map);
        }

        return ret;
    }

    public ImmutableTable<String, Integer, Route> getRoutesByTransitSources(Providers providers, TransitCache transitCache, List<Schema.Routes.SourceId> sourceIds) throws Exception {
        ImmutableTable.Builder<String, Integer, Route> builder = ImmutableTable.builder();
        for (Schema.Routes.SourceId sourceId : sourceIds) {
            ITransitSource source = _sourceLookup.get(sourceId);
            builder.putAll(source.getRoutesBySourceId(providers, transitCache, sourceId));
        }
        return builder.build();
    }
}
