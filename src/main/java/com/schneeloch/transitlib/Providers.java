package com.schneeloch.transitlib;

import com.google.common.collect.ImmutableList;
import com.schneeloch.outside.DatabaseProvider;
import com.schneeloch.outside.Downloader;

import java.util.concurrent.ExecutorService;

/**
 * Things which provide interfaces with the system and don't have state of their own
 */
public class Providers {
    private final ExecutorService service;
    private final DatabaseProvider databaseProvider;
    private final Downloader downloader;

    public Providers(ExecutorService service, DatabaseProvider databaseProvider, Downloader downloader) {

        this.service = service;
        this.databaseProvider = databaseProvider;
        this.downloader = downloader;
    }

    public ExecutorService getExecutorService() {
        return service;
    }

    public IDatabaseProvider getDatabaseProvider() {
        return databaseProvider;
    }

    public IDownloader getDownloader() {
        return downloader;
    }

    public void stop() throws Exception {
        try {
            service.shutdown();
        }
        finally {
            databaseProvider.stop();
        }
    }
}
