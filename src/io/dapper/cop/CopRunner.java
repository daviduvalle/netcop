package io.dapper.cop;

import static io.dapper.cop.configuration.CopConfiguration.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.text.DecimalFormat;

import io.dapper.cop.history.HistoryWriter;
import io.dapper.cop.models.TestRecord;

public class CopRunner {
    
    public void run() {
        HTTPPinger httpPinger = new HTTPPinger(PING_COUNT);

        // TODO: Make this configurable so that more than
        // one thread can run the ping.
        ScheduledExecutorService executor = 
                Executors.newScheduledThreadPool(BG_THREADS);
        
        Runnable task = () -> {
            try {

                HistoryWriter historyKeeper = new HistoryWriter();
                DecimalFormat df = new DecimalFormat("#.00");
                for (String endpoint : TestEndpoint.ENDPOINTS.keySet()) {
                    httpPinger.setTarget(TestEndpoint.ENDPOINTS.get(endpoint));
                    double avgTime = httpPinger.ping();
                    historyKeeper.addRecord(new TestRecord(endpoint, df.format(avgTime)));
                }
                historyKeeper.write();

            } catch (Exception e) {
                System.out.println("Something went wrong "+e);
            }
        };
        
        executor.scheduleAtFixedRate(task, 0,
                SECONDS_INTERVAL, TimeUnit.SECONDS);
    }
}
