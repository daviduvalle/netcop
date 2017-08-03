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

        for (String website : reportData.keySet()) {
            System.out.println("Website: "+website);
            System.out.println("Total number of requests: "+reportData.get
                    (website).size() * CopConfiguration.PING_COUNT);
            System.out.println("Averages saved: "+reportData.get(website).size());
            for (LatencyInstance latencyInstance : reportData.get(website)) {
                System.out.println(
                        "\t"+latencyInstance.getTime()+"\tavg: "+latencyInstance.getAverageTimeinMilli());
            }
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
                            new LatencyInstance(testInstance.getInstanceDate(), testRecord.getAvgTime()));
                } else {
                    ArrayList<LatencyInstance> latencyInstances = new ArrayList<>();
                    latencyInstances.add(new LatencyInstance(testInstance.getInstanceDate(), testRecord.getAvgTime()));
                    endpointToLatencyInstance.put(testRecord.getWebsiteName(), latencyInstances);
                }
            }
        }

        return endpointToLatencyInstance;
    }

    private static class LatencyInstance {
        private final String time;
        private final String averageTimeinMilli;

        public LatencyInstance(String time, String averageTimeinMilli) {
            this.time = time;
            this.averageTimeinMilli = averageTimeinMilli;
        }

        public String getTime() {
            return this.time;
        }

        public String getAverageTimeinMilli() {
            return this.averageTimeinMilli;
        }
    }

}
