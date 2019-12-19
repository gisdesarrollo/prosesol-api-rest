package com.prosesol.api.rest.services;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IHttpUrlConnection {

    public HttpURLConnection openConnection(HttpURLConnection httpUrlConnection, String method,
                                            URL url);
}
