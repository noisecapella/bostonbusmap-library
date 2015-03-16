package com.schneeloch.transitlib.parser;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.MoreExecutors;
import com.schneeloch.outside.DatabaseProvider;
import com.schneeloch.transitlib.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by george on 3/9/15.
 */
public class NextbusPredictionsRequester {
    private final String agency;
    private final Providers providers;
    private final TransitCache transitCache;
    private final NextbusTransitSource source;

    public NextbusPredictionsRequester(Providers providers, TransitCache transitCache, NextbusTransitSource source, String agency) {
        this.agency = agency;
        this.transitCache = transitCache;
        this.providers = providers;
        this.source = source;
    }

    public Map<String, PredictionForStop> getPredictionsByStop(final List<Stop> toRead) throws Exception {
        StringBuilder urlString = new StringBuilder("http://webservices.nextbus.com/service/publicXMLFeed?command=predictionsForMultiStops&a=" + agency);

        ImmutableTable<String, Integer, Route> routes = transitCache.readRoutesBySourceId(providers, source.getSourceIds());

        for (Stop stop : toRead) {
            for (String routeId : stop.getRouteIds()) {
                if (routes.containsRow(routeId)) {
                    urlString.append("&stops=").append(routeId).append("%7C");
                    urlString.append("%7C").append(stop.getStopId());
                }
            }
        }

        IDownloader downloader = providers.getDownloader();
        InputStream stream = downloader.download(urlString.toString());

        NextbusPredictionsFeedParser parser = new NextbusPredictionsFeedParser();
        parser.runParse(stream);

        return parser.getPredictions();
    }
}
