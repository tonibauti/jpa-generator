package org.tonibauti.jpa.generator.templates;

import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.main.Workspace;
import org.tonibauti.jpa.generator.templates.base.AbstractTemplate;
import org.tonibauti.jpa.generator.templates.base.FieldData;
import org.tonibauti.jpa.generator.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CrudRepositoryTemplate extends AbstractTemplate
{
    private static final String[] SOURCE =
    {
        "templates/repositories/CrudRepository.fm",
    };

    private static final String[] TARGET =
    {
        "${ClassName}CrudRepository.java",
    };


    public CrudRepositoryTemplate(Workspace workspace, List<DBTable> tables)
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
        String className = Strings.toClassName( dbTable.getName() );
        return getWorkspace().getCrudRepositoriesDir() + TARGET[index].replace("${ClassName}", className);
    }


    @Override
    protected void preMapping(StringBuilder template)
    {
    }


    @Override
    public Map<String, Object> getMapping(int index, DBTable dbTable)
    {
        Map<String, Object> map = new HashMap<>();

        String className  = Strings.toClassName( dbTable.getName() );
        //String objectName = Strings.toPropertyName( dbTable.getName() );
        List<FieldData> indexDataList = new ArrayList<>();

        List<String> importList = new ArrayList<>();

        /*
        // index multiple
        indexDataList.addAll( super.getMultipleIndex(dbTable, false, importList) );

        // index simple
        indexDataList.addAll( super.getSimpleIndex(dbTable, false, importList) );
        */

        // key
        String keyType = dbTable.isMultipleKey()
                            ? super.getMultipleKey( dbTable )
                            : super.getSimpleKey(dbTable, importList);

        map.put("CrudRepositoriesPackage", getWorkspace().getCrudRepositoriesPackage());
        map.put("EntitiesPackage", getWorkspace().getEntitiesPackage());
        map.put("CrudRepository", className+"CrudRepository");
        map.put("Entity", className+"Entity");
        map.put("Key", keyType);
        map.put("isMultipleKey", dbTable.isMultipleKey());
        map.put("importList", importList);
        map.put("indexDataList", indexDataList);

        return map;
    }


    @Override
    public void generate() throws Exception
    {
        for (int index=0; index<SOURCE.length; index++)
            for (DBTable dbTable : getTables())
                super.generate(index, dbTable);
    }

}

