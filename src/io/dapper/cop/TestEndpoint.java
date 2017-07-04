package io.dapper.cop;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestEndpoint {

    private TestEndpoint() {}

    public static final Map<String, String> ENDPOINTS;

    static {
        HashMap<String, String> testEndpoints = new HashMap<String, String>();
        testEndpoints.put("google", "http://www.google.com");
        //testEndpoints.put("netflix", "http://www.netflix.com");
        // testEndpoints.put("amazon", "http://www.amazon.com");
        //testEndpoints.put("facebook", "http://www.facebook.com");
        
        ENDPOINTS = Collections.unmodifiableMap(testEndpoints);
    }
}
