package org.tonibauti.jpa.generator.cli;


public class CLIArgs
{
    private static final CLIArgs instance = new CLIArgs();

    private String configFileName;
    private String environmentFileName;
    private boolean help;
    private boolean verbose;


    private CLIArgs()
    {
        super();
    }


    public static CLIArgs getInstance()
    {
        return instance;
    }


    public String getConfigFileName()
    {
        return configFileName;
    }


    public void setConfigFileName(String configFileName)
    {
        this.configFileName = configFileName;
    }


    public String getEnvironmentFileName()
    {
        return environmentFileName;
    }

    public void setEnvironmentFileName(String environmentFileName)
    {
        this.environmentFileName = environmentFileName;
    }


    public boolean isHelp()
    {
        return help;
    }


    public void setHelp(boolean help)
    {
        this.help = help;
    }


    public boolean isVerbose()
    {
        return verbose;
    }


    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

}

