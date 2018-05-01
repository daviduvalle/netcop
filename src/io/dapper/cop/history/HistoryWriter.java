package io.dapper.cop.history;

import java.io.BufferedWriter;
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

    public HistoryWriter() {
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
    public void write() {
        Gson gson = new Gson();
        String jsonOutput = gson.toJson(this.testInstance);
        
        Path storageFile = Paths.get(CopConfiguration.TMP_DIR,
                CopConfiguration.STORAGE_FILE);
        
        if (!storageFile.toFile().exists()) {
            try { 
                storageFile.toFile().createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter writer = 
                Files.newBufferedWriter(storageFile,
                        StandardOpenOption.APPEND)) {
            writer.write(jsonOutput);
            writer.write("\n");
            System.out.println("Writing record: "+jsonOutput);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.testInstance.clear();
        }
    }
    
}
