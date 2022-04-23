package org.tonibauti.jpa.generator.cli;

import com.github.lalyos.jfiglet.FigletFont;
import jakarta.validation.ValidationException;
import org.tonibauti.jpa.generator.utils.Resources;

import java.io.File;
import java.io.InputStream;


public class Console
{
    public static final String lineSeparator = System.lineSeparator();
    public static final String line = "----------------------------------------" + lineSeparator;

    protected static final String BANNER   = "JPA - Generator";
    protected static final String VERSION  = "1.0.0";
    protected static final String CLI_NAME = "jpa-generator";
    protected static final String URL      = "https://github.com/tonibauti";

    public static final String SUPPORTED_VERSION = "1.0.0";


    private Console() {}


    public static void banner() throws Exception
    {
        separator();

        String font = "fonts/small.flf";

        try (InputStream fontFileStream = Resources.getResourceAsStream(font))
        {
            String asciiArt = FigletFont.convertOneLine(fontFileStream, BANNER);
            System.out.print( asciiArt );
            System.out.println( "    " + VERSION );
            System.out.println( "    " + URL );
        }

        separator();
    }


    public static void separator()
    {
        System.out.println();
    }


    public static void out(String msg)
    {
        System.out.println(msg);
    }


    public static void error(String msg)
    {
        System.out.println("Error - " + msg);
    }


    public static void validationError(String msg)
    {
        System.out.println("Validation Errors: " + msg);
    }


    public static void info(String msg, Object obj)
    {
        out(msg + ": " + obj);
    }


    public static void verbose(String msg, boolean separator)
    {
        if (CLIArgs.getInstance().isVerbose())
        {
            if (msg != null)
                out( msg );

            if (separator)
                separator();
        }
    }


    public static void verbose(String msg)
    {
        verbose(msg, false);
    }


    public static void throwException(String msg) throws RuntimeException
    {
        throw new RuntimeException(msg);
    }


    public static void throwValidationException(String msg) throws RuntimeException
    {
        throw new ValidationException(msg);
    }


    public static void undefined(String msg, Object obj)
    {
        error(msg + ": '" + obj + "' not defined");
    }


    public static void fileNotFound(File file)
    {
        error("File not found: '" + file + "'");
    }


    public static void unable(String msg)
    {
        error("Unable to " + msg);
    }

}

