package org.tonibauti.jpa.generator.explorer;

import jakarta.validation.ValidationException;
import org.tonibauti.jpa.generator.cli.Console;
import org.tonibauti.jpa.generator.config.Config;
import org.tonibauti.jpa.generator.config.DataSourceConfig;
import org.tonibauti.jpa.generator.config.GeneratorConfig;
import org.tonibauti.jpa.generator.explorer.metada.DBColumn;
import org.tonibauti.jpa.generator.explorer.metada.DBConnection;
import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.explorer.metada.Database;
import org.tonibauti.jpa.generator.main.AbstractComponent;
import org.tonibauti.jpa.generator.mapper.Mapper;
import org.tonibauti.jpa.generator.utils.Resources;
import org.tonibauti.jpa.generator.utils.Strings;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseExplorer extends AbstractComponent implements AbstractResultSet
{
    private final Database database = new Database();


    public DatabaseExplorer()
    {
        super();
    }


    public DBConnection getDatabaseConnection()
    {
        return database.getDBConnection();
    }


    public List<DBTable> getTables()
    {
        return database.getTableList();
    }


    private Connection getConnection(DataSourceConfig dataSourceConfig) throws Exception
    {
        return DriverManager.getConnection( dataSourceConfig.getJdbcUrl(),
                                            dataSourceConfig.getUsername(),
                                            dataSourceConfig.getPassword() );
    }


    private void close(Connection conn, Statement stmt, ResultSet rst)
    {
        try { if (rst  != null) rst.close();  } catch (Exception e) { /* ignore */ }
        try { if (stmt != null) stmt.close(); } catch (Exception e) { /* ignore */ }
        try { if (conn != null) conn.close(); } catch (Exception e) { /* ignore */ }
    }


    public void explore(Config config) throws Exception
    {
        // load driver jar
        for (String fileName : config.getGeneratorConfig().getDataSourceConfig().getDriverJarAndDependencies())
            Resources.addToClassPath( fileName );

        // init driver
        Class.forName( config.getGeneratorConfig().getDataSourceConfig().getDriverClassName() );

        // explore database
        exploreDatabase( config.getGeneratorConfig() );
    }


    private boolean containsTable(String tableName, List<String> maskTableList)
    {
        if (isNullOrEmpty(maskTableList))
            return false;

        for (String maskTable : maskTableList)
            if (Strings.checkWildcard(tableName, maskTable, true))
                return true;

        return false;
    }


    private void exploreDatabase(GeneratorConfig generatorConfig) throws Exception
    {
        Connection conn = null;
        ResultSet rst = null;

        try
        {
            conn = getConnection( generatorConfig.getDataSourceConfig() );
            DatabaseMetaData dbMetaData = conn.getMetaData();

            database.getDBConnection().setDatabaseType( dbMetaData.getDatabaseProductName() );
            database.getDBConnection().setDataSource( generatorConfig.getDataSourceConfig().getName() );
            database.getDBConnection().setDriverJarAndDependencies( generatorConfig.getDataSourceConfig().getDriverJarAndDependencies() );
            database.getDBConnection().setDriverClassName( generatorConfig.getDataSourceConfig().getDriverClassName() );
            database.getDBConnection().setQuoteId( dbMetaData.getIdentifierQuoteString() );
            database.getDBConnection().setJdbcUrl( generatorConfig.getDataSourceConfig().getJdbcUrl() );
            database.getDBConnection().setUsername( generatorConfig.getDataSourceConfig().getUsername() );
            database.getDBConnection().setPassword( generatorConfig.getDataSourceConfig().getPassword() );
            database.getDBConnection().setDatabaseProductName( dbMetaData.getDatabaseProductName() );
            database.getDBConnection().setDatabaseProductVersion( dbMetaData.getDatabaseProductVersion() );


            // filter by includes and excludes

            int totalTables            = 0;
            int includedTables         = 0;
            int excludedTables         = 0;
            Integer catalogConstantsTables = null;
            int exploredTables         = 0;
            int exploredColumns        = 0;

            List<String> includeTableList = generatorConfig.getProjectConfig().getTablesConfig().getIncludes();
            List<String> excludeTableList = generatorConfig.getProjectConfig().getTablesConfig().getExcludes();
            List<String> catalogConstantsTableList = generatorConfig.getProjectConfig().getTablesConfig().getCatalogConstants();

            removeNullsAndBlanks( includeTableList );
            removeNullsAndBlanks( excludeTableList );
            removeNullsAndBlanks( catalogConstantsTableList );

            /*
                table types: "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"

                The getTables() method return a ResultSet with 5 Columns

                1. TABLE_CAT String     - table catalog (may be null)
                2. TABLE_SCHEM String   - table schema (may be null)
                3. TABLE_NAME String    - table name
                4. TABLE_TYPE String    - table type
                5. REMARKS String       - explanatory comment on the table
            */

            String[] types = { "TABLE", "VIEW" };
            rst = dbMetaData.getTables(conn.getCatalog(), conn.getSchema(), "%", types);

            while (rst.next())
            {
                totalTables++;

                String tableCatalog = getString(rst, "TABLE_CAT");
                String tableSchema  = getString(rst, "TABLE_SCHEM");
                String tableName    = getString(rst, "TABLE_NAME");
                String tableType    = getString(rst, "TABLE_TYPE");
                String remarks      = getString(rst, "REMARKS");

                //if (tableName.contains("$"))
                //    continue;

                boolean included = containsTable(tableName, includeTableList);
                boolean excluded = containsTable(tableName, excludeTableList);

                if (included)
                    includedTables++;

                if (excluded)
                    excludedTables++;

                if (!included || excluded)
                    continue;

                exploredTables++;

                // table
                DBTable dbTable = new DBTable();

                dbTable.setName( tableName );
                dbTable.setType( tableType );
                dbTable.setSchema( tableSchema );
                dbTable.setCatalog( tableCatalog );
                // quoted
                dbTable.setQuoted( !Strings.isValidIdentifier(tableName) );

                // columns
                int nColumns = exploreColumns(dbTable, dbMetaData, generatorConfig);

                if (nColumns == 0)
                {
                    throw new ValidationException("table '" + dbTable.getName() + "' has not included columns");
                }

                exploredColumns += nColumns;

                // primary keys
                explorePrimaryKeys(dbTable, dbMetaData);

                // indexes (unique keys)
                exploreIndexes(dbTable, dbMetaData);

                // sequences
                exploreSequences(dbTable, dbMetaData);

                // catalogsData
                if (super.bool(generatorConfig.getGenerateCatalogConstants()))
                {
                    if (catalogConstantsTables == null)
                        catalogConstantsTables = 0;

                    if (containsTable(tableName, catalogConstantsTableList))
                    {
                        List<String> catalogConstantsColumns = generatorConfig
                                                                    .getProjectConfig()
                                                                        .getColumnsConfig()
                                                                            .getCatalogConstants();

                        catalogConstantsTables += readCatalogConstantsTables(conn,
                                                                             dbTable,
                                                                             database.getDBConnection().getQuoteId(),
                                                                             catalogConstantsColumns);
                    }
                }

                // add table
                database.addTable( dbTable );
            }


            final List<DBTable> tables = database.getTableList();


            if (super.bool(generatorConfig.getGenerateJoins()))
            {
                // foreign keys and external foreign keys
                for (DBTable dbTable : tables)
                {
                    // foreign keys to another tables
                    exploreForeignKeys(dbTable, dbMetaData);

                    // external foreign keys to another tables
                    exploreExternalForeignKeys(dbTable, dbMetaData);
                }
            }


            // verbose
            for (DBTable dbTable : tables)
            {
                Console.verbose(Console.line +
                                "Table " + dbTable.getName() + ":" + Console.lineSeparator +
                                Console.line +
                                Mapper.getInstance().toYaml(dbTable), true);
            }

            // summary
            int maxLength = (catalogConstantsTables != null) ? 25 : 17;

            Console.info(Strings.blanksFill("Total Tables",     maxLength), totalTables);
            Console.info(Strings.blanksFill("Included Tables",  maxLength), includedTables);
            Console.info(Strings.blanksFill("Excluded Tables",  maxLength), excludedTables);

            if (catalogConstantsTables != null)
                Console.info("Catalog Constants Tables ", catalogConstantsTables);

            Console.info(Strings.blanksFill("Explored Tables",  maxLength), exploredTables);
            Console.info(Strings.blanksFill("Explored Columns", maxLength), exploredColumns);
        }
        finally
        {
            close(conn, null, rst);
        }
    }


    private boolean containsColumn(String tableName, String columnName, List<String> maskColumnList)
    {
        if (isNullOrEmpty(maskColumnList))
            return false;

        for (String maskColumn : maskColumnList)
        {
            if (!maskColumn.contains("."))
            {
                maskColumn = ("*." + maskColumn);
            }

            String[] s = Strings.splitLast(maskColumn, ".");
            String maskTable = s[0];
            maskColumn = s[1];

            if (Strings.checkWildcard(tableName, maskTable, true)
                &&
                Strings.checkWildcard(columnName, maskColumn, true))
            {
                return true;
            }
        }

        return false;
    }


    private int exploreColumns(DBTable dbTable, DatabaseMetaData dbMetaData, GeneratorConfig generatorConfig) throws Exception
    {
        int exploredColumns = 0;

        ResultSet rst = null;

        try
        {
            List<String> includedColumnList  = generatorConfig.getProjectConfig().getColumnsConfig().getIncludes();
            List<String> excludedColumnList  = generatorConfig.getProjectConfig().getColumnsConfig().getExcludes();
            List<String> encodedColumnList   = generatorConfig.getProjectConfig().getColumnsConfig().getEncoded();
            List<String> invisibleColumnList = generatorConfig.getProjectConfig().getColumnsConfig().getInvisible();

            removeNullsAndBlanks( includedColumnList );
            removeNullsAndBlanks( excludedColumnList );
            removeNullsAndBlanks( encodedColumnList );
            removeNullsAndBlanks( invisibleColumnList );

            rst = dbMetaData.getColumns(dbTable.getCatalog(), dbTable.getSchema(), dbTable.getName(), "%");

            while (rst.next())
            {
                String tableCatalog = getString(rst, "TABLE_CAT");
                String tableSchema  = getString(rst, "TABLE_SCHEM");
                String tableName    = getString(rst, "TABLE_NAME");
                String columnName   = getString(rst, "COLUMN_NAME");

                boolean included = containsColumn(tableName, columnName, includedColumnList);
                boolean excluded = containsColumn(tableName, columnName, excludedColumnList);

                if (!included || excluded)
                    continue;

                exploredColumns++;

                Integer sqlType         = getInt(rst,    "DATA_TYPE"); // java.sql.Types
                String sqlTypeName      = getString(rst, "TYPE_NAME");
                Integer size            = getInt(rst,    "COLUMN_SIZE");
                Integer decimalDigits   = getInt(rst,    "DECIMAL_DIGITS");
                Integer numPrecRadix    = getInt(rst,    "NUM_PREC_RADIX");
                Integer nullable        = getInt(rst,    "NULLABLE");  // ResultSetMetaData.columnNullable
                String remarks          = getString(rst, "REMARKS");
                String columnDef        = getString(rst, "COLUMN_DEF");
                Integer charOctetLength = getInt(rst,    "CHAR_OCTET_LENGTH");
                Integer ordinalPosition = getInt(rst,    "ORDINAL_POSITION");      // starting at 1
                String isNullable       = getString(rst, "IS_NULLABLE");        // YES, NO, <empty>
                String isAutoIncrement  = getString(rst, "IS_AUTOINCREMENT");   // YES, NO, <empty>
                String isGenerated      = getString(rst, "IS_GENERATEDCOLUMN"); // YES, NO, <empty>
                String scopeCatalog     = getString(rst, "SCOPE_CATALOG");
                String scopeSchema      = getString(rst, "SCOPE_SCHEMA");
                String scopeTable       = getString(rst, "SCOPE_TABLE");
                Integer sourceDataType  = getInt(rst,    "SOURCE_DATA_TYPE"); // java.sql.Types

                // column
                DBColumn dbColumn = new DBColumn();

                dbColumn.setName( columnName );
                dbColumn.setTable( tableName );
                dbColumn.setSchema( tableSchema );
                dbColumn.setCatalog( tableCatalog );
                dbColumn.setSqlType( sqlType );
                dbColumn.setSqlTypeName( sqlTypeName );
                dbColumn.setSize( super.integer(size) );
                dbColumn.setDecimalDigits( super.integer(decimalDigits) );
                dbColumn.setNullable( "YES".equalsIgnoreCase(isNullable) );
                dbColumn.setAutoIncrement( "YES".equalsIgnoreCase(isAutoIncrement) );
                dbColumn.setGenerated( "YES".equalsIgnoreCase(isGenerated) );
                dbColumn.setQuoted( !Strings.isValidIdentifier(columnName) );
                dbColumn.setClassName( database.getDBConnection().getColumnClassName(sqlType, sqlTypeName) );

                if (dbColumn.getClassName().equals(Object.class.getName()))
                {
                    if (dbColumn.isJson())
                    {
                        dbColumn.setClassName( DBConnection.JSON_CLASS_NAME );
                    }
                    else
                    if (dbColumn.isUUID())
                    {
                        dbColumn.setClassName( DBConnection.UUID_CLASS_NAME );
                        dbColumn.setSize( 36 );
                    }
                }

                // encoded --> only strings
                if (dbColumn.getClassName().equals(String.class.getName()))
                {
                    boolean encoded = containsColumn(tableName, columnName, encodedColumnList);
                    dbColumn.setEncoded( encoded );
                }

                // invisible
                boolean invisible = containsColumn(tableName, columnName, invisibleColumnList);
                dbColumn.setInvisible( invisible );


                // add column
                dbTable.addColumn( dbColumn );
            }
        }
        finally
        {
            close(null, null, rst);
        }

        return exploredColumns;
    }


    private void exploreSequences(DBTable dbTable, DatabaseMetaData dbMetaData) throws Exception
    {
        ResultSet rst = null;

        try
        {
            String[] types = { "SEQUENCE" };
            rst = dbMetaData.getTables(dbTable.getCatalog(), dbTable.getSchema(), "%", types);

            while (rst.next())
            {
                String tableCatalog = getString(rst, "TABLE_CAT");
                String tableSchema  = getString(rst, "TABLE_SCHEM");
                String tableName    = getString(rst, "TABLE_NAME");
                String tableType    = getString(rst, "TABLE_TYPE");
                String remarks      = getString(rst, "REMARKS");

                //System.err.println( tableName );
            }
        }
        finally
        {
            close(null, null, rst);
        }
    }


    private void explorePrimaryKeys(DBTable dbTable, DatabaseMetaData dbMetaData) throws Exception
    {
        ResultSet rst = null;

        try
        {
            // to add ordered primary key by KEY_SEQ (1..N)
            Map<Integer, String> aux = new HashMap<>();

            rst = dbMetaData.getPrimaryKeys(dbTable.getCatalog(), dbTable.getSchema(), dbTable.getName());

            while (rst.next())
            {
                String tableCatalog = getString(rst, "TABLE_CAT");
                String tableSchema  = getString(rst, "TABLE_SCHEM");
                String tableName    = getString(rst, "TABLE_NAME");
                String columnName   = getString(rst, "COLUMN_NAME");
                Integer keySeq      = getInt(rst,    "KEY_SEQ"); // 1..N
                String pkName       = getString(rst, "PK_NAME");

                aux.put(keySeq, columnName);
            }

            // add ordered primary key by KEY_SEQ (1..N)
            for (int i=1; i<=aux.size(); i++)
            {
                dbTable.addPrimaryKeyColumn( aux.get(i) );
            }

            aux.clear();

            if (dbTable.getPrimaryKeyList().isEmpty())
            {
                throw new ValidationException("table '" + dbTable.getName() + "' has not primary key");
            }
        }
        finally
        {
            close(null, null, rst);
        }
    }


    private void exploreIndexes(DBTable dbTable, DatabaseMetaData dbMetaData) throws Exception
    {
        ResultSet rst = null;

        try
        {
            final boolean onlyUniques = false;
            final boolean approximate = false;

            rst = dbMetaData.getIndexInfo( dbTable.getCatalog(),
                                           dbTable.getSchema(),
                                           dbTable.getName(),
                                           onlyUniques,
                                           approximate );

            while (rst.next())
            {
                String tableCatalog = getString(rst,  "TABLE_CAT");
                String tableSchema  = getString(rst,  "TABLE_SCHEM");
                String tableName    = getString(rst,  "TABLE_NAME");
                String columnName   = getString(rst,  "COLUMN_NAME");
                Boolean isNonUnique = getBoolean(rst, "NON_UNIQUE");
                String indexCatalog = getString(rst,  "INDEX_QUALIFIER");
                String indexName    = getString(rst,  "INDEX_NAME");

                boolean unique = !super.bool( isNonUnique );

                dbTable.addIndex(indexName, tableName, columnName, unique);
            }
        }
        finally
        {
            close(null, null, rst);
        }
    }


    private void exploreForeignKeys(DBTable dbTable, DatabaseMetaData dbMetaData) throws Exception
    {
        ResultSet rst = null;

        try
        {
            rst = dbMetaData.getImportedKeys(dbTable.getCatalog(), dbTable.getSchema(), dbTable.getName());

            while (rst.next())
            {
                String pkTableName    = getString(rst, "PKTABLE_NAME");
                String pkColumnName   = getString(rst, "PKCOLUMN_NAME");
                String fkTableName    = getString(rst, "FKTABLE_NAME");
                String fkColumnName   = getString(rst, "FKCOLUMN_NAME");
                Integer keySeq        = getInt(rst,    "KEY_SEQ");
                String foreignKeyName = getString(rst, "FK_NAME");

                DBColumn dbColumn = dbTable.getColumn( fkColumnName );

                DBTable dbForeignTable = database.getTable( pkTableName );

                if (dbForeignTable == null)
                    continue;

                DBColumn dbForeignColumn = dbForeignTable.getColumn( pkColumnName );

                if (dbForeignColumn == null)
                    continue;

                dbTable.addForeignKey(foreignKeyName,
                                      false, // not external
                                      dbTable,
                                      dbColumn,
                                      dbForeignTable,
                                      dbForeignColumn);
            }
        }
        finally
        {
            close(null, null, rst);
        }
    }


    private void exploreExternalForeignKeys(DBTable dbTable, DatabaseMetaData dbMetaData) throws Exception
    {
        ResultSet rst = null;

        try
        {
            rst = dbMetaData.getExportedKeys(dbTable.getCatalog(), dbTable.getSchema(), dbTable.getName());

            while (rst.next())
            {
                String pkTableName    = getString(rst, "PKTABLE_NAME");
                String pkColumnName   = getString(rst, "PKCOLUMN_NAME");
                String fkTableName    = getString(rst, "FKTABLE_NAME");
                String fkColumnName   = getString(rst, "FKCOLUMN_NAME");
                Integer keySeq        = getInt(rst,    "KEY_SEQ");
                String foreignKeyName = getString(rst, "FK_NAME");

                DBColumn dbColumn = dbTable.getColumn( pkColumnName );

                DBTable dbForeignTable = database.getTable( fkTableName );

                if (dbForeignTable == null)
                    continue;

                DBColumn dbForeignColumn = dbForeignTable.getColumn( fkColumnName );

                if (dbForeignColumn == null)
                    continue;

                dbTable.addForeignKey(foreignKeyName,
                                      true, // external
                                      dbTable,
                                      dbColumn,
                                      dbForeignTable,
                                      dbForeignColumn);
            }
        }
        finally
        {
            close(null, null, rst);
        }
    }


    private int readCatalogConstantsTables(Connection conn,
                                           DBTable dbTable,
                                           String quoteId,
                                           List<String> catalogConstantsColumns)
    {
        int catalogs = 0;

        final String prefix = "read catalog constants: ";

        final List<String> primaryKeyList = dbTable.getPrimaryKeyList();

        if (primaryKeyList.isEmpty())
        {
            throw new ValidationException(prefix + "'" + dbTable.getName() + "' has not primary key");
        }

        if (primaryKeyList.size() > 1)
        {
            throw new ValidationException(prefix + "'" + dbTable.getName() + "' has multiple primary key");
        }

        String primaryKey = primaryKeyList.get(0);

        removeNullsAndBlanks( catalogConstantsColumns );

        Statement stmt = null;
        ResultSet rst  = null;

        for (String catalogConstantsColumn : catalogConstantsColumns)
        {
            try
            {
                String query = "select "
                                + quoteId + catalogConstantsColumn + quoteId
                                + ", "
                                + quoteId + primaryKey + quoteId
                                + " from "
                                + quoteId + dbTable.getName() + quoteId
                                + " order by "
                                + quoteId + primaryKey + quoteId
                                + " asc";

                stmt = conn.createStatement();
                rst  = stmt.executeQuery( query );

                while (rst.next())
                {
                    String column = Strings.toClassName( catalogConstantsColumn );
                    Object code   = rst.getObject( catalogConstantsColumn );
                    Object value  = rst.getObject( primaryKey );

                    if (code == null)
                        continue;

                    if (code instanceof String)
                    {
                        if (value instanceof String)
                            value = "\"" + value + "\"";

                        dbTable.addCatalogConstantsData(column, (String)code, value);
                    }
                    else
                    {
                        String fieldName = dbTable.getName() + "." + catalogConstantsColumn;
                        throw new ValidationException(prefix + "'" + fieldName + "' is not a String class");
                    }
                }

                if (isNotEmpty(dbTable.getCatalogConstantsData()))
                {
                    catalogs++;
                    break;
                }
            }
            catch (Exception e)
            {
                /* ignore */
            }
            finally
            {
                close(null, stmt, rst);
            }
        }

        return catalogs;
    }

}

