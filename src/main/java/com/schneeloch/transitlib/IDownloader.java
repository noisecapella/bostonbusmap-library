package com.schneeloch.transitlib;

import java.io.InputStream;

/**
 * Created by george on 3/9/15.
 */
public interface IDownloader {
    public InputStream download(String url) throws Exception;
}