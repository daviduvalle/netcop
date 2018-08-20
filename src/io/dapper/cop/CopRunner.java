package io.dapper.cop;

import static io.dapper.cop.configuration.CopConfiguration.*;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

import io.dapper.cop.configuration.CopConfiguration;
import io.dapper.cop.history.HistoryWriter;
import io.dapper.cop.models.TestRecord;
import io.dapper.cop.net.HTTPPinger;
import io.dapper.cop.stats.CopStats;

public final class CopRunner {

    private static final DecimalFormat df = new DecimalFormat("#.00");

    private final List<String> endpoints;
    private final boolean writeToFile;

    public CopRunner(List<String> endpoints, boolean writeToFile) {
        this.endpoints = endpoints;
        this.writeToFile = writeToFile;
    }

    /**
     * Creates a fixed thread pool and renames each thread for easy identification
     * @param threadCount number of threads
     * @return an executor thread pool
     */
    private Executor getFixedThreadPool(short threadCount) {
        return Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t  = new Thread(r);
                t.setDaemon(true);
                t.setName("request-"+t.getId());
                return t;
            }
        });
    }

    public void run() {
        // Main background thread that runs
        // at a fixed rate
        ScheduledExecutorService executor = 
                Executors.newScheduledThreadPool(1);
        // Thread pool for request threads
        Executor requestThreadPool = getFixedThreadPool((short) 10);
        final AtomicInteger runCount = new AtomicInteger(1);
        HistoryWriter historyWriter = new HistoryWriter(writeToFile);

        Runnable task = () -> {
            historyWriter.createTestInstance();
            List<CompletableFuture<TestRecord>> timeFutures =
                    endpoints.stream().map(testEndPoint ->
                            CompletableFuture.supplyAsync(
                                    () -> {
                                        HTTPPinger httpPinger = new HTTPPinger();
                                        double time = httpPinger.ping(testEndPoint);
                                        TestRecord testRecord = new TestRecord(testEndPoint, df.format(time));
                                        return testRecord;
                                    }, requestThreadPool)).collect(toList());

            List<TestRecord> results =
                    timeFutures.stream().map(CompletableFuture::join).collect(toList());

            results.stream().forEach(r -> historyWriter.addRecord(r));
            historyWriter.write();

            // Stop running after the max count is reached
            if (runCount.intValue() == CopConfiguration.MAX_RUN_COUNT) {
                CopStats copStats = new CopStats(historyWriter.getHistoryFile());
                copStats.showStats();
                executor.shutdown();
            }

            runCount.incrementAndGet();
        };

        // Execute the task in an interval
        executor.scheduleAtFixedRate(task, 0,
                SECONDS_INTERVAL, TimeUnit.SECONDS);
    }
}
