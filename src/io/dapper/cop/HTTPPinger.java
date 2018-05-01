package io.dapper.cop;

import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
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
public class HTTPPinger {

    // Same timeout used for connect and connection request
    // change them if needed.
    private final static int TIMEOUT = 2000;
    private final static RequestConfig requestConfig;
    private static RequestConfig.Builder requestBuilder = RequestConfig
            .custom();
    static {
        requestBuilder = requestBuilder.setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT).setCookieSpec(CookieSpecs
                        .STANDARD);
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

            if (response.getStatusLine().getStatusCode() == 200) {
                stopWatch.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopWatch.stop();
        }

        return stopWatch.elapsed(TimeUnit.MILLISECONDS);
    }
}
