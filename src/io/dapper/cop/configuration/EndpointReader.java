package io.dapper.cop.configuration;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads, fixes, validates a list of endpoint
 * as strings given a file of endpoints.
 */
public final class EndpointReader {

    private final String endpointsFile;

    /**
     * Reads a file of endpoints
     * @param endpointsFile input file
     */
    public EndpointReader(String endpointsFile) {
        this.endpointsFile = endpointsFile;
    }

    /**
     * Load endpoints from a file
     * @return list of endpoints
     * @throws IOException if unable to read the file
     */
    public List<String> loadEndpoints() throws IOException {

        if (endpointsFile == null || endpointsFile.isEmpty()) {
            throw new IOException("Endpoints file not specified or empty");
        }

        File inputFile = new File(endpointsFile);

        if (!inputFile.exists()) {
            throw new IOException("File doesn't exists");
        }

        String content = null;

        try {
            content = new String(Files.readAllBytes(inputFile.toPath()));
        } catch (IOException e) {
            throw e;
        }

        String[] endpoints = content.split("\n");
        endpoints = schemeFix(endpoints);

        return Arrays.stream(endpoints).filter(x -> isValid(x)).collect(Collectors.toList());
    }

    /**
     * Verify that endpoints contain scheme and
     * add it if missing.
     * @param endpoints array of raw endpoints
     * @return an array of endpoints with scheme
     */
    private String[] schemeFix(String[] endpoints) {

        String[] finalEndpoints = new String[endpoints.length];
        int count = 0;
        for (String endpoint : endpoints) {
            if (endpoint.startsWith("http://")) {
                finalEndpoints[count] = endpoint;
            } else {
                finalEndpoints[count] = "http://" + endpoint;
            }
            count++;
        }

        return finalEndpoints;
    }

    /**
     * Validates endpoints
     * @param endpoint a string representing an endpoint
     * @return true if valid URL, false otherwise
     */
    private boolean isValid(String endpoint) {
        String[] scheme = {"http"};
        UrlValidator urlValidator = new UrlValidator(scheme);

        return urlValidator.isValid(endpoint);
    }
}