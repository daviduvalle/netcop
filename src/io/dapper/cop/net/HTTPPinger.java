package io.dapper.cop.net;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.common.base.Stopwatch;

/**
 * Simple http client that connects to a http server
 * and reports back the time spent during connection
 */
public final class HTTPPinger {

    // Same timeout used for connect and connection request
    // change them if needed.
    private final static int TIMEOUT = 4000;
    private final static RequestConfig requestConfig;
    private static RequestConfig.Builder requestBuilder = RequestConfig
            .custom();
    static {
        requestBuilder =
                requestBuilder.setConnectTimeout(TIMEOUT)
                        .setConnectionRequestTimeout(TIMEOUT)
                        .setCookieSpec(CookieSpecs.STANDARD).setRedirectsEnabled(false);
        requestConfig = requestBuilder.build();
    }

    /**
     * Returns the time spent during ping
     * @return ping time
     */
    public long ping(String endpoint) {
        HttpClient client = HttpClientBuilder.create().
                setDefaultRequestConfig(requestConfig).build();
        HttpGet request = new HttpGet(endpoint);
        Stopwatch stopWatch = Stopwatch.createStarted();

        try {
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                stopWatch.stop();
            }
        } catch (IOException e) {
            e.printStackTrace();
            stopWatch.stop();
        }

        return stopWatch.elapsed(TimeUnit.MILLISECONDS);
    }
}
