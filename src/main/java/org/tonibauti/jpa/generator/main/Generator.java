package org.tonibauti.jpa.generator.main;

import org.tonibauti.jpa.generator.cli.Console;
import org.tonibauti.jpa.generator.config.Config;
import org.tonibauti.jpa.generator.config.GeneratorConfig;
import org.tonibauti.jpa.generator.explorer.DatabaseExplorer;
import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.mapper.Mapper;
import org.tonibauti.jpa.generator.templates.*;
import org.tonibauti.jpa.generator.utils.Files;
import org.tonibauti.jpa.generator.validators.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;


public class Generator extends AbstractComponent
{

    public Generator()
    {
        super();
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


    public void generate(File configFile, File environmentFile) throws Exception
    {
        Console.separator();

        StringBuilder configContent;

        try (FileInputStream configFileInputStream = new FileInputStream(configFile))
        {
            // read config file with environment file
            if (environmentFile != null)
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

        // read config content
        Config config = readYaml( configContent );

        // validate config file
        Validator.getInstance().validate( config );

        Console.verbose(Console.line +
                        "Config:" + Console.lineSeparator +
                        Console.line +
                        Mapper.getInstance().toYaml(config), true);

        // explore database
        DatabaseExplorer dbExplorer = new DatabaseExplorer();
        dbExplorer.explore( config );

        // generate
        generate(config.getGeneratorConfig(), dbExplorer);

        Console.out("Generated in '" + config.getGeneratorConfig().getProjectConfig().getPath() + "'");
    }


    private void generate(GeneratorConfig generatorConfig, DatabaseExplorer dbExplorer) throws Exception
    {
        Console.separator();

        final List<DBTable> tables = dbExplorer.getTables();

        Workspace workspace = new Workspace(generatorConfig, dbExplorer.getDatabaseConnection());
        workspace.create();

        if (super.bool(generatorConfig.getGenerateConfig()))
        {
            Console.out("Generating Config...");
            ConfigTemplate configTemplate = new ConfigTemplate(workspace, tables);
            configTemplate.generate();
        }

        if (super.bool(generatorConfig.getGenerateEntities()))
        {
            if (GeneratorConfig.REPOSITORIES_MODE_SPRING_DATA.equalsIgnoreCase(generatorConfig.getGenerateMode()))
            {
                Console.out("Generating Entities...");
                EntityTemplate entityTemplate = new EntityTemplate(workspace, tables);
                entityTemplate.generate();
            }
            else
            if (GeneratorConfig.REPOSITORIES_MODE_NATIVE.equalsIgnoreCase(generatorConfig.getGenerateMode()))
            {
                Console.out("Generating Entities...");
                EntityTemplate entityTemplate = new EntityTemplate(workspace, tables);
                entityTemplate.generate();
            }
        }

        if (super.bool(generatorConfig.getGenerateCrudRepositories()))
        {
            if (GeneratorConfig.REPOSITORIES_MODE_SPRING_DATA.equalsIgnoreCase(generatorConfig.getGenerateMode()))
            {
                Console.out("Generating Crud Repositories...");
                CrudRepositoryTemplate crudRepositoryTemplate = new CrudRepositoryTemplate(workspace, tables);
                crudRepositoryTemplate.generate();
            }
            else
            if (GeneratorConfig.REPOSITORIES_MODE_NATIVE.equalsIgnoreCase(generatorConfig.getGenerateMode()))
            {
                Console.out("Generating Crud Repositories...");
                CrudNativeRepositoryTemplate crudNativeRepositoryTemplate = new CrudNativeRepositoryTemplate(workspace, tables);
                crudNativeRepositoryTemplate.generate();
            }
        }

        if (super.bool(generatorConfig.getGenerateCrudRepositoriesTest()))
        {
            Console.out("Generating Crud Repositories Test...");
            CrudRepositoryTestTemplate crudRepositoryTestTemplate = new CrudRepositoryTestTemplate(workspace, tables);
            crudRepositoryTestTemplate.generate();
        }

        if (super.bool(generatorConfig.getGenerateCatalogConstants()))
        {
            Console.out("Generating Catalog Constants...");
            CatalogConstantsTemplate catalogConstantsTemplate = new CatalogConstantsTemplate(workspace, tables);
            catalogConstantsTemplate.generate();
        }

        Console.out("");
    }

}

