package org.tonibauti.jpa.generator.main;

import org.tonibauti.jpa.generator.cli.Console;
import org.tonibauti.jpa.generator.config.Config;
import org.tonibauti.jpa.generator.config.GeneratorConfig;
import org.tonibauti.jpa.generator.explorer.DatabaseExplorer;
import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.templates.CatalogConstantsTemplate;
import org.tonibauti.jpa.generator.templates.ConfigTemplate;
import org.tonibauti.jpa.generator.templates.CrudNativeRepositoryTemplate;
import org.tonibauti.jpa.generator.templates.CrudRepositoryTemplate;
import org.tonibauti.jpa.generator.templates.CrudRepositoryTestTemplate;
import org.tonibauti.jpa.generator.templates.EntityTemplate;

import java.util.List;


public class Generator extends AbstractComponent
{

    public Generator()
    {
        super();
    }


    public void generate(Config config) throws Exception
    {
        if (isNullOrEmpty(config))
            return;

        Console.separator();

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
            if (GeneratorConfig.REPOSITORIES_MODE_NATIVE_SQL.equalsIgnoreCase(generatorConfig.getGenerateMode()))
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
            if (GeneratorConfig.REPOSITORIES_MODE_NATIVE_SQL.equalsIgnoreCase(generatorConfig.getGenerateMode()))
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

        Console.separator();
    }

}

