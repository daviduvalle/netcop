package io.dapper.cop.history;

import io.dapper.cop.models.TestInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.reflect.Type;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

/**
 *  Reads a netcop history file
 *  if it exists
 */
public class HistoryReader {

    public List<TestInstance> read() throws IOException {
        
        Path storageFile = Paths.get("/tmp/", "netcop.json");
        
        if (!storageFile.toFile().exists()) {
            throw new IOException("Storage file doesn't exists");
        }
        
        String content = new String(Files.readAllBytes(storageFile));
        
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<TestInstance>>(){}.getType();
        List<TestInstance> testInstances = gson.fromJson(content, listType);
        
        return testInstances;
    }
}
