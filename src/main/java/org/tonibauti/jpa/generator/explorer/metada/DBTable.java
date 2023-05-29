package org.tonibauti.jpa.generator.explorer.metada;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.tonibauti.jpa.generator.utils.Triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class DBTable
{
    private String name;
    private String type;
    private String schema;
    private String catalog;
    private boolean quoted;

    private final Map<String, DBColumn> columns = new LinkedHashMap<>();
    private final List<String> primaryKeyList = new ArrayList<>();
    private final Map<String, DBIndex> indexMap = new LinkedHashMap<>();
    private final Map<String, DBForeignKey> foreignKeyMap = new LinkedHashMap<>();
    private final Map<String, DBForeignKey> externalForeignKeyMap = new LinkedHashMap<>();
    private final List<Triple<String,String,Object>> catalogConstantsData = new ArrayList<>();


    public DBTable() {}


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getSchema()
    {
        return schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    public String getCatalog()
    {
        return catalog;
    }

    public void setCatalog(String catalog)
    {
        this.catalog = catalog;
    }

    public boolean isQuoted()
    {
        return quoted;
    }

    public void setQuoted(boolean quoted)
    {
        this.quoted = quoted;
    }


    public List<DBColumn> getColumnList()
    {
        return new ArrayList<>( columns.values() );
    }


    @JsonIgnore
    public DBColumn getColumn(String columnName)
    {
        return columns.get( columnName );
    }


    @JsonIgnore
    public void addColumn(DBColumn dbColumn)
    {
        columns.put(dbColumn.getName(), dbColumn);
    }


    public List<String> getPrimaryKeyList()
    {
        return primaryKeyList;
    }


    public boolean isMultipleKey()
    {
        return (primaryKeyList.size() > 1);
    }


    @JsonIgnore
    public void addPrimaryKeyColumn(String columnName)
    {
        DBColumn dbColumn = getColumn( columnName );

        if (dbColumn == null)
            return;

        dbColumn.setPrimaryKey( true );

        primaryKeyList.add( columnName );
    }


    public Collection<DBIndex> getIndexList()
    {
        return indexMap.values();
    }


    @JsonIgnore
    public void addIndex(String indexName, String tableName, String columnName, boolean unique)
    {
        DBColumn dbColumn = getColumn( columnName );

        if (dbColumn == null)
            return;

        dbColumn.setUniqueKey( unique );

        DBIndex dbIndex = indexMap.get( indexName );

        if (dbIndex == null)
        {
            dbIndex = new DBIndex(indexName, tableName, columnName, unique);
            indexMap.put(indexName, dbIndex);
        }
        else
        {
            dbIndex.addColumn( columnName );
        }
    }


    public List<Triple<String,String,Object>> getCatalogConstantsData()
    {
        return catalogConstantsData;
    }


    public Collection<DBForeignKey> getForeignKeyList()
    {
        return foreignKeyMap.values();
    }


    @JsonIgnore
    public void addForeignKey(String foreignKeyName,
                              boolean external,
                              DBTable dbTable,
                              DBColumn dbColumn,
                              DBTable dbReferencedTable,
                              DBColumn dbReferencedColumn)
    {
        DBForeignKey dbForeignKey = (external)
                                        ? externalForeignKeyMap.get( foreignKeyName )
                                        : foreignKeyMap.get( foreignKeyName );

        if (dbForeignKey == null)
        {
            dbForeignKey = new DBForeignKey(foreignKeyName, external, dbTable, dbColumn, dbReferencedTable, dbReferencedColumn);

            if (external)
                externalForeignKeyMap.put(foreignKeyName, dbForeignKey);
            else
                foreignKeyMap.put(foreignKeyName, dbForeignKey);
        }
        else
        {
            dbForeignKey.addForeignKeyRef(dbColumn, dbReferencedColumn);
        }
    }


    public Collection<DBForeignKey> getExternalForeignKeyList()
    {
        return externalForeignKeyMap.values();
    }


    @JsonIgnore
    public void addCatalogConstantsData(String column, String code, Object value)
    {
        this.catalogConstantsData.add( Triple.of(column, code, value) );
    }


    @JsonIgnore
    public boolean isJson()
    {
        for (DBColumn dbColumn : columns.values())
            if (dbColumn.isJson())
                return true;

        return false;
    }

}

