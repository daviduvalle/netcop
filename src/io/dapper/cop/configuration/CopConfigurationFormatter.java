package io.dapper.cop.configuration;

public class CopConfigurationFormatter {
    
    public static void printConfiguration() {
        System.out.println("****");
        System.out.println("Version: "+CopConfiguration.COP_VERSION);
        System.out.println("Ping interval (seconds) "+CopConfiguration.SECONDS_INTERVAL);
        System.out.println("****");
    }

}
