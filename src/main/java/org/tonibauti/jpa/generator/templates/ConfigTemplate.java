package org.tonibauti.jpa.generator.templates;

import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.main.Workspace;
import org.tonibauti.jpa.generator.templates.base.AbstractTemplate;
import org.tonibauti.jpa.generator.utils.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConfigTemplate extends AbstractTemplate
{
    private static final String[] SOURCE =
    {
        //"templates/config/DataSourceConfig.fm",
        "templates/config/JpaConfig.fm",
        "templates/config/datasource.properties.fm",
        //"templates/config/persistence.xml.fm",
    };

    private static final String[] TARGET =
    {
        //"${ClassName}DataSourceConfig.java",
        "${ClassName}JpaConfig.java",
        "${DataSourceName}_datasource.properties",
        //"${DataSourceName}_persistence.xml",
    };


    public ConfigTemplate(Workspace workspace, List<DBTable> tables)
    {
        super(workspace, tables);
    }


    @Override
    public String getSource(int index, DBTable dbTable)
    {
        return SOURCE[index];
    }


    @Override
    public String getTarget(int index, DBTable dbTable)
    {
        String target;

        if (TARGET[index].endsWith(".properties"))
        {
            target = getWorkspace().getDataSourceResourcesDir() +
                     TARGET[index].replace("${DataSourceName}", workspace.getDataSourceName());
        }
        else
        if (TARGET[index].endsWith(".xml"))
        {
            target = getWorkspace().getPersistenceResourcesDir() +
                     TARGET[index].replace("${DataSourceName}", workspace.getDataSourceName());
        }
        else
        {
            target = getWorkspace().getConfigDir() +
                     TARGET[index].replace("${ClassName}", Strings.toClassName(getWorkspace().getDataSourceName()));
        }

        return target;
    }


    @Override
    protected void preMapping(StringBuilder template)
    {
    }


    @Override
    public Map<String, Object> getMapping(int index, DBTable dbTable)
    {
        Map<String, Object> map = new HashMap<>();

        String dataSourceName = getWorkspace().getDataSourceName();
        String className      = Strings.toClassName( dataSourceName );
        String objectName     = Strings.toPropertyName( dataSourceName );

        map.put("ConfigPackage", getWorkspace().getConfigPackage());
        map.put("RepositoriesPackage", getWorkspace().getRepositoriesPackage());
        map.put("EntitiesPackage", getWorkspace().getEntitiesPackage());
        map.put("ClassName", className);
        map.put("objectName", objectName);
        map.put("DatabaseResourcesDir", getWorkspace().getDatabaseResourcesDir());
        map.put("DataSourceName", workspace.getDataSourceName());
        map.put("DataSourcePropertiesName", super.getDataSourcePropertiesName());
        map.put("DatabaseProductName", getWorkspace().getDbConnection().getDatabaseProductName());
        map.put("driverClassName", getWorkspace().getDbConnection().getDriverClassName());
        map.put("jdbcUrl", getWorkspace().getDbConnection().getJdbcUrl());
        map.put("username", getWorkspace().getDbConnection().getUsername());
        map.put("password", getWorkspace().getDbConnection().getPassword());
        map.put("dataSourceProperties", getWorkspace().getDataSourceProperties());
        map.put("jpaProperties", getWorkspace().getJpaProperties());

        map.put("xml", TARGET[index].endsWith("xml"));
        map.put("hikari", getWorkspace().getDataSourceProvider().equalsIgnoreCase("hikari"));
        map.put("tomcat", getWorkspace().getDataSourceProvider().equalsIgnoreCase("tomcat"));

        return map;
    }


    @Override
    public void generate() throws Exception
    {
        for (int index=0; index<SOURCE.length; index++)
            super.generate( index );
    }

}

