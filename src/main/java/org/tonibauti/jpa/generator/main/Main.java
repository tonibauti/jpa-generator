package org.tonibauti.jpa.generator.main;

import jakarta.validation.ValidationException;
import org.tonibauti.jpa.generator.cli.CLI;
import org.tonibauti.jpa.generator.cli.CLIArgs;
import org.tonibauti.jpa.generator.cli.Console;

import java.io.File;


public class Main
{

    public static void main(String[] args)
    {
        try
        {
            Console.banner();

            if (!CLI.getInstance().parseArgs(args))
                return;

            if (CLIArgs.getInstance().isHelp())
            {
                CLI.getInstance().usage();
                return;
            }

            File configFile = new File( CLIArgs.getInstance().getConfig() );

            if (!configFile.exists() || !configFile.isFile())
            {
                Console.fileNotFound( configFile );
                return;
            }

            File environmentFile = null;

            if (CLIArgs.getInstance().getEnvironment() != null)
            {
                environmentFile = new File( CLIArgs.getInstance().getEnvironment() );

                if (!environmentFile.exists() || !environmentFile.isFile())
                {
                    Console.fileNotFound( environmentFile );
                    return;
                }
            }

            Generator generator = new Generator();
            generator.generate(configFile, environmentFile);
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

