package org.tonibauti.jpa.generator.main;

import org.tonibauti.jpa.generator.config.GeneratorConfig;
import org.tonibauti.jpa.generator.config.ProjectConfig;
import org.tonibauti.jpa.generator.explorer.metada.DBConnection;
import org.tonibauti.jpa.generator.utils.Strings;

import java.io.File;
import java.util.Map;


public class Workspace
{
    public static final String SRC          = "src";
    public static final String MAIN         = "main";
    public static final String JAVA         = "java";
    public static final String TEST         = "test";
    public static final String RESOURCES    = "resources";
    public static final String DATABASE     = "database";
    public static final String DATA_SOURCE  = "datasource";
    public static final String PERSISTENCE  = "persistence";
    public static final String ENTITIES     = "entities";
    public static final String CATALOGS     = "catalogs";
    public static final String REPOSITORIES = "repositories";
    public static final String CRUD         = "crud";
    public static final String CUSTOM       = "custom";
    public static final String BASE         = "base";
    public static final String CONSTRAINTS  = "constraints";

    private GeneratorConfig generatorConfig;
    private DBConnection dbConnection;


    private Workspace() {}


    public Workspace(GeneratorConfig generatorConfig, DBConnection dbConnection)
    {
        this.generatorConfig = generatorConfig;
        this.dbConnection = dbConnection;
    }


    public DBConnection getDbConnection()
    {
        return dbConnection;
    }


    private boolean bool(Boolean value)
    {
        return (value != null && value);
    }


    //
    // DataSource
    //

    public String getDataSourceProvider()
    {
        return generatorConfig.getDataSourceConfig().getProvider();
    }


    public String getDataSourceName()
    {
        return generatorConfig.getDataSourceConfig().getName();
    }


    public Map<String, String> getDataSourceProperties()
    {
        return generatorConfig.getDataSourceConfig().getProperties();
    }


    public Map<String, String> getJpaProperties()
    {
        return generatorConfig.getJpaConfig().getProperties();
    }


    //
    // Project
    //

    public String getProjectType()
    {
        return generatorConfig.getProjectConfig().getType();
    }


    public String getProjectPath()
    {
        return generatorConfig.getProjectConfig().getPath();
    }


    public String getConfigPackage()
    {
        return generatorConfig.getProjectConfig().getConfigPackage();
    }


    public String getPersistencePackage()
    {
        return generatorConfig.getProjectConfig().getPersistencePackage();
    }


    public String getPersistenceTestPackage()
    {
        return generatorConfig.getProjectConfig().getPersistenceTestPackage();
    }


    public String getEncoder()
    {
        return generatorConfig.getProjectConfig().getEncoder();
    }


    public boolean isSpringDataMode()
    {
        return GeneratorConfig.REPOSITORIES_MODE_SPRING_DATA.equalsIgnoreCase(generatorConfig.getGenerateMode());
    }


    public boolean isNativeSqlMode()
    {
        return GeneratorConfig.REPOSITORIES_MODE_NATIVE_SQL.equalsIgnoreCase(generatorConfig.getGenerateMode());
    }


    public boolean isEntities()
    {
        return bool(generatorConfig.getGenerateEntities());
    }


    public boolean isCrudRepositories()
    {
        return (bool(generatorConfig.getGenerateCrudRepositories()) && isSpringDataMode());
    }


    public boolean isCrudNativeRepositories()
    {
        return (bool(generatorConfig.getGenerateCrudRepositories()) && isNativeSqlMode());
    }


    public boolean isCrudRepositoriesTest()
    {
        return bool(generatorConfig.getGenerateCrudRepositoriesTest());
    }


    public boolean isCatalogConstants()
    {
        return bool(generatorConfig.getGenerateCatalogConstants());
    }


    public boolean isUseBuilders()
    {
        return bool(generatorConfig.getProjectConfig().getUseBuilders());
    }


