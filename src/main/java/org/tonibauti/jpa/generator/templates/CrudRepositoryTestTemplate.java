package org.tonibauti.jpa.generator.templates;

import org.tonibauti.jpa.generator.explorer.metada.DBColumn;
import org.tonibauti.jpa.generator.explorer.metada.DBConnection;
import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.main.Workspace;
import org.tonibauti.jpa.generator.templates.base.AbstractTemplate;
import org.tonibauti.jpa.generator.templates.base.FieldData;
import org.tonibauti.jpa.generator.utils.Strings;

import java.util.*;


public class CrudRepositoryTestTemplate extends AbstractTemplate
{
    private static final String[] SOURCE =
    {
        "templates/repositories/test/constraints/AbstractConstraints.fm",
        "templates/repositories/test/constraints/${DatabaseConstraints}.fm",
        "templates/repositories/test/DataTestFactory.fm",
        "templates/repositories/test/AbstractRepositoryTest.fm",
        "templates/repositories/test/CrudRepositoryTest.fm",
    };

    private static final String[] TARGET =
    {
        "AbstractConstraints.java",
        "${DatabaseConstraints}.java",
        "DataTestFactory.java",
        "AbstractRepositoryTest.java",
        "${ClassName}CrudRepositoryTest.java",
    };

    private static final Map<String, String> DatabaseConstraintsMaps = new LinkedHashMap<>();
    static
    {
        DatabaseConstraintsMaps.put("h2",         "H2Constraints");
        DatabaseConstraintsMaps.put("db2",        "DB2Constraints");
        DatabaseConstraintsMaps.put("hsqldb",     "HSQLDBConstraints");
        DatabaseConstraintsMaps.put("mariadb",    "MariaDBConstraints");
        DatabaseConstraintsMaps.put("mysql",      "MySQLConstraints");
        DatabaseConstraintsMaps.put("oracle",     "OracleConstraints");
        DatabaseConstraintsMaps.put("postgresql", "PostgreSQLConstraints");
        DatabaseConstraintsMaps.put("sqlite",     "SQLiteConstraints");
        DatabaseConstraintsMaps.put("sqlserver",  "SQLServerConstraints");
        DatabaseConstraintsMaps.put("unknown",    "UnknownConstraints");
    }



    public CrudRepositoryTestTemplate(Workspace workspace, List<DBTable> tables)
    {
        super(workspace, tables);
    }


    private String getDatabaseConstraints()
    {
        String databaseProductName = getWorkspace().getDbConnection().getDatabaseProductName().toLowerCase();

        for (Map.Entry<String, String> entry : DatabaseConstraintsMaps.entrySet())
            if (databaseProductName.contains(entry.getKey()))
                return entry.getValue();

        return DatabaseConstraintsMaps.get( "unknown" );
    }


    @Override
    public String getSource(int index, DBTable dbTable)
    {
        if (SOURCE[index].contains("${DatabaseConstraints}"))
        {
            return SOURCE[index].replace("${DatabaseConstraints}", getDatabaseConstraints());
        }
        else
        if (SOURCE[index].contains("CrudRepository") && workspace.isNativeMode())
        {
            return SOURCE[index].replace("CrudRepository", "CrudNativeRepository");
        }

        return SOURCE[index];
    }


    @Override
    public String getTarget(int index, DBTable dbTable)
    {
        if (TARGET[index].contains("${ClassName}"))
        {
            String className = Strings.toClassName( dbTable.getName() );

            if (workspace.isSpringDataMode())
            {
                return getWorkspace().getCrudRepositoriesTestDir()
                       + TARGET[index].replace("${ClassName}", className);
            }
            else
            if (workspace.isNativeMode())
            {
                return getWorkspace().getCrudRepositoriesTestDir()
                       + TARGET[index].replace("${ClassName}", className).replace("CrudRepository", "CrudNativeRepository");
            }
            else
            {
                return null;
            }
        }
        else
        if (TARGET[index].contains("Constraints"))
        {
            // constraints

            if (TARGET[index].contains("${DatabaseConstraints}"))
            {
                return getWorkspace().getBaseConstraintsRepositoriesTestDir()
                       + TARGET[index].replace("${DatabaseConstraints}", getDatabaseConstraints());
            }
            else
            {
                return getWorkspace().getBaseConstraintsRepositoriesTestDir() + TARGET[index];
            }
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


    private FieldData getFieldData(DBColumn dbColumn)
    {
        FieldData fieldData = new FieldData();

        String quoteId = workspace.getDbConnection().getQuoteId();

        String columnName = dbColumn.isQuoted() ? (quoteId + dbColumn.getName() + quoteId) : dbColumn.getName();

        fieldData.setName( columnName );
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

        if (dbColumn.isPrimaryKey())
            fieldData.setPk( true );

        return fieldData;
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

        List<String> importList = new ArrayList<>();

        for (DBColumn dbColumn : dbTable.getColumnList())
        {
            FieldData fieldData = getFieldData( dbColumn );

            // filter
            if (super.isFilterType(dbColumn))
                filterDataList.add( fieldData );
            // filter

            if (dbColumn.isPrimaryKey())
                pkFieldDataList.add( fieldData );

            fieldDataList.add( fieldData );
        }

        // key
        String keyType = dbTable.isMultipleKey()
                            ? super.getMultipleKey( dbTable )
                            : super.getSimpleKey(dbTable, importList);

        // primary keys from table of multiple key
        String multipleKeyTableName = super.getMultipleKeyTableName( dbTable );
        if (multipleKeyTableName != null)
        {
            DBTable dbTableMultiKey = super.getTable( multipleKeyTableName );
            if (dbTableMultiKey != null)
            {
                pkFieldDataList.clear();

                for (DBColumn dbColumn : dbTableMultiKey.getColumnList())
                {
                    if (dbColumn.isPrimaryKey())
                    {
                        pkFieldDataList.add( getFieldData( dbColumn ) );
                    }
                }
            }
        }

        map.put("CrudRepositoriesTestPackage", getWorkspace().getCrudRepositoriesTestPackage());
        map.put("BaseRepositoriesTestPackage", getWorkspace().getBaseRepositoriesTestPackage());
        map.put("BaseConstraintsRepositoriesTestPackage", getWorkspace().getBaseConstraintsRepositoriesTestPackage());
        map.put("CrudRepositoriesPackage", getWorkspace().getCrudRepositoriesPackage());
        map.put("DatabaseConstraints", getDatabaseConstraints());
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

        if (workspace.isCrudRepositoriesTest())
        {
            List<FieldData> indexDataList = new ArrayList<>();

            /*
            // index multiple
            indexDataList.addAll( super.getMultipleIndex(dbTable, true, importList) );

            // index simple
            indexDataList.addAll( super.getSimpleIndex(dbTable, true, importList) );
            */

            map.put("indexDataList", indexDataList);
        }

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

