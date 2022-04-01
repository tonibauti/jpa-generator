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
            }
            else
            {
                String sourceFileName = CLIArgs.getInstance().getSource();
                File sourceFile = new File( sourceFileName );

                if (sourceFile.exists() && sourceFile.isFile())
                {
                    Generator generator = new Generator();
                    generator.generate( sourceFile );
                }
                else
                {
                    Console.fileNotFound( sourceFile );
                }
            }
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

