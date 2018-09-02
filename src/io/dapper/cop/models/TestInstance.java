package io.dapper.cop.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class TestInstance {

    private String dateTime;
    private List<SampleRecord> sampleRecords;

    public TestInstance() {
        this.dateTime = LocalDateTime.now().toString();
        this.sampleRecords = new ArrayList<SampleRecord>();
    }

    public List<SampleRecord> getSampleRecords() {
        return this.sampleRecords;
    }

    public void addTestRecord(SampleRecord sampleRecord) {
        this.sampleRecords.add(sampleRecord);
    }

    public String getInstanceDate() {
        return this.dateTime;
    }
    
    public void clear() {
        this.sampleRecords.clear();
    }
}
