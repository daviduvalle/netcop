package io.dapper.cop.stats;

import java.util.*;

import io.dapper.cop.models.TestInstance;
import io.dapper.cop.models.SampleRecord;

/**
 * Computes basic statistics based on saved data
 */
public final class CopStats {

    private final List<TestInstance> testInstances;
    private final boolean writeStats;

    /**
     * Provides stats
     * @param testInstances list of test instances
     */
    public CopStats(List<TestInstance> testInstances, boolean writeStats) {
        this.testInstances = testInstances;
        this.writeStats = writeStats;
    }

    /**
     * Shows stats in the STOUT and also writes them into a file
     */
    public void showStats() {
        Map<String, Queue<Double>> reportData = this.loadData(this.testInstances);
        Queue<EndpointStats> stats = computeStats(reportData);

        if (stats.isEmpty()) {
            System.out.println("No stats available");
            return;
        }

        if (this.writeStats) {
            StatsFileWriter statsFileWriter = new StatsFileWriter(stats);
            statsFileWriter.writeStats();
        }

        System.out.println();
        System.out.format("%s%30s%10s%10s%10s",
                "Endpoint", "Samples", "Average", "Median", "Deviation");
        System.out.println();

        while (!stats.isEmpty()) {
            System.out.println(stats.poll());
        }
    }

    /**
     * Loads test instances into a map where the key is the endpoint being tested
     * @param testInstances latency test instances
     * @return a map containing key = website, value = list of test instances
     */
    private Map<String, Queue<Double>> loadData(
            List<TestInstance> testInstances) {

        HashMap<String, Queue<Double>> endpointToLatencyInstance = new
                HashMap<>();

        for (TestInstance testInstance : testInstances) {
            for (SampleRecord sampleRecord : testInstance.getSampleRecords()) {
                if (endpointToLatencyInstance.containsKey(sampleRecord.getEndpoint())) {
                    endpointToLatencyInstance.get(sampleRecord.getEndpoint()).add(Double
                            .valueOf(sampleRecord.getTime()));
                } else {
                    Queue<Double> latencyInstances =
                            new PriorityQueue<>();
                    latencyInstances.add(Double.valueOf(sampleRecord.getTime()));
                    endpointToLatencyInstance.put(sampleRecord.getEndpoint(), latencyInstances);
                }
            }
        }

        return endpointToLatencyInstance;
    }

    /**
     *  Computes avg, median, and standard deviation
     *  @return a priority queue sorted by endpoint avg. latency
     */
    private Queue<EndpointStats> computeStats(Map<String, Queue<Double>>
                                        reportData) {

        Comparator<EndpointStats> comparator = Comparator.comparing
                (EndpointStats::getAverage);

        Queue<EndpointStats> statsQueue =
                new PriorityQueue<EndpointStats>(reportData.keySet().size(),
                        comparator);

        for (String endpoint : reportData.keySet()) {
            Queue<Double> latencies = reportData.get(endpoint);
            Queue<Double> latenciesCopy = new PriorityQueue<>(latencies);

            double average = 0;
            double median = 0;
            int latenciesLength = latencies.size();
            int medianPos = latenciesLength / 2;
            boolean isOdd = latenciesLength % 2 != 0 ? true : false;
            medianPos = isOdd ? medianPos : medianPos - 1;

            // Compute average and median
            for (int i = 0; i < latenciesLength; i++) {
                double tmp = latencies.poll();
                if (isOdd && i == medianPos) {
                    median = tmp;
                } else if (!isOdd && i == medianPos){
                    median = (tmp + latencies.peek()) / 2;
                }

                average += tmp;
            }

            average = average / latenciesLength;

            // Compute std. deviation
            double numerator = 0;
            double denominator = latenciesLength - 1;
            for (Double latencyInstance : latenciesCopy) {
                numerator += Math.pow(latencyInstance - average, 2);
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
}