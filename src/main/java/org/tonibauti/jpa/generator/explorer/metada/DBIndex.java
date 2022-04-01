package org.tonibauti.jpa.generator.explorer.metada;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;


public class DBIndex
{
    private final String name;
    private final String table;
    private final List<String> columns = new ArrayList<>();
    private final boolean unique;


    public DBIndex(String indexName, String table, String column, boolean unique)
    {
        this.name   = indexName;
        this.table  = table;
        this.unique = unique;

        addColumn( column );
    }


    public String getName()
    {
        return name;
    }

    public String getTable()
    {
        return table;
    }

    public List<String> getColumns()
    {
        return columns;
    }

    public boolean isUnique()
    {
        return unique;
    }

    @JsonIgnore
    public void addColumn(String column)
    {
        columns.add( column );
    }

}

