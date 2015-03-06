package com.schneeloch.transitlib;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.schneeloch.outside.DatabaseProvider;

import java.util.List;

/**
 * Created by george on 3/1/15.
 */
public class TransitSystem {
    private final ImmutableList<ITransitSource> sources;
    private final StopCache stopCache;

    public TransitSystem(ImmutableList<ITransitSource> sources, StopCache stopCache) {
        this.sources = sources;
        this.stopCache = stopCache;
    }


    public List<Stop> getStopsNear(DatabaseProvider provider, float lat, float lon) throws Exception {
        List<Stop> stops = Lists.newArrayList();

        for (ITransitSource source : sources) {
            stops.addAll(source.getStopsNear(provider, stopCache, lat, lon));
        }

        return stops;
    }
}
