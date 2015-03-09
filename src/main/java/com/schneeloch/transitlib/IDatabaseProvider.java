package com.schneeloch.transitlib;

import com.almworks.sqlite4java.SQLiteException;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by george on 3/1/15.
 */
public interface IDatabaseProvider {
    Future<List<Stop>> readStops(List<String> toRead) throws Throwable;
}
