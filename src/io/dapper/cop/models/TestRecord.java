package io.dapper.cop.models;

public class TestRecord {
    private String websiteName;
    private String avgTime;
    
    public TestRecord(String websiteName, String avgTime) {
        this.websiteName = websiteName;
        this.avgTime = avgTime;
    }
    
    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }
    
    public String getWebsiteName() {
        return this.websiteName;
    }
    
    public void setAvgTime(String avgTime) {
        this.avgTime = avgTime;
    }
    
    public String getAvgTime() {
        return this.avgTime;
    }
    
    @Override
    public String toString() {
        return this.websiteName + " avg. time " + this.avgTime;
    }
}
