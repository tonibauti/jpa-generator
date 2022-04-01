package org.tonibauti.jpa.generator.cli;


public class CLIArgs
{
    private static final CLIArgs instance = new CLIArgs();

    private String source;
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


    public String getSource()
    {
        return source;
    }


    public CLIArgs setSource(String source)
    {
        this.source = source;
        return this;
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


    public CLIArgs setVerbose(boolean verbose)
    {
        this.verbose = verbose;
        return this;
    }

}

