package io.dapper.cop;

import io.dapper.cop.configuration.CopConfiguration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.dapper.cop.configuration.CopConfigurationFormatter;
import io.dapper.cop.stats.CopStats;

import java.io.File;

/**
 * Cop CLI
 */
public class Cop {
    
    public static void main(String[] args) throws InterruptedException {
        
        Options options = new Options();
        options.addOption("help", "prints this help");
        options.addOption("stats", "show text based stats");
        options.addOption("config", "prints version and configuration");
        
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("Error while parsing arguments");
            e.printStackTrace();
        }

        if (line.getOptions().length == 0) {
            CopRunner copRunner = new CopRunner();
            System.out.println("Netcop "+ CopConfiguration.COP_VERSION);
            copRunner.run();
        }
        else if (line.hasOption("config")) {
            CopConfigurationFormatter.printConfiguration();
        }
        else if (line.hasOption("stats")) {
            //CopStats copStats = new CopStats();
            //copStats.printStats();
        } else if (line.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("netcop", options);
        }
    }
}
