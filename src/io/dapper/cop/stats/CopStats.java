package io.dapper.cop.stats;

import java.io.File;
import java.io.IOException;
import java.util.*;

import io.dapper.cop.configuration.CopConfiguration;
import io.dapper.cop.history.HistoryReader;
import io.dapper.cop.models.TestInstance;
import io.dapper.cop.models.TestRecord;

/**
 * Provides basic statistics based on previous tests
 */
public class CopStats {

    private final HistoryReader historyReader;
    
    public CopStats(File tmpFile) {
        historyReader = new HistoryReader(tmpFile);
    }

    /**
     * Prints the data into the STDOUT
     */
    public void printStats() {

        List<TestInstance> testInstances = null;

        try {
            testInstances = historyReader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (testInstances == null || testInstances.isEmpty()) {
            System.out.println("No data available to show");
        }

        Map<String, List<LatencyInstance>> reportData = this.loadData(testInstances);
        Comparator<EndpointStats> comparator = new Comparator<EndpointStats>() {
            @Override
            public int compare(EndpointStats o1, EndpointStats o2) {
                return Double.compare(o1.average, o2.average);
            }
        };

        Queue<EndpointStats> statsQueue =
                new PriorityQueue<EndpointStats>(reportData.keySet().size(),
                        comparator);

        for (String endpoint : reportData.keySet()) {
            List<LatencyInstance> latencies = reportData.get(endpoint);
            double average = 0;

            // Compute average
            for (LatencyInstance latencyInstance : latencies) {
                average += Double.valueOf(latencyInstance.pingTime);
            }

            average = average / latencies.size();

            // Compute std. deviation
            float numerator = 0;
            float denominator = latencies.size() - 1;
            for (LatencyInstance latencyInstance : latencies) {
                numerator += Math.pow(Float.valueOf(latencyInstance.pingTime)
                                - average, 2);
            }

            float tmpValue = numerator / denominator;
            double deviation = Math.sqrt(tmpValue);

            EndpointStats endpointStats =
                    new EndpointStats(endpoint, latencies.size(), average,
                            deviation);
            statsQueue.offer(endpointStats);
        }

        System.out.println("Endpoint Samples Average Std_deviation");
        while (!statsQueue.isEmpty()) {
            System.out.println(statsQueue.poll());
        }
    }

    /**
     * Loads test instances into a map where the key is the website being tested
     * @param testInstances latency test instances
     * @return a map containing key = website, value = list of test instances
     */
    private Map<String, List<LatencyInstance>> loadData(List<TestInstance> testInstances) {

        HashMap<String, List<LatencyInstance>> endpointToLatencyInstance = new HashMap<>();

        for (TestInstance testInstance : testInstances) {
            for (TestRecord testRecord : testInstance.getTestRecords()) {
                if (endpointToLatencyInstance.containsKey(testRecord.getWebsiteName())) {
                    endpointToLatencyInstance.get(
                            testRecord.getWebsiteName()).add(
                            new LatencyInstance(testInstance.getInstanceDate
                                    (), testRecord.getTime()));
                } else {
                    ArrayList<LatencyInstance> latencyInstances = new ArrayList<>();
                    latencyInstances.add(new LatencyInstance(testInstance.getInstanceDate(), testRecord.getTime()));
                    endpointToLatencyInstance.put(testRecord.getWebsiteName(), latencyInstances);
                }
            }
        }

        return endpointToLatencyInstance;
    }

    private static class EndpointStats {
        private final String endpoint;
        private final int dataPoints;
        private final double average;
        private final double stdDeviation;

        public EndpointStats(String endpoint, int dataPoints, double average,
                             double stdDeviation) {
            this.endpoint = endpoint;
            this.dataPoints = dataPoints;
            this.average = average;
            this.stdDeviation = stdDeviation;
        }

        @Override
        public String toString() {
            return String.format("%s %d %.2f %.2f",
                    this.endpoint, this.dataPoints,
                    this.average, this.stdDeviation);
        }
    }

    private static class LatencyInstance {
        private final String timestamp;
        private final String pingTime;

        public LatencyInstance(String timestamp, String pingTime) {
            this.timestamp = timestamp;
            this.pingTime = pingTime;
        }
    }

}
