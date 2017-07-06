package io.dapper.cop.stats;

import java.io.IOException;
import java.util.List;

import io.dapper.cop.history.HistoryReader;
import io.dapper.cop.models.TestInstance;

public class CopStats {

    private final HistoryReader historyReader;
    
    public CopStats() {
        historyReader = new HistoryReader();
    }
    
    public void printStats() {
        try {
            List<TestInstance> testInstances = historyReader.read();
            // Iterate over the instances
            // Create a hashmap where key is the website and value
            // a list of time/avg response sorted by time
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
