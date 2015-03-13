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
    private final IDownloader downloader;
    private final ExecutorService executorService;
    private final TransitCache transitCache;
    private final DatabaseProvider provider;

    public NextbusPredictionsRequester(String agency, IDownloader downloader, ExecutorService executorService, TransitCache transitCache, DatabaseProvider provider) {
        this.agency = agency;
        this.downloader = downloader;
        this.executorService = executorService;
        this.transitCache = transitCache;
        this.provider = provider;
    }

    public ListenableFuture<Map<String, PredictionForStop>> getPredictionsByStop(final NextbusTransitSource source, final List<Stop> toRead) {
        ListenableFutureTask<Map<String, PredictionForStop>> task =  ListenableFutureTask.create(new Callable<Map<String, PredictionForStop>>() {
            @Override
            public Map<String, PredictionForStop> call() throws Exception {
                StringBuilder urlString = new StringBuilder("http://webservices.nextbus.com/service/publicXMLFeed?command=predictionsForMultiStops&a=" + agency);

                ImmutableTable<String, Integer, Route> routes = transitCache.readRoutesBySourceId(provider, source.getSourceIds()).get();

                for (Stop stop : toRead) {
                    for (String routeId : stop.getRouteIds()) {
                        if (routes.containsRow(routeId)) {
                            urlString.append("&stops=").append(routeId).append("%7C");
                            urlString.append("%7C").append(stop.getStopId());
                        }
                    }
                }

                System.out.println(urlString);

                InputStream stream = downloader.download(urlString.toString());

                NextbusPredictionsFeedParser parser = new NextbusPredictionsFeedParser();
                parser.runParse(stream);

                return parser.getPredictions();
            }
        });
        executorService.submit(task);


        return task;
    }
}
