package com.schneeloch.schema;

import java.util.Collection;

public class Schema {
    public static final String dbName = "transitData";
    public static final String oldDb = "bostonBusMap";

    private static final int INT_TRUE = 1;
    private static final int INT_FALSE = 0;

    public static int toInteger(boolean b) {
        return b ? INT_TRUE : INT_FALSE;
    }

    public static boolean fromInteger(int i) {
        return i == INT_TRUE;
    }


    public static class Bounds {
        public static final String table = "bounds";
        public static final String[] columns = new String[] {
                "route", "weekdays", "start", "stop"
        };

        public static final int routeIndex = 1;
        public static final String routeColumn = "route";
        public static final String routeColumnOnTable = "bounds.route";
        public static final int weekdaysIndex = 2;
        public static final String weekdaysColumn = "weekdays";
        public static final String weekdaysColumnOnTable = "bounds.weekdays";
        public static final int startIndex = 3;
        public static final String startColumn = "start";
        public static final String startColumnOnTable = "bounds.start";
        public static final int stopIndex = 4;
        public static final String stopColumn = "stop";
        public static final String stopColumnOnTable = "bounds.stop";

        public static final String dropSql = "DROP TABLE IF EXISTS bounds";
        public static final String createSql = "CREATE TABLE IF NOT EXISTS bounds (route STRING, weekdays INTEGER, start INTEGER, stop INTEGER)";
        public static class Bean {
            public final String route;
            public final int weekdays;
            public final int start;
            public final int stop;
            public Bean(String route, int weekdays, int start, int stop) {
                this.route = route;
                this.weekdays = weekdays;
                this.start = start;
                this.stop = stop;
            }
        }
    }
    public static class Directions {
        public static final String table = "directions";
        public static final String[] columns = new String[] {
                "dirTag", "dirNameKey", "dirTitleKey", "dirRouteKey", "useAsUI"
        };

        public static final int dirTagIndex = 1;
        public static final String dirTagColumn = "dirTag";
        public static final String dirTagColumnOnTable = "directions.dirTag";
        public static final int dirNameKeyIndex = 2;
        public static final String dirNameKeyColumn = "dirNameKey";
        public static final String dirNameKeyColumnOnTable = "directions.dirNameKey";
        public static final int dirTitleKeyIndex = 3;
        public static final String dirTitleKeyColumn = "dirTitleKey";
        public static final String dirTitleKeyColumnOnTable = "directions.dirTitleKey";
        public static final int dirRouteKeyIndex = 4;
        public static final String dirRouteKeyColumn = "dirRouteKey";
        public static final String dirRouteKeyColumnOnTable = "directions.dirRouteKey";
        public static final int useAsUIIndex = 5;
        public static final String useAsUIColumn = "useAsUI";
        public static final String useAsUIColumnOnTable = "directions.useAsUI";

        public static final String dropSql = "DROP TABLE IF EXISTS directions";
        public static final String createSql = "CREATE TABLE IF NOT EXISTS directions (dirTag STRING PRIMARY KEY, dirNameKey STRING, dirTitleKey STRING, dirRouteKey STRING, useAsUI INTEGER)";
        public static class Bean {
            public final String dirTag;
            public final String dirNameKey;
            public final String dirTitleKey;
            public final String dirRouteKey;
            public final int useAsUI;
            public Bean(String dirTag, String dirNameKey, String dirTitleKey, String dirRouteKey, int useAsUI) {
                this.dirTag = dirTag;
                this.dirNameKey = dirNameKey;
                this.dirTitleKey = dirTitleKey;
                this.dirRouteKey = dirRouteKey;
                this.useAsUI = useAsUI;
            }
        }
    }
    public static class DirectionsStops {
        public static final String table = "directionsStops";
        public static final String[] columns = new String[] {
                "dirTag", "tag"
        };

        public static final int dirTagIndex = 1;
        public static final String dirTagColumn = "dirTag";
        public static final String dirTagColumnOnTable = "directionsStops.dirTag";
        public static final int tagIndex = 2;
        public static final String tagColumn = "tag";
        public static final String tagColumnOnTable = "directionsStops.tag";

        public static final String dropSql = "DROP TABLE IF EXISTS directionsStops";
        public static final String createSql = "CREATE TABLE IF NOT EXISTS directionsStops (dirTag STRING, tag STRING)";
        public static class Bean {
            public final String dirTag;
            public final String tag;
            public Bean(String dirTag, String tag) {
                this.dirTag = dirTag;
                this.tag = tag;
            }
        }
    }
    public static class Favorites {
        public static final String table = "favorites";
        public static final String[] columns = new String[] {
                "tag"
        };

        public static final int tagIndex = 1;
        public static final String tagColumn = "tag";
        public static final String tagColumnOnTable = "favorites.tag";

        public static final String dropSql = "DROP TABLE IF EXISTS favorites";
        public static final String createSql = "CREATE TABLE IF NOT EXISTS favorites (tag STRING PRIMARY KEY)";
        public static class Bean {
            public final String tag;
            public Bean(String tag) {
                this.tag = tag;
            }
        }
    }
    public static class Locations {
        public static final String table = "locations";
        public static final String[] columns = new String[] {
                "lat", "lon", "name"
        };

