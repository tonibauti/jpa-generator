package org.tonibauti.jpa.generator.main;

import jakarta.validation.ValidationException;
import org.tonibauti.jpa.generator.cli.CLI;
import org.tonibauti.jpa.generator.cli.CLIArgs;
import org.tonibauti.jpa.generator.cli.Console;

import java.io.File;


public class Main
{

    private static boolean existsFile(File file)
    {
        return (file != null && file.exists() && file.isFile());
    }


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

            File configFile = new File( CLIArgs.getInstance().getConfig() );

            if (!existsFile(configFile))
            {
                Console.fileNotFound( configFile );
                return;
            }

            File environmentFile = null;

            if (CLIArgs.getInstance().getEnvironment() != null)
            {
                environmentFile = new File( CLIArgs.getInstance().getEnvironment() );

                if (!existsFile(environmentFile))
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

