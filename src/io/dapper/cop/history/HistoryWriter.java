package io.dapper.cop.history;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import io.dapper.cop.configuration.CopConfiguration;
import io.dapper.cop.models.TestInstance;
import io.dapper.cop.models.TestRecord;

/**
 * Keeps network tests history in memory or in a file
 */
public class HistoryWriter {

    private final boolean recordToFile;
    private TestInstance testInstance;
    private List<TestInstance> testInstances;
    private File tmpFile;

    public HistoryWriter(boolean recordToFile) {

        this.testInstances = new ArrayList<>();
        this.recordToFile = recordToFile;

        if (this.recordToFile) {
            try {
                tmpFile = File.createTempFile("netcop.", null);
                System.out.println("Writing data to: " + tmpFile
                        .getAbsolutePath().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTestInstance() {
        this.testInstance = new TestInstance();
    }

    /**
     * Add record to memory storage before writing
     * it down.
     * @param testRecord a single test
     */
    public void addRecord(TestRecord testRecord) {
        this.testInstance.addTestRecord(testRecord);
    }

    /**
     * Get back a list of test instances
     * @return list of test instances
     */
    public List<TestInstance> getTestInstances() {
        return this.testInstances;
    }

    /**
     * Points to the temporal history file
     * @return a file containing test records
     */
    public File getHistoryFile() {
        return tmpFile;
    }

    /**
     * Appends a test instances (multiple test records) into a file
     * or a list depending on the value of "recordToFile"
     */
    public void write() {
        if (recordToFile) {
            Gson gson = new Gson();
            String jsonOutput = gson.toJson(this.testInstance);

            try (FileWriter writer =
                         new FileWriter(tmpFile, true)) {
                writer.write(jsonOutput);
                writer.write("\n");
                System.out.println("Writing record: " + jsonOutput);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.testInstance.clear();
            }
        } else {
            System.out.println("running!");
            testInstances.add(this.testInstance);
        }
    }
    
}