    public boolean isUseTimestampLikeDate()
    {
        return bool(generatorConfig.getProjectConfig().getUseTimestampLikeDate());
    }


    public boolean isUseVarchar36LikeUuid()
    {
        return bool(generatorConfig.getProjectConfig().getUseVarchar36LikeUuid());
    }


    public String getLogAnnotation()
    {
        return generatorConfig.getProjectConfig().getLogAnnotation();
    }


    public boolean isLog()
    {
        return (getLogAnnotation() != null && !getLogAnnotation().trim().isEmpty());
    }


    public boolean isSpring()
    {
        return ProjectConfig.PROJECT_TYPE_SPRING.equalsIgnoreCase(getProjectType());
    }


    public boolean isQuarkus()
    {
        return ProjectConfig.PROJECT_TYPE_QUARKUS.equalsIgnoreCase(getProjectType());
    }


    //
    // Paths
    //

    private void createDir(String dirName)
    {
        if (Strings.isNullOrEmpty(dirName))
            return;

        File dir = new File(dirName);

        if (!dir.exists())
            dir.mkdirs();
    }


    private String toJavaPath(String path)
    {
        path = path.replace('\\', '/');

        if (!path.endsWith("/"))
            return path + "/";
        else
            return path;
    }


    private String packageToPath(String packageName)
    {
        return packageName.replace('.', '/');
    }


    public String getConfigPackagePath()
    {
        return toJavaPath( packageToPath(getConfigPackage()) );
    }


    public String getPersistencePackagePath()
    {
        return toJavaPath( packageToPath(getPersistencePackage()) );
    }


    public String getPersistenceTestPackagePath()
    {
        return toJavaPath( packageToPath(getPersistenceTestPackage()) );
    }


    public String getProjectDir()
    {
        return toJavaPath( getProjectPath() );
    }


    public String getSrcDir()
    {
        return getProjectDir() + toJavaPath( SRC );
    }


    public String getMainDir()
    {
        return getSrcDir() + toJavaPath( MAIN );
    }


    public String getJavaDir()
    {
        return getMainDir() + toJavaPath( JAVA );
    }


    public String getResourcesDir()
    {
        return getMainDir() + toJavaPath( RESOURCES );
    }


    public String getTestDir()
    {
        return getSrcDir() + toJavaPath( TEST );
    }


    public String getJavaTestDir()
    {
        return getTestDir() + toJavaPath( JAVA );
    }


    public String getResourcesTestDir()
    {
        return getTestDir() + toJavaPath( RESOURCES );
    }


    public String getConfigDir()
    {
        return getJavaDir() + getConfigPackagePath();
    }


    public String getPersistenceDir()
    {
        return getJavaDir() + getPersistencePackagePath();
    }


    public String getPersistenceTestDir()
    {
        return getJavaTestDir() + getPersistenceTestPackagePath();
    }


    public String getDataSourcePersistenceDir()
    {
        return getPersistenceDir() + toJavaPath( getDataSourceName().toLowerCase() );
    }


    public String getDataSourcePersistenceTestDir()
    {
        return getPersistenceTestDir() + toJavaPath( getDataSourceName().toLowerCase() );
    }


    public String getEntitiesDir()
    {
        return getDataSourcePersistenceDir() + toJavaPath( ENTITIES );
    }


    public String getCatalogConstantsDir()
    {
        return getEntitiesDir() + toJavaPath( CATALOGS );
    }


    public String getRepositoriesDir()
    {
        return getDataSourcePersistenceDir() + toJavaPath( REPOSITORIES );
    }


    public String getRepositoriesTestDir()
    {
        return getDataSourcePersistenceTestDir() + toJavaPath( REPOSITORIES );
    }


    public String getCrudRepositoriesDir()
    {
        return getRepositoriesDir() + toJavaPath( CRUD );
    }


    public String getCustomRepositoriesDir()
    {
        return getRepositoriesDir() + toJavaPath( CUSTOM );
    }


