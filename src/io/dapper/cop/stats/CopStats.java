package io.dapper.cop.stats;

import java.io.File;
import java.io.IOException;
import java.util.*;

import io.dapper.cop.history.HistoryReader;
import io.dapper.cop.models.EndpointStats;
import io.dapper.cop.models.TestInstance;
import io.dapper.cop.models.TestRecord;

/**
 * Computes basic statistics based on saved data
 */
public class CopStats {

    private final HistoryReader historyReader;

    /**
     * Ctr.
     * @param tmpFile where latency instances are stored
     */
    public CopStats(File tmpFile) {
        historyReader = new HistoryReader(tmpFile);
    }

    /**
     * Shows stats in the STOUT and also writes them into a file
     */
    public void showStats() {
        Map<String, Queue<LatencyInstance>> reportData = this.getReportData();
        Queue<EndpointStats> stats = computeStats(reportData);

        if (stats.isEmpty()) {
            System.out.println("No stats available");
            return;
        }

        StatsFileWriter statsFileWriter = new StatsFileWriter(stats);
        statsFileWriter.writeStats();

        System.out.println();
        System.out.println("Endpoint Samples Average Median Std_deviation");
        while (!stats.isEmpty()) {
            System.out.println(stats.poll());
        }
    }

    /**
     * Loads data in a suitable way to be processed
     * @return a map containing endpoint -> sorted queue of {@link LatencyInstance}
     */
    private Map<String, Queue<LatencyInstance>> getReportData() {

        List<TestInstance> testInstances = null;

        try {
            testInstances = historyReader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (testInstances == null || testInstances.isEmpty()) {
            System.out.println("No data available, temp file was empty");
        }

        return this.loadData(testInstances);
    }

    /**
     *  Computes avg, median, and standard deviation
     *  @return a priority queue sorted by endpoint avg. latency
     */
    private Queue<EndpointStats> computeStats(Map<String, Queue<LatencyInstance>>
                                        reportData) {

        Comparator<EndpointStats> comparator = Comparator.comparing
                (EndpointStats::getAverage);

        Queue<EndpointStats> statsQueue =
                new PriorityQueue<EndpointStats>(reportData.keySet().size(),
                        comparator);

        for (String endpoint : reportData.keySet()) {
            Queue<LatencyInstance> latencies = reportData.get(endpoint);
            Queue<LatencyInstance> latenciesCopy = new PriorityQueue<>
                    (latencies);

            double average = 0;
            double median = 0;
            int latenciesLength = latencies.size();
            int medianPos = latenciesLength / 2;
            boolean isOdd = latenciesLength % 2 != 0 ? true : false;
            medianPos = isOdd ? medianPos : medianPos - 1;

            // Compute average and mean
            for (int i = 0; i < latenciesLength; i++) {
                double tmp = latencies.poll().pingTime;
                if (isOdd && i == medianPos) {
                    median = tmp;
                } else if (!isOdd && i == medianPos){
                    median = (tmp + latencies.peek().pingTime) / 2;
                }

                average += tmp;
            }

            average = average / latenciesLength;

            // Compute std. deviation
            double numerator = 0;
            double denominator = latenciesLength - 1;
            for (LatencyInstance latencyInstance : latenciesCopy) {
                numerator += Math.pow(Double.valueOf(latencyInstance.pingTime)
                                - average, 2);
            }

            double tmpValue = numerator / denominator;
            double deviation = Math.sqrt(tmpValue);


            EndpointStats endpointStats =
                    new EndpointStats(
                            endpoint,
                            latenciesLength,
                            average,
                            median,
                            deviation);

            statsQueue.offer(endpointStats);
        }

        return statsQueue;
    }

    /**
     * Loads test instances into a map where the key is the website being tested
     * @param testInstances latency test instances
     * @return a map containing key = website, value = list of test instances
     */
    private Map<String, Queue<LatencyInstance>> loadData(List<TestInstance>
                                                             testInstances) {

        HashMap<String, Queue<LatencyInstance>> endpointToLatencyInstance = new
                HashMap<>();

        Comparator<LatencyInstance> comparator = Comparator.comparing
                (LatencyInstance::getPingTime);

        for (TestInstance testInstance : testInstances) {
            for (TestRecord testRecord : testInstance.getTestRecords()) {
                if (endpointToLatencyInstance.containsKey(testRecord.getWebsiteName())) {
                    endpointToLatencyInstance.get(
                            testRecord.getWebsiteName()).add(
                            new LatencyInstance(testInstance.getInstanceDate
                                    (), testRecord.getTime()));
                } else {
                    Queue<LatencyInstance> latencyInstances =
                            new PriorityQueue<>(comparator);
                    latencyInstances.add(new LatencyInstance(testInstance.getInstanceDate(), testRecord.getTime()));
                    endpointToLatencyInstance.put(testRecord.getWebsiteName(), latencyInstances);
                }
            }
        }

        return endpointToLatencyInstance;
    }

    private static class LatencyInstance {
        private final String timestamp;
        private final double pingTime;

        public LatencyInstance(String timestamp, String pingTime) {
            this.timestamp = timestamp;
            this.pingTime = Double.valueOf(pingTime);
        }

        public double getPingTime() {
            return this.pingTime;
        }
    }

}
