package org.tonibauti.jpa.generator.explorer.metada;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Database
{
    private final DBConnection dbConnection = new DBConnection();
    private final Map<String, DBTable> tables = new LinkedHashMap<>();


    public Database() {}


    public DBConnection getDBConnection()
    {
        return dbConnection;
    }


    public List<DBTable> getTableList()
    {
        return new ArrayList<>( tables.values() );
    }


    @JsonIgnore
    public DBTable getTable(String tableName)
    {
        return tables.get( tableName );
    }


    @JsonIgnore
    public void addTable(DBTable dbTable)
    {
        tables.put(dbTable.getName(), dbTable);
    }

}

