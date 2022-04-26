package org.tonibauti.jpa.generator.explorer.metada;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;


public class DBForeignKey
{
    public static class DBForeignKeyRef
    {
        private final DBColumn column;
        private final DBColumn referencedColumn;

        public DBForeignKeyRef(DBColumn column, DBColumn referencedColumn)
        {
            this.column = column;
            this.referencedColumn = referencedColumn;
        }

        private DBColumn getColumn()
        {
            return column;
        }

        public String getColumnName()
        {
            return column.getName();
        }

        private DBColumn getReferencedColumn()
        {
            return referencedColumn;
        }

        public String getReferencedTableName()
        {
            return referencedColumn.getTable();
        }

        public String getReferencedColumnName()
        {
            return referencedColumn.getName();
        }
    }

    private final String name;
    private final boolean external;
    private final DBTable table;
    private final DBTable referencedTable;
    private final List<DBForeignKeyRef> dbForeignKeyRefList = new ArrayList<>();
    private boolean primaryKey;
    private boolean nullable;
    private boolean unique;
    private boolean referencedPrimaryKey;
    private boolean referencedNullable;
    private boolean referencedUnique;
    private boolean many;


    public DBForeignKey(String foreignKeyName,
                        boolean external,
                        DBTable table,
                        DBColumn dbColumn,
                        DBTable referencedTable,
                        DBColumn referencedColumn)
    {
        this.name            = foreignKeyName;
        this.external        = external;
        this.table           = table;
        this.referencedTable = referencedTable;

        addForeignKeyRef(dbColumn, referencedColumn);
    }


    public String getName()
    {
        return name;
    }

    public boolean isExternal()
    {
        return external;
    }

    public String getTable()
    {
        return table.getName();
    }

    @JsonIgnore
    public String getColumn()
    {
        return dbForeignKeyRefList.get(0).getColumnName();
    }

    public String getForeignTable()
    {
        return referencedTable.getName();
    }

    @JsonIgnore
    public String getForeignColumn()
    {
        return dbForeignKeyRefList.get(0).getReferencedColumnName();
    }

    public List<DBForeignKeyRef> getForeignKeyRefList()
    {
        return dbForeignKeyRefList;
    }

    @JsonIgnore
    public void addForeignKeyRef(DBColumn dbColumn, DBColumn dbReferencedColumn)
    {
        dbForeignKeyRefList.add( new DBForeignKeyRef(dbColumn, dbReferencedColumn) );
        analyze();
    }

    @JsonIgnore
    public boolean isPrimaryKeyJoin()
    {
        return (primaryKey
                &&
                referencedPrimaryKey
                &&
                table.getPrimaryKeyList().equals(referencedTable.getPrimaryKeyList()));
                //table.getPrimaryKeyList().size() == referencedTable.getPrimaryKeyList().size());
    }

    public boolean isNullable()
    {
        return nullable;
    }

    public boolean isUnique()
    {
        return unique;
    }

    public boolean isMany()
    {
        return many;
    }

    private void analyze()
    {
        primaryKey = true;
        nullable   = false;     // annotation default value: true  --> then is "false" if all is false
        unique     = true;      // annotation default value: false --> then is "true" if all is true
        referencedPrimaryKey = true;
        referencedNullable   = false;
        referencedUnique     = true;

        int countUnique = 0;
        int countReferencedUnique = 0;

        for (DBForeignKeyRef dbForeignKeyRef : dbForeignKeyRefList)
        {
            primaryKey &= dbForeignKeyRef.getColumn().isPrimaryKey();
            nullable |= dbForeignKeyRef.getColumn().isNullable(); // check is all is false
            unique &= dbForeignKeyRef.getColumn().isUniqueKey();  // check is all is true

            if (dbForeignKeyRef.getColumn().isUniqueKey())
                countUnique++;

            referencedPrimaryKey &= dbForeignKeyRef.getReferencedColumn().isPrimaryKey();
            referencedNullable |= dbForeignKeyRef.getReferencedColumn().isNullable(); // check is all is false
            referencedUnique &= dbForeignKeyRef.getReferencedColumn().isUniqueKey();  // check is all is true

            if (dbForeignKeyRef.getReferencedColumn().isUniqueKey())
                countReferencedUnique++;
        }

        if (primaryKey && referencedPrimaryKey)
        {
            many = (table.getPrimaryKeyList().size() != referencedTable.getPrimaryKeyList().size());
        }
        else
        if (unique && referencedPrimaryKey)
        {
            many = (countUnique != referencedTable.getPrimaryKeyList().size());
        }
        else
        if (primaryKey && referencedUnique)
        {
            many = (table.getPrimaryKeyList().size() != countReferencedUnique);
        }
        else
        if (unique && referencedUnique)
        {
            many = (countUnique != countReferencedUnique);
        }
        else
        {
            many = true;
        }
    }

}

