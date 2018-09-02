package io.dapper.cop.history;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import io.dapper.cop.models.TestInstance;
import io.dapper.cop.models.SampleRecord;

/**
 * Keeps network tests history in memory or in a file
 */
public class HistoryWriter {

    private final boolean recordToFile;
    private final List<TestInstance> testInstances;
    private TestInstance testInstance;
    private File tmpFile;

    public HistoryWriter(boolean recordToFile) {

        this.testInstances = new ArrayList<>();
        this.recordToFile = recordToFile;
    }

    public void createTestInstance() {
        this.testInstance = new TestInstance();
    }

    /**
     * Add record to memory storage before writing
     * it down.
     * @param sampleRecord a single test
     */
    public void addRecord(SampleRecord sampleRecord) {
        this.testInstance.addTestRecord(sampleRecord);
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
            FileWriter writer = null;

            try {
                this.tmpFile = getTempFile();
                writer = new FileWriter(this.tmpFile, true);
                writer.write(jsonOutput);
                writer.write("\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.print(".");
        } else {
            System.out.print(".");
            testInstances.add(this.testInstance);
        }
    }

    /**
     * Creates a new temp file or return a
     * reference to an existing one
     * @return temporal file
     * @throws IOException if cannot create a temp file
     */
    private File getTempFile() throws IOException {
        if (this.tmpFile == null) {
            tmpFile = File.createTempFile("netcop.", null);
            System.out.println("Writing collected samples in: "
                    + tmpFile.getAbsolutePath().toString());
        }

        return tmpFile;
    }
    
}
