package io.dapper.cop;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.dapper.cop.configuration.CopConfigurationFormatter;
import io.dapper.cop.stats.CopStats;

public class Cop {
    
    public static void main(String[] args) throws InterruptedException {
        
        Options options = new Options();
        options.addOption("help", "prints this help");
        options.addOption("stats", "show text based stats");
        options.addOption("graph", "generates a graph of the stats");
        options.addOption("config", "prints version and configuration");
        options.addOption("run", "runs and collect data");
        
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("Error while parsing arguments");
            e.printStackTrace();
        }
        
        if (line.hasOption("run")) {
            CopRunner copRunner = new CopRunner();
            copRunner.run();
        }
        else if (line.hasOption("config")) {
            CopConfigurationFormatter.printConfiguration();
        }
        else if (line.hasOption("stats")) {
            CopStats copStats = new CopStats();
            copStats.printStats();
        } else if (line.hasOption("graph")) {
            // TODO: generate a nice chart
        } else if (line.getArgList().size() == 0 || line.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("netcop", options);
        }
    }
}
