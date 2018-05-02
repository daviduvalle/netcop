package io.dapper.cop.stats;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import io.dapper.cop.configuration.CopConfiguration;
import io.dapper.cop.history.HistoryReader;
import io.dapper.cop.models.TestInstance;
import io.dapper.cop.models.TestRecord;

/**
 * Provides basic statistics based on previous tests
 */
public class CopStats {

    private final HistoryReader historyReader;
    
    public CopStats() {
        historyReader = new HistoryReader();
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

        for (String endpoint : reportData.keySet()) {
            System.out.println("Endpoint: "+endpoint);
            List<LatencyInstance> latencies = reportData.get(endpoint);
            System.out.println("# of data points loaded: "+latencies.size());
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

            System.out.println("\tavg: "+average);
            System.out.println("\tstd. deviation: "+deviation);

            System.out.println();
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

    private static class LatencyInstance {
        private final String timestamp;
        private final String pingTime;

        public LatencyInstance(String timestamp, String pingTime) {
            this.timestamp = timestamp;
            this.pingTime = pingTime;
        }
    }

}
