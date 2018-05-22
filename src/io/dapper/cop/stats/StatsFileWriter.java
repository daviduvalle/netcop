package io.dapper.cop.stats;

import com.google.gson.Gson;
import io.dapper.cop.models.EndpointStats;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Writes stats into a file in the OS temp files directory
 */
public final class StatsFileWriter {

    private Queue<EndpointStats> stats;

    public StatsFileWriter(Queue<EndpointStats> stats) {
        this.stats = stats;
    }

    /**
     * Writes stats
     */
    public void writeStats() {

        Queue<EndpointStats> copy = new PriorityQueue<>(stats);
        List<EndpointStats> finalList = new ArrayList<>();

        while (!copy.isEmpty()) {
            finalList.add(copy.poll());
        }

        try {
            File temp = File.createTempFile("netcop_stats", null);

            System.out.println();
            System.out.println("Writing stats at: "+temp.getAbsolutePath());
            Gson gson = new Gson();
            String jsonOutput = gson.toJson(finalList);
            FileWriter fw = new FileWriter(temp);
            fw.write(jsonOutput);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
