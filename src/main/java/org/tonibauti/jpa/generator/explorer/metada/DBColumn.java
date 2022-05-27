package org.tonibauti.jpa.generator.explorer.metada;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;


public class DBColumn
{
    private String name;
    private String table;
    private String schema;
    private String catalog;
    private int sqlType;
    private String sqlTypeName;
    private int size;
    private int decimalDigits;
    private boolean nullable;
    private boolean autoIncrement;
    private boolean generated;
    private boolean encoded;
    private boolean invisible;
    private boolean primaryKey;
    private boolean uniqueKey;
    private boolean quoted;
    private String className;


    public DBColumn() {}


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTable()
    {
        return table;
    }

    public void setTable(String table)
    {
        this.table = table;
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

    public int getSqlType()
    {
        return sqlType;
    }

    public void setSqlType(int sqlType)
    {
        this.sqlType = sqlType;
    }

    public String getSqlTypeName()
    {
        return sqlTypeName;
    }

    public void setSqlTypeName(String sqlTypeName)
    {
        this.sqlTypeName = sqlTypeName;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getDecimalDigits()
    {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits)
    {
        this.decimalDigits = decimalDigits;
    }

    public boolean isNullable()
    {
        return nullable;
    }

    public void setNullable(boolean nullable)
    {
        this.nullable = nullable;
    }

    public boolean isAutoIncrement()
    {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement)
    {
        this.autoIncrement = autoIncrement;
    }

    public boolean isGenerated()
    {
        return generated;
    }

    public void setGenerated(boolean generated)
    {
        this.generated = generated;
    }

    public boolean isEncoded()
    {
        return encoded;
    }

    public void setEncoded(boolean encoded)
    {
        this.encoded = encoded;
    }

    public boolean isInvisible()
    {
        return invisible;
    }

    public void setInvisible(boolean invisible)
    {
        this.invisible = invisible;
    }

    public boolean isPrimaryKey()
    {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public boolean isUniqueKey()
    {
        return uniqueKey;
    }

    public void setUniqueKey(boolean uniqueKey)
    {
        this.uniqueKey = uniqueKey;
    }

    public boolean isQuoted()
    {
        return quoted;
    }

    public void setQuoted(boolean quoted)
    {
        this.quoted = quoted;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    @JsonIgnore
    public boolean isJson()
    {
        return className.equals( DBConnection.JSON_CLASS_NAME );
    }


    @JsonIgnore
    public boolean isUUID()
    {
        return className.equals( UUID.class.getName() );
    }

}

