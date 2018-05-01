package io.dapper.cop;

import static io.dapper.cop.configuration.CopConfiguration.*;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.*;
import java.text.DecimalFormat;

import io.dapper.cop.history.HistoryWriter;
import io.dapper.cop.models.TestRecord;

public class CopRunner {

    public void run() {

        // Main background thread that runs
        // at a fixed rate
        ScheduledExecutorService executor = 
                Executors.newScheduledThreadPool(1);
        // Thread pool for request threads
        Executor requestThreadPool = Executors.newFixedThreadPool(10, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t  = new Thread(r);
                t.setDaemon(true);
                t.setName("request-"+t.getId());
                return t;
            }
        });
        DecimalFormat df = new DecimalFormat("#.00");

        Runnable task = () -> {
            HistoryWriter historyWriter = new HistoryWriter();
            List<CompletableFuture<TestRecord>> timeFutures =
                    TestEndpoint.ENDPOINTS.keySet().stream().map(testEndPoint ->
                            CompletableFuture.supplyAsync(
                                    () -> {
                                        HTTPPinger httpPinger = new HTTPPinger();
                                        double time = httpPinger.ping
                                                (TestEndpoint.ENDPOINTS
                                                        .get(testEndPoint));
                                        TestRecord testRecord = new
                                                TestRecord(testEndPoint,
                                                df.format(time));
                                        return testRecord;
                                    }, requestThreadPool)).collect(
                            toList());

            List<TestRecord> results =
                    timeFutures.stream().map(CompletableFuture::join).collect(toList());

            results.stream().forEach(r -> historyWriter.addRecord(r));
            historyWriter.write();
        };
        
        executor.scheduleAtFixedRate(task, 0,
                SECONDS_INTERVAL, TimeUnit.SECONDS);
    }
}