    public String getCrudRepositoriesTestDir()
    {
        return getRepositoriesTestDir() + toJavaPath( CRUD );
    }


    public String getCustomRepositoriesTestDir()
    {
        return getRepositoriesTestDir() + toJavaPath( CUSTOM );
    }


    public String getBaseCrudNativeRepositoriesDir()
    {
        return getCrudRepositoriesDir() + toJavaPath( BASE );
    }


    public String getBaseRepositoriesTestDir()
    {
        return getRepositoriesTestDir() + toJavaPath( "_"+BASE );
    }


    public String getBaseConstraintsRepositoriesTestDir()
    {
        return getBaseRepositoriesTestDir() + toJavaPath( CONSTRAINTS );
    }


    public String getDatabaseResourcesDir()
    {
        return DATABASE;
    }


    public String getDataSourceResourcesDir()
    {
        return getResourcesDir() + toJavaPath( getDatabaseResourcesDir() );
    }


    public String getPersistenceResourcesDir()
    {
        return getResourcesDir() + toJavaPath( getDatabaseResourcesDir() );
    }


    //
    // Packages
    //

    private String getPackage(String path)
    {
        path = path.replace('\\', '/');

        if (path.endsWith("/"))
            path = path.substring(0, path.length()-1);

        int index = path.indexOf( JAVA );

        if (index >= 0)
            index += (JAVA.length() + 1);
        else
            index = 0;

        return path.substring(index).replace('/', '.');
    }


    public String getEntitiesPackage()
    {
        return getPackage( getEntitiesDir() );
    }


    public String getCatalogConstantsPackage()
    {
        return getPackage( getCatalogConstantsDir() );
    }


    public String getRepositoriesPackage()
    {
        return getPackage( getRepositoriesDir() );
    }


    public String getRepositoriesTestPackage()
    {
        return getPackage( getRepositoriesTestDir() );
    }


    public String getCrudRepositoriesPackage()
    {
        return getPackage( getCrudRepositoriesDir() );
    }


    public String getCrudRepositoriesTestPackage()
    {
        return getPackage( getCrudRepositoriesTestDir() );
    }


    public String getBaseCrudNativeRepositoriesPackage()
    {
        return getPackage( getBaseCrudNativeRepositoriesDir() );
    }


    public String getBaseRepositoriesTestPackage()
    {
        return getPackage( getBaseRepositoriesTestDir() );
    }


    public String getBaseConstraintsRepositoriesTestPackage()
    {
        return getPackage( getBaseConstraintsRepositoriesTestDir() );
    }



    public void create()
    {
        // root
        createDir( getMainDir() );
        createDir( getJavaDir() );
        createDir( getResourcesDir() );

        // config
        createDir( getConfigDir() );

        // resources
        createDir( getResourcesDir() );
        createDir( getDataSourceResourcesDir() );
        //createDir( getPersistenceResourcesDir() );

        // entities
        if (isEntities())
        {
            createDir( getEntitiesDir() );
        }

        // entities / catalogs
        if (isCatalogConstants())
        {
            createDir( getCatalogConstantsDir() );
        }

        // crud repositories
        if (isCrudRepositories() || isCrudNativeRepositories())
        {
            createDir( getRepositoriesDir() );

            createDir( getCrudRepositoriesDir() );
            createDir( getCustomRepositoriesDir() );

            if (isCrudNativeRepositories())
                createDir( getBaseCrudNativeRepositoriesDir() );
        }

        // test crud repositories
        if (isCrudRepositoriesTest())
        {
            createDir( getTestDir() );
            createDir( getJavaTestDir() );
            //createDir( getResourcesTestDir() );
            createDir( getRepositoriesTestDir() );

            createDir( getBaseRepositoriesTestDir() );
            createDir( getBaseConstraintsRepositoriesTestDir() );

            createDir( getCrudRepositoriesTestDir());
            createDir( getCustomRepositoriesTestDir());
        }
    }

}

