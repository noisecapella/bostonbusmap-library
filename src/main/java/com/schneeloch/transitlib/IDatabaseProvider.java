package com.schneeloch.transitlib;

import com.almworks.sqlite4java.SQLiteException;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by george on 3/1/15.
 */
public interface IDatabaseProvider {
    List<Stop> readStops(List<String> toRead) throws Exception;
    List<Route> getRoutesBySourceId(final Collection<Integer> sourceIds) throws Exception;
    List<String> getStopIdsNear(float lat, float lon) throws Exception;
}
