package com.schneeloch.transitlib;

import com.almworks.sqlite4java.SQLiteException;
import com.schneeloch.outside.DatabaseProvider;

import java.util.Collection;
import java.util.List;

/**
 * Created by george on 3/1/15.
 */
public interface ITransitSource {
    List<Stop> readStops(DatabaseProvider provider, StopCache stopCache, List<String> toRead) throws Exception;

    List<Stop> getStopsNear(DatabaseProvider provider, StopCache stopCache, float lat, float lon) throws Exception;
}
