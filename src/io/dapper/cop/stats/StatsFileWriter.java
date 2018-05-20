package io.dapper.cop.stats;

import com.google.gson.Gson;
import io.dapper.cop.models.EndpointStats;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

/**
 * Writes stats into a file in the OS temp files directory
 */
public class StatsFileWriter {

    private Queue<EndpointStats> stats;

    public StatsFileWriter(Queue<EndpointStats> stats) {
        this.stats = stats;
    }

    /**
     * Writes stats
     * TODO: write them in the sorted order
     */
    public void writeStats() {

        String jsonOutput = null;
        File temp = null;

        try {
            temp = File.createTempFile("netcop_stats", null);
            System.out.println();
            System.out.println("Writing stats in: "+temp.getAbsolutePath());
            Gson gson = new Gson();
            jsonOutput = gson.toJson(stats);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fw = new FileWriter(temp)) {
            fw.write(jsonOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
