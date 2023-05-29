package org.tonibauti.jpa.generator.main;

import jakarta.validation.ValidationException;
import org.tonibauti.jpa.generator.cli.CLI;
import org.tonibauti.jpa.generator.cli.CLIArgs;
import org.tonibauti.jpa.generator.cli.Console;
import org.tonibauti.jpa.generator.config.Config;


public class Main
{

    public static void main(String[] args)
    {
        try
        {
            Console.banner();

            if (!CLI.getInstance().parseArgs(args))
            {
                return;
            }

            if (CLIArgs.getInstance().isHelp())
            {
                CLI.getInstance().usage();
                return;
            }

            // read config
            Config config = Configurer
                            .getInstance()
                            .readConfig(CLIArgs.getInstance().getConfigFileName(),
                                        CLIArgs.getInstance().getEnvironmentFileName());

            // generate
            Generator generator = new Generator();
            generator.generate( config );
        }
        catch (ValidationException e)
        {
            Console.validationError( e.getMessage() );
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Console.error( e.toString() );
        }
        finally
        {
            Console.separator();
        }
    }

}

