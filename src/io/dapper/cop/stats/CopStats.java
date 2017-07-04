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
            System.out.println(testInstances.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
