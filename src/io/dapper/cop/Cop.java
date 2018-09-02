package io.dapper.cop;

import static io.dapper.cop.configuration.CopConfiguration.*;

import io.dapper.cop.configuration.CopConfiguration;
import io.dapper.cop.configuration.EndpointReader;
import org.apache.commons.cli.*;

import java.util.List;

/**
 * Cop CLI
 */
public final class Cop {

    private static final String FILE_OPTION = "file";
    private static final String HELP_OPTION = "help";
    private static final String PERSIST_DATA = "persist";
    private static final String SAMPLES = "samples";
    private static final String WAIT_SECONDS = "seconds";
    private static final String USAGE = "netcop --file yourfile.txt";

    /**
     * Gathers CLI args and runs the cop
     * @param args CLI args
     * @throws Exception if unable to process input parameters
     */
    public static void main(String[] args) throws Exception {
        
        Options options = new Options();

        Option fileOption = Option.builder().hasArg(true).longOpt(FILE_OPTION).required(true)
                .desc("file containing endpoints to be tested").argName("file.txt").build();
        Option persistOption = Option.builder().hasArg(true).longOpt(PERSIST_DATA).required(false)
                .desc("persist testing data and output in temporal files").argName
                ("true|false").build();
        Option samplesOption = Option.builder().hasArg(true).longOpt(SAMPLES).required(false)
                .desc("number of samples to take, default: " + CopConfiguration
                        .DEFAULT_SAMPLES_COUNT).argName("number").build();
        Option waitOption = Option.builder().hasArg(true).longOpt(WAIT_SECONDS).required(false)
                .desc("number of seconds to wait between samples, default: " + CopConfiguration
                        .DEFAULT_SECONDS_INTERVAL).argName("seconds").build();
        Option helpOption = Option.builder().hasArg(false).required(false).longOpt("help")
                .desc("print this help").build();

        options.addOption(fileOption);
        options.addOption(persistOption);
        options.addOption(samplesOption);
        options.addOption(waitOption);
        options.addOption(helpOption);

        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        boolean persistData = false;
        int waitSecondsInterval = CopConfiguration.DEFAULT_SECONDS_INTERVAL;
        int samplesCount = CopConfiguration.DEFAULT_SAMPLES_COUNT;
        HelpFormatter formatter = new HelpFormatter();

        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Parsing failed: " + e.getMessage());
            System.err.println("Example usage: "+USAGE);
            return;
        }

        if (line.hasOption(HELP_OPTION)) {
            formatter.printHelp(USAGE, options);
            return;
        }

        if (line.hasOption(PERSIST_DATA)) {
            persistData = Boolean.valueOf(line.getOptionValue(PERSIST_DATA));
        }

        if (line.hasOption(SAMPLES)) {
            samplesCount = Integer.parseInt(line.getOptionValue(SAMPLES));
            if (samplesCount == 0) {
                samplesCount = CopConfiguration.DEFAULT_SAMPLES_COUNT;
            }
        }

        if (line.hasOption(WAIT_SECONDS)) {
            waitSecondsInterval = Integer.parseInt(line.getOptionValue(WAIT_SECONDS));
            if (waitSecondsInterval < CopConfiguration.DEFAULT_SECONDS_INTERVAL) {
                waitSecondsInterval = CopConfiguration.DEFAULT_SECONDS_INTERVAL;
            }
        }

        System.out.println("Netcop version: "+CopConfiguration.VERSION);

        EndpointReader endpointReader = new EndpointReader(line.getOptionValue(FILE_OPTION));
        List<String> endpoints = endpointReader.loadEndpoints();

        System.out.println("Samples: " + samplesCount + ", wait: " +
                waitSecondsInterval + " secs, endpoints: " + endpoints.size());

        CopRunner copRunner = new CopRunner(
                endpoints,
                persistData,
                waitSecondsInterval,
                samplesCount);

        copRunner.run();
    }
}
