package io.dapper.cop.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestInstance {

    private String dateTime;
    private List<TestRecord> testRecords;

    public TestInstance() {
        this.dateTime = LocalDateTime.now().toString();
        this.testRecords = new ArrayList<TestRecord>();
    }

    public List<TestRecord> getTestRecords() {
        return this.testRecords;
    }

    public void addTestRecord(TestRecord testRecord) {
        this.testRecords.add(testRecord);
    }

    public String getInstanceDate() {
        return this.dateTime;
    }
    
    public void clear() {
        this.testRecords.clear();
    }
}
