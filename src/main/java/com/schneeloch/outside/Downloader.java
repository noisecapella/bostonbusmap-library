package com.schneeloch.outside;

import com.schneeloch.transitlib.IDownloader;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by george on 3/1/15.
 */
public class Downloader implements IDownloader {
    public Downloader() {
    }

    @Override
    public InputStream download(String url) throws Exception {
        return new URL(url).openStream();
    }
}
