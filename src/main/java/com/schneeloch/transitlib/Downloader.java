package com.schneeloch.transitlib;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by george on 3/1/15.
 */
public class Downloader {
    public static InputStream download(String url) throws Exception {
        return new URL(url).openStream();
    }
}
