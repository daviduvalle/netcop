package io.dapper.cop;

import static io.dapper.cop.configuration.CopConfiguration.*;
import io.dapper.cop.configuration.EndpointReader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.List;

/**
 * Cop CLI
 */
public final class Cop {

    private static final String FILE_OPTION = "file";
    private static final String HELP_OPTION = "help";
    private static final String PERSIST_DATA = "persist";

    /**
     * Gathers CLI args and runs the cop
     * @param args CLI args
     * @throws Exception if unable to process input parameters
     */
    public static void main(String[] args) throws Exception {
        
        Options options = new Options();
        options.addOption(FILE_OPTION, true, "file containing endpoints each in a new line");
        options.addOption(PERSIST_DATA, true, "persists testing data and statistics in temporal" +
                " files if enabled");
        options.addOption(HELP_OPTION, "prints this help");

        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        boolean persistData = false;
        
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            throw e;
        }

        if (line.hasOption(HELP_OPTION)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("netcop -file yourfile.txt -persist false", options);
            return;
        }

        if (!line.hasOption(FILE_OPTION)) {
            throw new IllegalArgumentException("Input file not specified as argument, terminating.");
        }

        if (line.hasOption(PERSIST_DATA)) {
            persistData = Boolean.valueOf(line.getOptionValue(PERSIST_DATA));
        }

        EndpointReader endpointReader = new EndpointReader(line.getOptionValue(FILE_OPTION));
        List<String> endpoints = endpointReader.loadEndpoints();

        CopRunner copRunner = new CopRunner(endpoints, persistData, DEFAULT_SECONDS_INTERVAL, DEFAULT_MAX_RUN_COUNT);
        copRunner.run();
    }
}
