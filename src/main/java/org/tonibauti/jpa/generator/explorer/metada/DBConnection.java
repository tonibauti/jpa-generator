package org.tonibauti.jpa.generator.explorer.metada;

import java.sql.Types;
import java.util.List;


public class DBConnection
{
    public static final String BYTE_ARRAY_CLASS_NAME = "byte[]";
    public static final String JSON_CLASS_NAME = "Json";

    private String databaseType;
    private String databaseProductName;
    private String databaseProductVersion;
    private String quoteId;
    private String sqlKeyWords;
    private String dataSource;
    private List<String> driverJarAndDependencies;
    private String driverClassName;
    private String jdbcUrl;
    protected String username;
    protected String password;


    public DBConnection() {}


    public String getDatabaseType()
    {
        return databaseType;
    }

    public void setDatabaseType(String databaseType)
    {
        this.databaseType = databaseType;
    }

    public String getDatabaseProductName()
    {
        return databaseProductName;
    }

    public void setDatabaseProductName(String databaseProductName)
    {
        this.databaseProductName = databaseProductName;
    }

    public String getDatabaseProductVersion()
    {
        return databaseProductVersion;
    }

    public void setDatabaseProductVersion(String databaseProductVersion)
    {
        this.databaseProductVersion = databaseProductVersion;
    }

    public String getQuoteId()
    {
        return quoteId;
    }

    public void setQuoteId(String quoteId)
    {
        if ("\"".equals(quoteId))
            this.quoteId = "\\\"";
        else
            this.quoteId = quoteId;
    }

    public String getSqlKeyWords()
    {
        return sqlKeyWords;
    }

    public void setSqlKeyWords(String sqlKeyWords)
    {
        this.sqlKeyWords = sqlKeyWords;
    }

    public String getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(String dataSource)
    {
        this.dataSource = dataSource;
    }

    public List<String> getDriverJarAndDependencies()
    {
        return driverJarAndDependencies;
    }

    public void setDriverJarAndDependencies(List<String> driverJarAndDependencies)
    {
        this.driverJarAndDependencies = driverJarAndDependencies;
    }

    public String getDriverClassName()
    {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName)
    {
        this.driverClassName = driverClassName;
    }

    public String getJdbcUrl()
    {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl)
    {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }


    public String getColumnClassName(int sqlType, String sqlTypeName)
    {
        String className = Object.class.getName();

        switch (sqlType)
        {
            case Types.BIT:
            case Types.BOOLEAN:
                className = Boolean.class.getName();
                break;

            /*
            case Types.TINYINT:
                className = Byte.class.getName();
                break;

            case Types.SMALLINT:
                className = Short.class.getName();
                break;
            */

            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                className = Integer.class.getName();
                break;

            case Types.BIGINT:
                //className = java.math.BigInteger.class.getName();
                className = Long.class.getName();
                break;

            /*
            case Types.NUMERIC:
            case Types.DECIMAL:
                className = java.math.BigDecimal.class.getName();
                break;

            case Types.REAL:
                className = Float.class.getName();
                break;
            */

            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.REAL:
            case Types.FLOAT:
            case Types.DOUBLE:
                className = Double.class.getName();
                break;

            case Types.CHAR:
            case Types.NCHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
                className = String.class.getName();
                break;

            case Types.DATE:
                className = java.sql.Date.class.getName();
                break;

            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
                className = java.sql.Time.class.getName();
                break;

            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                className = java.sql.Timestamp.class.getName();
                break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                className = BYTE_ARRAY_CLASS_NAME;  // byte[].class.getName() --> "[B"
                break;

            case Types.NULL:
                className = Void.class.getName();
                break;

            case Types.DISTINCT:
            case Types.STRUCT:
            case Types.OTHER:
            case Types.JAVA_OBJECT:
                className = Object.class.getName();
                break;

            case Types.ARRAY:
                className = java.sql.Array.class.getName();
                break;

            case Types.BLOB:
                className = java.sql.Blob.class.getName();
                break;

            case Types.CLOB:
                className = java.sql.Clob.class.getName();
                break;

            case Types.NCLOB:
                className = java.sql.NClob.class.getName();
                break;

            case Types.SQLXML:
                className = java.sql.SQLXML.class.getName();
                break;

            case Types.REF:
            case Types.REF_CURSOR:
                className = java.sql.Ref.class.getName();
                break;

            case Types.ROWID:
                className = java.sql.RowId.class.getName();
                break;
        }

        return className;
    }

}

