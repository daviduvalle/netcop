package io.dapper.cop.models;

/**
 * Represents a single sample taken from an
 * endpoint
 */
public class SampleRecord {
    private String endpoint;
    private String time;
    
    public SampleRecord(String endpoint, String time) {
        this.endpoint = endpoint;
        this.time = time;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getTime() {
        return this.time;
    }
    
    @Override
    public String toString() {
        return this.endpoint + "  ping time " + this.time;
    }
}
