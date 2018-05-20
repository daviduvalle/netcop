package io.dapper.cop.models;

import java.text.DecimalFormat;

/**
 * Represents stats of an endpoint, exposes avg as is
 * used by a comparator to sort
 */
public class EndpointStats {
    private final String endpoint;
    private final int dataPoints;
    private final double average;
    private final double median;
    private final double stdDeviation;

    public EndpointStats(String endpoint, int dataPoints,
                         double average,
                         double median,
                         double stdDeviation) {
        this.endpoint = endpoint;
        this.dataPoints = dataPoints;
        this.average = formatter(average);
        this.stdDeviation = formatter(stdDeviation);
        this.median = formatter(median);
    }

    private double formatter(double input) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(input));
    }

    public double getAverage() {
        return this.average;
    }

    @Override
    public String toString() {
        return String.format("%s %d %.2f %.2f %.2f",
                this.endpoint,
                this.dataPoints,
                this.average,
                this.median,
                this.stdDeviation);
    }
}
