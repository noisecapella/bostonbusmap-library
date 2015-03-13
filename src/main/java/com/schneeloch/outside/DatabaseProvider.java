package com.schneeloch.outside;

import com.almworks.sqlite4java.*;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.schneeloch.transitlib.Geometry;
import com.schneeloch.transitlib.IDatabaseProvider;
import com.schneeloch.transitlib.Route;
import com.schneeloch.transitlib.Stop;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by george on 3/1/15.
 */
public class DatabaseProvider implements IDatabaseProvider {
    private final SQLiteQueue queue;

    public DatabaseProvider() {
        queue = new SQLiteQueue(new File("/home/georgeandroid/IdeaProjects/bostonbusmap-library/database.db"));
        queue.start();
    }

    private static class RunnableExecutorPair {
        public Runnable runnable;
        public Executor executor;
        public RunnableExecutorPair(Runnable runnable, Executor executor) {
            this.runnable = runnable;
            this.executor = executor;
        }
    }

    /**
     * Like Function but throws Throwable and has specific argument
     * @param <T>
     */
    private interface CustomJob<T> {
        public T apply(SQLiteConnection connection) throws Throwable;
    }

    protected <T> ListenableFuture<T> executeJob(final CustomJob<T> job) throws Exception {
        final CopyOnWriteArrayList<RunnableExecutorPair> listeners = new CopyOnWriteArrayList<>();
        final SQLiteJob<T> extendedJob = new SQLiteJob<T>() {
            @Override
            protected T job(SQLiteConnection connection) throws Throwable {
                return job.apply(connection);
            }

            @Override
            protected void jobFinished(T result) throws Throwable {
                for (RunnableExecutorPair pair : listeners) {
                    pair.executor.execute(pair.runnable);
                }
            }
        };
        final Future<T> future = queue.execute(extendedJob);
        return JdkFutureAdapters.listenInPoolThread(future);
    }

    public ListenableFuture<List<Stop>> readStops(final List<String> toRead) throws Throwable {
        final String[] parameters = new String[toRead.size()];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = "?";
        }
        return executeJob(new CustomJob<List<Stop>>() {
            @Override
            public List<Stop> apply(SQLiteConnection connection) throws Throwable {
                SQLiteStatement statement = connection.prepare("SELECT s.tag, s.lat, s.lon, s.title, sm.route " +
                        "FROM stops AS s " +
                        "JOIN stopmapping AS sm ON s.tag = sm.tag WHERE s.tag IN (" + Joiner.on(',').join(parameters) + ")");
                try {
                    for (int i = 0; i < parameters.length; i++) {
                        statement.bind(i + 1, toRead.get(i));
                    }
                    Map<String, Stop.Builder> map = Maps.newHashMap();
                    while (statement.step()) {
                        String stopId = statement.columnString(0);
                        float lat = (float) statement.columnDouble(1);
                        float lon = (float) statement.columnDouble(2);
                        String title = statement.columnString(3);
                        String routeId = statement.columnString(4);

                        if (map.containsKey(stopId)) {
                            map.get(stopId).addRouteId(routeId);
                        }
                        else {
                            Stop.Builder builder = new Stop.Builder()
                                    .stopId(stopId)
                                    .stopTitle(title)
                                    .lat(lat)
                                    .lon(lon)
                                    .addRouteId(routeId);
                            map.put(stopId, builder);
                        }
                    }
                    List<Stop> ret = Lists.newArrayList();
                    for (String stopId : toRead) {
                        Stop stop = map.get(stopId).build();
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

    public ListenableFuture<List<String>> getStopIdsNear(float lat, float lon) throws Throwable {
        double lonFactor = Math.cos(lat * Geometry.degreesToRadians);
        String latDiff = "(lat - " + lat + ")";
        String lonDiff = "((lon - " + lon + ")*" + lonFactor + ")";
        final String sql = "SELECT tag FROM stops ORDER BY (" + latDiff + "*" + latDiff + " + " + lonDiff + "*" + lonDiff + ") ASC LIMIT 15";

        return executeJob(new CustomJob<List<String>>() {

            @Override
            public List<String> apply(SQLiteConnection connection) throws Throwable {
                SQLiteStatement statement = connection.prepare(sql);
                try {
                    List<String> ret = Lists.newArrayList();
                    while (statement.step()) {
                        ret.add(statement.columnString(0));
                    }
                    return ret;
                } finally {
                    statement.dispose();
                }
            }
        });
    }

    public ListenableFuture<List<Route>> getRoutesBySourceId(final Collection<Integer> sourceIds) throws Exception {
        String[] questionMarks = new String[sourceIds.size()];
        for (int i = 0; i < questionMarks.length; i++) {
            questionMarks[i] = "?";
        }
        if (questionMarks.length == 0) {
            throw new RuntimeException("No sourceIds specified");
        }

        final String sql = "SELECT route, routetitle, agencyid FROM routes where agencyid IN (" + Joiner.on(", ").join(questionMarks) + ") order by listorder ASC ";
        return executeJob(new CustomJob<List<Route>>() {

            @Override
            public List<Route> apply(SQLiteConnection connection) throws Throwable {
                SQLiteStatement statement = connection.prepare(sql);
                int i = 0;
                for (int sourceId : sourceIds) {
                    statement.bind(i+1, sourceId);
                    i++;
                }
                try {
                    List<Route> ret = Lists.newArrayList();
                    while (statement.step()) {
                        String route = statement.columnString(0);
                        String routeTitle = statement.columnString(1);
                        int sourceId = statement.columnInt(2);
                        ret.add(new Route(route, routeTitle, sourceId));
                    }
                    return ret;
                } finally {
                    statement.dispose();
                }
            }
        });
    }

    public void stop() throws InterruptedException {
        queue.stop(true).join();
    }
}
