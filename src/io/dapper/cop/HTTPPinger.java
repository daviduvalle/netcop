package io.dapper.cop;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.common.base.Stopwatch;

public class HTTPPinger {

    private int pingCount;
    private String websiteURL;
    
    public HTTPPinger(int pingCount) {
        this.pingCount = pingCount;
    }
    
    public void setTarget(String websiteURL) {
        this.websiteURL = websiteURL;
    }
    
    public double ping() {
        
        double avg = 0;
        int successfulPings = 0;
        
        for (int i = 0; i < this.pingCount; i++) {
            
            RequestConfig.Builder requestBuilder = RequestConfig.custom();
            requestBuilder = requestBuilder.setConnectTimeout(2000);
            requestBuilder = requestBuilder.setConnectionRequestTimeout(2000);
            requestBuilder = requestBuilder.setCookieSpec(CookieSpecs.STANDARD);
            
            HttpClient client = HttpClientBuilder.create().
                    setDefaultRequestConfig(requestBuilder.build()).build();
            HttpGet request = new HttpGet(this.websiteURL);
            Stopwatch stopWatch = Stopwatch.createStarted();
            try {
                HttpResponse response = client.execute(request);
                
                if (response.getStatusLine().getStatusCode() == 200) {
                    stopWatch.stop();
                    avg += stopWatch.elapsed(TimeUnit.MILLISECONDS);
                    successfulPings++;
                } else {
                    avg += 0;
                }
                
            } catch (IOException e) {
                e.printStackTrace();
                stopWatch.stop();
            }
        }

        if (successfulPings != 0) {
            avg = avg / successfulPings;
        }
        
        return avg;
    }
}
