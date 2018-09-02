package io.dapper.cop;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

import io.dapper.cop.history.HistoryReader;
import io.dapper.cop.history.HistoryWriter;
import io.dapper.cop.models.TestInstance;
import io.dapper.cop.models.TestRecord;
import io.dapper.cop.net.HTTPPinger;
import io.dapper.cop.stats.CopStats;

/**
 * Runs parallel HTTP pings and collects timing data per endpoint
 * The network requests run by a background thread on a configured
 * time interval for a certain number of cycles.
 * For example a 5 secs interval of 10 (samplesCount) cycles will run
 * 10 times in total with a pause of 5 seconds between runs.
 */
public final class CopRunner {

    private static final DecimalFormat df = new DecimalFormat("#.00");

    private final List<String> endpoints;
    private final boolean writeToFiles;
    private final int waitSecondsInterval;
    private final int samplesCount;

    /**
     * Creates a cop runner instance
     * @param endpoints list of endpoints
     * @param writeToFiles determines if tests will be persisted or not
     * @param waitSecondsInterval interval of seconds to wait before re-testing
     * @param samplesCount max number of tests to run
     */
    public CopRunner(List<String> endpoints, boolean writeToFiles, int waitSecondsInterval, int samplesCount) {
        this.endpoints = endpoints;
        this.writeToFiles = writeToFiles;
        this.waitSecondsInterval = waitSecondsInterval;
        this.samplesCount = samplesCount;
    }

    /**
     * Starts collecting samples
     */
    public void run() {
        // Main background thread that runs
        // at a fixed rate
        ScheduledExecutorService executor = 
                Executors.newScheduledThreadPool(1);
        // Thread pool for request threads
        Executor requestThreadPool = getFixedThreadPool((short) 10);
        final AtomicInteger runCount = new AtomicInteger(1);

        System.out.printf("Netcop collecting data on %d endpoints\n", this.endpoints.size());
        HistoryWriter historyWriter = new HistoryWriter(writeToFiles);
        System.out.printf("Progress ");

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
            if (runCount.intValue() == this.samplesCount) {
                List<TestInstance> testInstances = this.getTestInstances(historyWriter);
                CopStats copStats = new CopStats(testInstances, writeToFiles);
                copStats.showStats();
                executor.shutdown();
            }

            runCount.incrementAndGet();
        };

        // Execute the task in an interval
        executor.scheduleAtFixedRate(task, 0,
                this.waitSecondsInterval, TimeUnit.SECONDS);
    }

    /**
     * Gets the list of test instances from a history writer
     * @param historyWriter tracks test instances in memory or in a file
     * @return a list of test instances
     */
    private List<TestInstance> getTestInstances(HistoryWriter historyWriter) {

        List<TestInstance> testInstances = null;

        if (historyWriter.getHistoryFile() != null) {
            HistoryReader historyReader = new HistoryReader(historyWriter.getHistoryFile());
            try {
                testInstances = historyReader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            testInstances = historyWriter.getTestInstances();
        }

        return testInstances;
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
}
