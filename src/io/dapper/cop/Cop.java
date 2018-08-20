package io.dapper.cop;

import io.dapper.cop.configuration.CopConfiguration;
import io.dapper.cop.configuration.EndpointReader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.dapper.cop.configuration.CopConfigurationFormatter;
import io.dapper.cop.stats.CopStats;

import java.io.File;
import java.util.List;

/**
 * Cop CLI
 */
public class Cop {

    private static final String FILE_OPTION = "file";
    private static final String HELP_OPTION = "help";

    /**
     * Gathers CLI args and runs the cop
     * @param args CLI args
     * @throws Exception if unable to process input parameters
     */
    public static void main(String[] args) throws Exception {
        
        Options options = new Options();
        options.addOption(FILE_OPTION, true, "file containing endpoints each in a new line");
        options.addOption(HELP_OPTION, "prints this help");

        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            throw e;
        }

        if (line.hasOption(HELP_OPTION)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("netcop -file yourfile.txt", options);
            return;
        }

        if (!line.hasOption(FILE_OPTION)) {
            throw new IllegalArgumentException("Input file not specified as argument, terminating.");
        }

        EndpointReader endpointReader = new EndpointReader(line.getOptionValue(FILE_OPTION));
        List<String> endpoints = endpointReader.loadEndpoints();

        CopRunner copRunner = new CopRunner(endpoints, true);
        copRunner.run();
    }
}
