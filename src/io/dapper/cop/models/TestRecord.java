package io.dapper.cop.models;

public class TestRecord {
    private String websiteName;
    private String time;
    
    public TestRecord(String websiteName, String time) {
        this.websiteName = websiteName;
        this.time = time;
    }
    
    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }
    
    public String getWebsiteName() {
        return this.websiteName;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getTime() {
        return this.time;
    }
    
    @Override
    public String toString() {
        return this.websiteName + "  ping time " + this.time;
    }
}
