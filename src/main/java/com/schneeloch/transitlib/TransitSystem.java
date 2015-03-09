package com.schneeloch.transitlib;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.schneeloch.outside.DatabaseProvider;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by george on 3/1/15.
 */
public class TransitSystem {
    private final ImmutableList<ITransitSource> sources;
    private final TransitCache transitCache;

    public TransitSystem(ImmutableList<ITransitSource> sources, TransitCache transitCache) {
        this.sources = sources;
        this.transitCache = transitCache;
    }


    public ListenableFuture<Iterable<Stop>> getStopsNear(DatabaseProvider provider, float lat, float lon) throws Throwable {
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
}
