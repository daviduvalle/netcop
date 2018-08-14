package io.dapper.cop.configuration;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EndpointReader {

    public static void main(String[] args) {
        loadEndpoints("endpoints.txt");
    }

    private static void loadEndpoints(String endpointsFile) {
        File inputFile = new File(endpointsFile);

        if (!inputFile.exists()) {
            System.out.println("input file doesn't exists, terminating");
        }

        String content = null;
        try {
            content = new String(Files.readAllBytes(inputFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] endpoints = content.split("\n");
        endpoints = schemeFix(endpoints);

        List<String> validEndpoints =
                Arrays.stream(endpoints).filter(x -> isValid(x)).collect(Collectors.toList());

        validEndpoints.forEach(x -> System.out.println(x));

    }

    /**
     * Verify that endpoints contain scheme and
     * add it if missing.
     * @param endpoints array of raw endpoints
     * @return an array of endpoints with scheme
     */
    private static String[] schemeFix(String[] endpoints) {

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
    private static boolean isValid(String endpoint) {
        String[] scheme = {"http"};
        UrlValidator urlValidator = new UrlValidator(scheme);

        return urlValidator.isValid(endpoint);
    }
}