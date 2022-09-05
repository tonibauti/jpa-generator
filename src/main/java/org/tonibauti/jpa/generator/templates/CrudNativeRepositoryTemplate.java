package org.tonibauti.jpa.generator.templates;

import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.main.Workspace;
import org.tonibauti.jpa.generator.templates.base.AbstractTemplate;
import org.tonibauti.jpa.generator.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// https://www.postgresql.org/docs/12/functions-json.html


public class CrudNativeRepositoryTemplate extends AbstractTemplate
{
    private boolean isPostgresJson;

    private static final String[] SOURCE =
    {
        "templates/repositories/ICrudNativeRepository.fm",
        "templates/repositories/AbstractCrudNativeRepository.fm",
        "templates/repositories/CrudNativeEntityData.fm",
        "templates/repositories/CrudNativeRepository.fm",
        "templates/repositories/QueryResultMapper.fm",
    };

    private static final String[] TARGET =
    {
        "CrudNativeRepository.java",
        "AbstractCrudNativeRepository.java",
        "CrudNativeEntityData.java",
        "${ClassName}CrudNativeRepository.java",
        "QueryResultMapper.java",
    };


    public CrudNativeRepositoryTemplate(Workspace workspace, List<DBTable> tables)
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
        if (TARGET[index].contains("${ClassName}"))
        {
            String className = Strings.toClassName( dbTable.getName() );
            return getWorkspace().getCrudRepositoriesDir() + TARGET[index].replace("${ClassName}", className);
        }
        else
        {
            // base
            return getWorkspace().getBaseCrudNativeRepositoriesDir() + TARGET[index];
        }
    }


    @Override
    protected void preMapping(StringBuilder template)
    {
    }


    @Override
    public Map<String, Object> getMapping(int index, DBTable dbTable)
    {
        Map<String, Object> map = new HashMap<>();

        this.isPostgresJson |= (super.isPostgresDatabase()  && dbTable.isJson());

        String className  = Strings.toClassName( dbTable.getName() );
        //String objectName = Strings.toPropertyName( dbTable.getName() );

        List<String> importList = new ArrayList<>();

        String keyType = dbTable.isMultipleKey()
                            ? super.getMultipleKey( dbTable )
                            : super.getSimpleKey(dbTable, importList);

        map.put("CrudRepositoriesPackage", getWorkspace().getCrudRepositoriesPackage());
        map.put("BaseCrudNativeRepositoriesPackage", getWorkspace().getBaseCrudNativeRepositoriesPackage());
        map.put("ConfigPackage", getWorkspace().getConfigPackage());
        map.put("PersistencePackage", getWorkspace().getPersistencePackage());
        map.put("JpaConfig", Strings.toClassName(getWorkspace().getDataSourceName())+"JpaConfig");
        map.put("EntitiesPackage", getWorkspace().getEntitiesPackage());
        map.put("CrudNativeRepository", className+"CrudNativeRepository");
        map.put("Entity", className+"Entity");
        map.put("Key", keyType);
        map.put("isMultipleKey", dbTable.isMultipleKey());
        map.put("importList", importList);
        map.put("isPostgresJson", isPostgresJson);

        return map;
    }


    @Override
    public void generate() throws Exception
    {
        this.isPostgresJson = false;

        for (int index=0; index<SOURCE.length; index++)
            for (DBTable dbTable : getTables())
                super.generate(index, dbTable);
    }

}

