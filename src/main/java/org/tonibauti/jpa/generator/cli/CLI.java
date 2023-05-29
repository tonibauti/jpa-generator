package org.tonibauti.jpa.generator.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;


public class CLI
{
    private static final String CONFIG_FILE_OPTION  = "f";
    private static final String ENV_FILE_OPTION     = "e";
    private static final String HELP_OPTION         = "h";
    private static final String VERBOSE_OPTION      = "v";

    private static final CLI instance = new CLI();

    private final Options options = new Options();
    private final CommandLineParser parser = new DefaultParser();
    private final HelpFormatter helpFormatter = new HelpFormatter();


    private CLI()
    {
        super();
        configure();
    }


    public static CLI getInstance()
    {
        return instance;
    }


    private void configure()
    {
        Option configFileOption = Option.builder(CONFIG_FILE_OPTION)
                                    //.longOpt("config")
                                    .argName("config file")
                                    .desc("config file (yaml format) is required")
                                    .hasArg()
                                    .required()
                                    .build();

        Option environmentFileOption = Option.builder(ENV_FILE_OPTION)
                                        //.longOpt("env")
                                        .argName("environment file")
                                        .desc("environment file (key/value format)")
                                        .hasArg()
                                        .build();

        Option helpOption = Option.builder(HELP_OPTION)
                                //.longOpt("help")
                                .desc("print this help message")
                                .build();

        Option verboseOption = Option.builder(VERBOSE_OPTION)
                                //.longOpt("verbose")
                                .desc("enable verbose")
                                .build();

        options.addOption( configFileOption );
        options.addOption( environmentFileOption );
        options.addOption( helpOption );
        options.addOption( verboseOption );
    }


    public void usage()
    {
        String cmd    = "java -jar " + Console.CLI_NAME + "-" + Console.VERSION + ".jar";
        String opts   = " [options]";
        String header = "options:";
        String footer = ""; //""More info: " + Console.URL;

        //helpFormatter.printHelp(cmd + opts, options, false);
        helpFormatter.printHelp( cmd + opts, header, options, footer, false);
    }


    public boolean parseArgs(String[] args)
    {
        try
        {
            final boolean stopAtNonOption = true;
            CommandLine commandLine = parser.parse(options, args, stopAtNonOption);

            CLIArgs cliArgs = CLIArgs.getInstance();

            if (commandLine.hasOption(CONFIG_FILE_OPTION))
            {
                String config = commandLine.getOptionValue(CONFIG_FILE_OPTION);
                cliArgs.setConfigFileName( config.trim() );
            }

            if (commandLine.hasOption(ENV_FILE_OPTION))
            {
                String environment = commandLine.getOptionValue(ENV_FILE_OPTION);
                cliArgs.setEnvironmentFileName( environment.trim() );
            }

            cliArgs.setHelp( commandLine.hasOption(HELP_OPTION) );
            cliArgs.setVerbose( commandLine.hasOption(VERBOSE_OPTION) );

            return true;
        }
        catch (Exception e)
        {
            usage();
        }

        return false;
    }

}

