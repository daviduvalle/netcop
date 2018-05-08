package io.dapper.cop.history;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;

import io.dapper.cop.configuration.CopConfiguration;
import io.dapper.cop.models.TestInstance;
import io.dapper.cop.models.TestRecord;

/**
 * Keeps network tests history
 */
public class HistoryWriter {

    private TestInstance testInstance;
    private File tmpFile;

    public HistoryWriter() {

        try {
            tmpFile = File.createTempFile("netcop", null);
            System.out.println("Writing data to: " + tmpFile
                    .getAbsolutePath().toString());
        } catch (IOException e) {
            e.printStackTrace();
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
     * Actually writes the data into the default storage
     * location.
     */
    public File write() {
        Gson gson = new Gson();
        String jsonOutput = gson.toJson(this.testInstance);

        try (FileWriter writer =
                new FileWriter(tmpFile,true)) {
            writer.write(jsonOutput);
            writer.write("\n");
            System.out.println("Writing record: "+jsonOutput);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.testInstance.clear();
        }

        return tmpFile;
    }
    
}
