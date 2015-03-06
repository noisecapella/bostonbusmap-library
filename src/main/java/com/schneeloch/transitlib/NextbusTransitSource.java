package com.schneeloch.transitlib;

import com.almworks.sqlite4java.SQLiteException;
import com.google.common.collect.Lists;
import com.schneeloch.outside.DatabaseProvider;

import java.util.List;

/**
 * Created by george on 3/1/15.
 */
public class NextbusTransitSource implements ITransitSource {
    public NextbusTransitSource() {

    }

    @Override
    public List<Stop> readStops(DatabaseProvider provider, StopCache stopCache, List<String> toRead) throws Exception {
        return stopCache.readStops(provider, toRead);
    }

    @Override
    public List<Stop> getStopsNear(DatabaseProvider provider, StopCache stopCache, float lat, float lon) throws Exception {
        return stopCache.readStopsNear(provider, lat, lon);
    }
}
