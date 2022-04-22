package org.tonibauti.jpa.generator.cli;


public class CLIArgs
{
    private static final CLIArgs instance = new CLIArgs();

    private String config;
    private String environment;
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


    public String getConfig()
    {
        return config;
    }


    public void setConfig(String config)
    {
        this.config = config;
    }


    public String getEnvironment()
    {
        return environment;
    }

    public void setEnvironment(String environment)
    {
        this.environment = environment;
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

