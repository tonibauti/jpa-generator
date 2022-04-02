package org.tonibauti.jpa.generator.templates;

import org.tonibauti.jpa.generator.explorer.metada.DBColumn;
import org.tonibauti.jpa.generator.explorer.metada.DBConnection;
import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.main.Workspace;
import org.tonibauti.jpa.generator.templates.base.AbstractTemplate;
import org.tonibauti.jpa.generator.templates.base.FieldData;
import org.tonibauti.jpa.generator.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CrudRepositoryTestTemplate extends AbstractTemplate
{
    private static final String[] SOURCE =
    {
        "templates/repositories/test/CrudRepositoryTest.fm",
    };

    private static final String[] TARGET =
    {
        "${ClassName}CrudRepositoryTest.java",
    };


    public CrudRepositoryTestTemplate(Workspace workspace, List<DBTable> tables)
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
            return getWorkspace().getCrudRepositoriesTestDir() + TARGET[index].replace("${ClassName}", className);
        }
        else
        if (TARGET[index].contains("Constraints"))
        {
            // constraints
            return getWorkspace().getBaseConstraintsRepositoriesTestDir() + TARGET[index];
        }
        else
        {
            // base
            return getWorkspace().getBaseRepositoriesTestDir() + TARGET[index];
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

        String className  = Strings.toClassName( dbTable.getName() );
        String objectName = Strings.toPropertyName( dbTable.getName() );

        List<FieldData> fieldDataList   = new ArrayList<>();
        List<FieldData> filterDataList  = new ArrayList<>();
        List<FieldData> pkFieldDataList = new ArrayList<>();
        List<FieldData> indexDataList   = new ArrayList<>();

        List<String> importList = new ArrayList<>();

        for (DBColumn dbColumn : dbTable.getColumnList())
        {
            FieldData fieldData = new FieldData();

            fieldData.setName( dbColumn.getName() );
            fieldData.setColumn( Strings.toColumnName(dbColumn.getName()) );
            fieldData.setProperty( Strings.toPropertyName(dbColumn.getName()) );
            fieldData.setType( super.getNormalizedType(dbColumn.getClassName(), null) );

            // DataTestFactory
            if (dbColumn.getClassName().equals(DBConnection.BYTE_ARRAY_CLASS_NAME))
                fieldData.setType("ByteArray");
            else
            if (dbColumn.getClassName().equals(java.sql.Date.class.getName()))
                fieldData.setType("SqlDate");
            else
            if (dbColumn.isJson())
                fieldData.setType("Json");
            // DataTestFactory


            // filter
            if (super.isFilterType(dbColumn))
                filterDataList.add( fieldData );
            // filter


            if (dbColumn.isPrimaryKey())
                pkFieldDataList.add( fieldData );


            fieldDataList.add( fieldData );
        }

        // index multiple
        indexDataList.addAll( super.getMultipleIndex(dbTable, true, importList) );
        
        // index simple
        indexDataList.addAll( super.getSimpleIndex(dbTable, true, importList) );

        // key
        String keyType = dbTable.isMultipleKey()
                            ? super.getMultipleKey( dbTable )
                            : super.getSimpleKey(dbTable, importList);

        map.put("CrudRepositoriesTestPackage", getWorkspace().getCrudRepositoriesTestPackage());
        map.put("BaseRepositoriesTestPackage", getWorkspace().getBaseRepositoriesTestPackage());
        map.put("BaseConstraintsRepositoriesTestPackage", getWorkspace().getBaseConstraintsRepositoriesTestPackage());
        map.put("CrudRepositoriesPackage", getWorkspace().getCrudRepositoriesPackage());
        map.put("ConfigPackage", getWorkspace().getConfigPackage());
        map.put("JpaConfig", Strings.toClassName(getWorkspace().getDataSourceName())+"JpaConfig");
        map.put("EntitiesPackage", getWorkspace().getEntitiesPackage());
        map.put("ClassName", className);
        map.put("objectName", objectName);
        map.put("Entity", className+"Entity");
        map.put("Key", keyType);
        map.put("isMultipleKey", dbTable.isMultipleKey());
        map.put("importList", importList);
        map.put("fieldDataList", fieldDataList);
        map.put("filterDataList", filterDataList);
        map.put("pkFieldDataList", pkFieldDataList);
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

