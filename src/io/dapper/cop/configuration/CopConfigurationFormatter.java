package io.dapper.cop.configuration;

public class CopConfigurationFormatter {
    
    public static void printConfiguration() {
        System.out.println("****");
        System.out.println("Version: "+CopConfiguration.COP_VERSION);
        System.out.println("Ping count: "+CopConfiguration.PING_COUNT);
        System.out.println("Ping interval (seconds) "+CopConfiguration.SECONDS_INTERVAL);
        System.out.println("Threads: "+CopConfiguration.BG_THREADS);
        System.out.println("****");
    }

}
