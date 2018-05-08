package io.dapper.cop.history;

import io.dapper.cop.configuration.CopConfiguration;
import io.dapper.cop.models.TestInstance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

/**
 *  Reads a netcop history file
 *  if it exists
 */
public class HistoryReader {

    private final File tmpFile;

    public HistoryReader(File tmpFile) {
        this.tmpFile = tmpFile;
    }

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
