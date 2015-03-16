package com.schneeloch.outside;

import com.almworks.sqlite4java.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.schneeloch.schema.Schema;
import com.schneeloch.transitlib.Geometry;
import com.schneeloch.transitlib.IDatabaseProvider;
import com.schneeloch.transitlib.Route;
import com.schneeloch.transitlib.Stop;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by george on 3/1/15.
 */
public class DatabaseProvider implements IDatabaseProvider {
    private final SQLiteQueue _queue;

    public DatabaseProvider() {
        _queue = new SQLiteQueue(new File("/home/georgeandroid/IdeaProjects/bostonbusmap-library/database.db"));
        _queue.start();
    }

    @Override
    public List<Stop> readStops(final List<String> toRead) throws Exception {
        final String[] parameters = new String[toRead.size()];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = "?";
        }

        return _queue.execute(new SQLiteJob<List<Stop>>() {
            @Override
            protected List<Stop> job(SQLiteConnection connection) throws Throwable {
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
        }).complete();
    }

    @Override
    public List<String> getStopIdsNear(float lat, float lon) throws Exception {
        double lonFactor = Math.cos(lat * Geometry.degreesToRadians);
        String latDiff = "(lat - " + lat + ")";
        String lonDiff = "((lon - " + lon + ")*" + lonFactor + ")";
        final String sql = "SELECT tag FROM stops ORDER BY (" + latDiff + "*" + latDiff + " + " + lonDiff + "*" + lonDiff + ") ASC LIMIT 15";

        return _queue.execute(new SQLiteJob<List<String>>() {

            @Override
            protected List<String> job(SQLiteConnection connection) throws Throwable {
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
        }).complete();
    }

    @Override
    public List<Route> getRoutesBySourceId(final List<Schema.Routes.SourceId> sourceIds) throws Exception {
        String[] questionMarks = new String[sourceIds.size()];
        for (int i = 0; i < questionMarks.length; i++) {
            questionMarks[i] = "?";
        }
        if (questionMarks.length == 0) {
            throw new RuntimeException("No sourceIds specified");
        }

        final String sql = "SELECT route, routetitle, agencyid FROM routes where agencyid IN (" + Joiner.on(", ").join(questionMarks) + ") order by listorder ASC ";
        return _queue.execute(new SQLiteJob<List<Route>>() {
            @Override
            protected List<Route> job(SQLiteConnection connection) throws Throwable {
                SQLiteStatement statement = connection.prepare(sql);
                int i = 0;
                for (Schema.Routes.SourceId sourceId : sourceIds) {
                    statement.bind(i+1, sourceId.getValue());
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
        }).complete();
    }

    public void stop() throws InterruptedException {
        _queue.stop(true).join();
    }
}