        public static final int latIndex = 1;
        public static final String latColumn = "lat";
        public static final String latColumnOnTable = "locations.lat";
        public static final int lonIndex = 2;
        public static final String lonColumn = "lon";
        public static final String lonColumnOnTable = "locations.lon";
        public static final int nameIndex = 3;
        public static final String nameColumn = "name";
        public static final String nameColumnOnTable = "locations.name";

        public static final String dropSql = "DROP TABLE IF EXISTS locations";
        public static final String createSql = "CREATE TABLE IF NOT EXISTS locations (lat FLOAT, lon FLOAT, name STRING PRIMARY KEY)";
        public static class Bean {
            public final float lat;
            public final float lon;
            public final String name;
            public Bean(float lat, float lon, String name) {
                this.lat = lat;
                this.lon = lon;
                this.name = name;
            }
        }
    }
    public static class Routes {
        public static final String table = "routes";
        public static final String[] columns = new String[] {
                "route", "color", "oppositecolor", "pathblob", "listorder", "agencyid", "routetitle"
        };

        public static final int routeIndex = 1;
        public static final String routeColumn = "route";
        public static final String routeColumnOnTable = "routes.route";
        public static final int colorIndex = 2;
        public static final String colorColumn = "color";
        public static final String colorColumnOnTable = "routes.color";
        public static final int oppositecolorIndex = 3;
        public static final String oppositecolorColumn = "oppositecolor";
        public static final String oppositecolorColumnOnTable = "routes.oppositecolor";
        public static final int pathblobIndex = 4;
        public static final String pathblobColumn = "pathblob";
        public static final String pathblobColumnOnTable = "routes.pathblob";
        public static final int listorderIndex = 5;
        public static final String listorderColumn = "listorder";
        public static final String listorderColumnOnTable = "routes.listorder";
        public static final int agencyidIndex = 6;
        public static final String agencyidColumn = "agencyid";
        public static final String agencyidColumnOnTable = "routes.agencyid";

        public static final int enumagencyidSubway = 1;
        public static final int enumagencyidCommuterRail = 2;
        public static final int enumagencyidBus = 3;
        public static final int enumagencyidHubway = 50;
        public static final int routetitleIndex = 7;
        public static final String routetitleColumn = "routetitle";
        public static final String routetitleColumnOnTable = "routes.routetitle";

        public static final String dropSql = "DROP TABLE IF EXISTS routes";
        public static final String createSql = "CREATE TABLE IF NOT EXISTS routes (route STRING PRIMARY KEY, color INTEGER, oppositecolor INTEGER, pathblob BLOB, listorder INTEGER, agencyid INTEGER, routetitle STRING)";
        public static class Bean {
            public final String route;
            public final int color;
            public final int oppositecolor;
            public final byte[] pathblob;
            public final int listorder;
            public final int agencyid;
            public final String routetitle;
            public Bean(String route, int color, int oppositecolor, byte[] pathblob, int listorder, int agencyid, String routetitle) {
                this.route = route;
                this.color = color;
                this.oppositecolor = oppositecolor;
                this.pathblob = pathblob;
                this.listorder = listorder;
                this.agencyid = agencyid;
                this.routetitle = routetitle;
            }
        }
    }
    public static class Stopmapping {
        public static final String table = "stopmapping";
        public static final String[] columns = new String[] {
                "route", "tag"
        };

        public static final int routeIndex = 1;
        public static final String routeColumn = "route";
        public static final String routeColumnOnTable = "stopmapping.route";
        public static final int tagIndex = 2;
        public static final String tagColumn = "tag";
        public static final String tagColumnOnTable = "stopmapping.tag";

        public static final String dropSql = "DROP TABLE IF EXISTS stopmapping";
        public static final String createSql = "CREATE TABLE IF NOT EXISTS stopmapping (route STRING, tag STRING, PRIMARY KEY (route, tag))";
        public static class Bean {
            public final String route;
            public final String tag;
            public Bean(String route, String tag) {
                this.route = route;
                this.tag = tag;
            }
        }
    }
    public static class Stops {
        public static final String table = "stops";
        public static final String[] columns = new String[] {
                "tag", "lat", "lon", "title"
        };

        public static final int tagIndex = 1;
        public static final String tagColumn = "tag";
        public static final String tagColumnOnTable = "stops.tag";
        public static final int latIndex = 2;
        public static final String latColumn = "lat";
        public static final String latColumnOnTable = "stops.lat";
        public static final int lonIndex = 3;
        public static final String lonColumn = "lon";
        public static final String lonColumnOnTable = "stops.lon";
        public static final int titleIndex = 4;
        public static final String titleColumn = "title";
        public static final String titleColumnOnTable = "stops.title";

        public static final String dropSql = "DROP TABLE IF EXISTS stops";
        public static final String createSql = "CREATE TABLE IF NOT EXISTS stops (tag STRING PRIMARY KEY, lat FLOAT, lon FLOAT, title STRING)";
        public static class Bean {
            public final String tag;
            public final float lat;
            public final float lon;
            public final String title;
            public Bean(String tag, float lat, float lon, String title) {
                this.tag = tag;
                this.lat = lat;
                this.lon = lon;
                this.title = title;
            }
        }
    }
}
