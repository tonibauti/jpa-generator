package org.tonibauti.jpa.generator.cli;

import org.apache.commons.cli.*;


public class CLI
{
    private static final String FILE_OPTION     = "f";
    private static final String HELP_OPTION     = "h";
    private static final String VERBOSE_OPTION  = "v";

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
        Option sourceOption = Option.builder(FILE_OPTION)
                                .argName("config file")
                                .desc("config file (yaml format) is required")
                                .hasArg()
                                .required()
                                .build();

        Option helpOption = Option.builder(HELP_OPTION)
                                //.longOpt("help")
                                .desc("print this help message")
                                .build();

        Option verboseOption = Option.builder(VERBOSE_OPTION)
                                //.longOpt("verbose")
                                .desc("enable verbose")
                                .build();

        options.addOption( sourceOption );
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

            if (commandLine.hasOption(FILE_OPTION))
            {
                String source = commandLine.getOptionValue(FILE_OPTION);
                cliArgs.setSource( source.trim() );
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

