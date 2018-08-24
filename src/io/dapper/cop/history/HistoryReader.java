package io.dapper.cop.history;

import io.dapper.cop.models.TestInstance;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.util.stream.Collectors;

import com.google.gson.Gson;

/**
 *  Reads a netcop history file if it exists
 */
public class HistoryReader {

    private final File tmpFile;

    /**
     * Reads a netcope history file
     * @param tmpFile file to read from
     */
    public HistoryReader(File tmpFile) {
        this.tmpFile = tmpFile;
    }

    /**
     * Reads a netcop history file and returns a list of test instances
     * @return a list of test instances
     * @throws IOException if unable to read file contents
     */
    public List<TestInstance> read() throws IOException {

        // Reads a file containing different samples stored as separate
        // JSON objects
        String content = new String(Files.readAllBytes(tmpFile.toPath()));

        String[] tests = content.split("\n");

        Gson gson = new Gson();

        List<TestInstance> output = Arrays.stream(tests).map(t ->
            gson.fromJson(t, TestInstance.class)).collect(Collectors.toList());

        return output;
    }
}
