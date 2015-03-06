package com.schneeloch.outside;

import com.almworks.sqlite4java.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.schneeloch.transitlib.Geometry;
import com.schneeloch.transitlib.IDatabaseProvider;
import com.schneeloch.transitlib.Stop;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by george on 3/1/15.
 */
public class DatabaseProvider implements IDatabaseProvider {
    private final SQLiteQueue queue;

    public DatabaseProvider() {
        queue = new SQLiteQueue(new File("/home/georgeandroid/IdeaProjects/bostonbusmap-library/database.db"));
        queue.start();
    }

    public Future<List<Stop>> readStops(final List<String> toRead) throws SQLiteException {
        final String[] parameters = new String[toRead.size()];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = "?";
        }
        return queue.execute(new SQLiteJob<List<Stop>>() {

            @Override
            protected List<Stop> job(SQLiteConnection connection) throws Throwable {
                SQLiteStatement statement = connection.prepare("SELECT tag, lat, lon, title from stops where tag IN (" + Joiner.on(',').join(parameters) + ")");
                try {
                    for (int i = 0; i < parameters.length; i++) {
                        statement.bind(i + 1, toRead.get(i));
                    }
                    List<Stop> ret = Lists.newArrayList();
                    while (statement.step()) {
                        String stopId = statement.columnString(0);
                        float lat = (float) statement.columnDouble(1);
                        float lon = (float) statement.columnDouble(2);
                        String title = statement.columnString(3);

                        Stop stop = new Stop(stopId, title, lat, lon);
                        ret.add(stop);
                    }
                    return ret;
                }
                finally {
                    statement.dispose();
                }
            }
        });
    }

    public Future<List<String>> getStopIdsNear(float lat, float lon) throws SQLiteException {
        double lonFactor = Math.cos(lat * Geometry.degreesToRadians);
        String latDiff = "(lat - " + lat + ")";
        String lonDiff = "((lon - " + lon + ")*" + lonFactor + ")";
        final String sql = "SELECT tag FROM stops ORDER BY (" + latDiff + "*" + latDiff + " + " + lonDiff + "*" + lonDiff + ") ASC LIMIT 15";

        return queue.execute(new SQLiteJob<List<String>>() {

            @Override
            protected List<String> job(SQLiteConnection connection) throws Throwable {
                SQLiteStatement statement = connection.prepare(sql);
                try {
                    List<String> ret = Lists.newArrayList();
                    while (statement.step()) {
                        ret.add(statement.columnString(0));
                    }
                    return ret;
                }
                finally {
                    statement.dispose();
                }
            }
        });
    }

    public void stop() throws InterruptedException {
        queue.stop(true).join();
    }
}
