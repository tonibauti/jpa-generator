package org.tonibauti.jpa.generator.main;

import org.tonibauti.jpa.generator.cli.Console;
import org.tonibauti.jpa.generator.config.Config;
import org.tonibauti.jpa.generator.mapper.Mapper;
import org.tonibauti.jpa.generator.utils.Files;
import org.tonibauti.jpa.generator.validators.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;


public class Configurer extends AbstractComponent
{

    private static final Configurer instance = new Configurer();


    private Configurer()
    {
        super();
    }


    public static Configurer getInstance()
    {
        return instance;
    }


    private Config readYaml(File configFile) throws Exception
    {
        try (FileInputStream inputStream = new FileInputStream(configFile))
        {
            return Mapper.getInstance().readYaml(inputStream, Config.class);
        }
    }


    private Config readYaml(StringBuilder configContent) throws Exception
    {
        try
        {
            return Mapper.getInstance().readYaml(configContent, Config.class);
        }
        catch (Exception e)
        {
            if (e.getMessage().contains("NullPointerException"))
            {
                // JsonMappingException
                String msg = super.getLastSplit(e.getMessage(), ":");
                msg = msg.substring(0,msg.length()-1); // remove ')'
                throw new Exception("Unrecognized value in field '" + msg + "' from source file");
            }
            else
            {
                throw e;
            }
        }
    }


    private StringBuilder readConfigContent(File configFile, File environmentFile) throws Exception
    {
        StringBuilder configContent;

        try (FileInputStream configFileInputStream = new FileInputStream(configFile))
        {
            // read config file with environment file
            if (isNotEmpty(environmentFile))
            {
                Properties env = Files.readProperties( new FileInputStream(environmentFile) );

                configContent = Files.readLines(configFileInputStream, (line) ->
                {
                    return super.replacePattern(line, "{{", "}}", env);
                });
            }
            else
            {
                configContent = Files.readStream( configFileInputStream );
            }
        }

        return configContent;
    }


    public Config readConfig(File configFile, File environmentFile) throws Exception
    {
        // read config content
        StringBuilder configContent = readConfigContent(configFile, environmentFile);
        Config config = readYaml( configContent );

        // validate config
        Validator.getInstance().validate( config );

        Console.verbose(Console.line +
                        "Config:" + Console.lineSeparator +
                        Console.line +
                        Mapper.getInstance().toYaml(config), true);

        return config;
    }


    private boolean existsFile(File file)
    {
        return (file != null && file.exists() && file.isFile());
    }


    public Config readConfig(String configFileName, String environmentFileName) throws Exception
    {
        File configFile = new File( configFileName );

        if (!existsFile(configFile))
        {
            Console.fileNotFound( configFile );
            return null;
        }

        File environmentFile = null;

        if (isNotEmpty(environmentFileName))
        {
            environmentFile = new File( environmentFileName );

            if (!existsFile(environmentFile))
            {
                Console.fileNotFound( environmentFile );
                return null;
            }
        }

        return readConfig(configFile, environmentFile);
    }

}

