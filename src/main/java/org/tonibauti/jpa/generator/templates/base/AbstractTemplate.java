package org.tonibauti.jpa.generator.templates.base;

import org.tonibauti.jpa.generator.explorer.metada.DBColumn;
import org.tonibauti.jpa.generator.explorer.metada.DBConnection;
import org.tonibauti.jpa.generator.explorer.metada.DBForeignKey;
import org.tonibauti.jpa.generator.explorer.metada.DBIndex;
import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.main.AbstractComponent;
import org.tonibauti.jpa.generator.main.Workspace;
import org.tonibauti.jpa.generator.utils.Files;
import org.tonibauti.jpa.generator.utils.Resources;
import org.tonibauti.jpa.generator.utils.Strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public abstract class AbstractTemplate extends AbstractComponent
{
    protected Workspace workspace;
    protected List<DBTable> tables;

    private static final Set<String> filterTypes = new HashSet<String>()
    {{
        add( Boolean.class.getName() );
        add( Character.class.getName() );
        add( Byte.class.getName() );
        add( Short.class.getName() );
        add( Integer.class.getName() );
        add( Long.class.getName() );
        add( Float.class.getName() );
        add( Double.class.getName() );
        add( BigInteger.class.getName() );
        add( BigDecimal.class.getName() );
        add( String.class.getName() );
        add( java.util.Date.class.getName() );
        add( java.sql.Date.class.getName() );
        add( Time.class.getName() );
        add( Timestamp.class.getName() );
    }};


    protected AbstractTemplate(Workspace workspace, List<DBTable> tables)
    {
        super();
        setWorkspace( workspace );
        setTables( tables );
    }


    protected abstract String getSource(int index, DBTable dbTable);
    protected abstract String getTarget(int index, DBTable dbTable);
    protected abstract void preMapping(StringBuilder template);
    protected abstract Map<String, Object> getMapping(int index, DBTable dbTable);
    public abstract void generate() throws Exception;


    protected Workspace getWorkspace()
    {
        return workspace;
    }

    protected void setWorkspace(Workspace workspace)
    {
        this.workspace = workspace;
    }


    protected List<DBTable> getTables()
    {
        return tables;
    }

    protected void setTables(List<DBTable> tables)
    {
        this.tables = tables;
    }


    protected DBTable getTable(String tableName)
    {
        if (tables != null)
            for (DBTable dbTable : tables)
                if (dbTable.getName().equals(tableName))
                    return dbTable;

        return null;
    }


    protected boolean isPostgresDatabase()
    {
        return "postgresql".equalsIgnoreCase(workspace.getDbConnection().getDatabaseProductName());
    }


    protected String getDataSourcePropertiesName()
    {
        return workspace.getDataSourceName().replace("_","-").toLowerCase();
    }


    protected boolean isAutoGenerated(DBColumn dbColumn)
    {
        return (dbColumn.isAutoIncrement() || dbColumn.isGenerated());
    }


    protected boolean isIntegerType(DBColumn dbColumn)
    {
        return (dbColumn.getClassName().equals(Byte.class.getName())
                ||
                dbColumn.getClassName().equals(Short.class.getName())
                ||
                dbColumn.getClassName().equals(Integer.class.getName())
                ||
                dbColumn.getClassName().equals(Long.class.getName()));
    }


    protected boolean isFilterType(DBColumn dbColumn)
    {
        if (dbColumn.isJson())
            //return false;
            return true;
        else
            return filterTypes.contains(dbColumn.getClassName());
    }


    protected boolean isEmbeddable(DBTable dbTable)
    {
        return (workspace.isSpringDataMode() && dbTable.isMultipleKey());
    }


    protected String getJavaPackage()
    {
        return workspace.isSpring3() ? "jakarta" : "javax";
    }


    protected List<DBForeignKey.DBForeignKeyRef> getMultipleKeyWithDifferentNames(DBTable dbTable)
    {
        if (workspace.isSpringDataMode())
        {
            if (dbTable.isMultipleKey() &&  !dbTable.getForeignKeyList().isEmpty())
            {
                for (DBForeignKey dbForeignKey : dbTable.getForeignKeyList())
                {
                    // primary key with same size --> same PK
                    if (dbForeignKey.isPrimaryKeyJoin())
                    {
                        // different names
                        if (!dbForeignKey.isEqualsPrimaryKeyJoin())
                        {
                            return dbForeignKey.getForeignKeyRefList();
                        }
                    }
                }
            }
        }

        return new ArrayList<>();
    }


    protected String getSimpleKey(DBTable dbTable, List<String> importList)
    {
        String primaryKey = dbTable.getPrimaryKeyList().get(0);
        return getNormalizedType(dbTable.getColumn(primaryKey).getClassName(), importList);
    }


    protected String getMultipleKeyTableName(DBTable dbTable)
    {
        if (!dbTable.isMultipleKey())
            return null;

        String table = dbTable.getName();

        if (workspace.isSpringDataMode()) // TODO: in NativeMode??
        {
            for (DBForeignKey dbForeignKey : dbTable.getForeignKeyList())
            {
                // primary key with same size --> same PK
                if (dbForeignKey.isPrimaryKeyJoin())
                {
                    table = dbForeignKey.getForeignTable();
                    break;
                }
            }
        }

        return table;
    }


    protected String getMultipleKey(DBTable dbTable)
    {
        String multipleKeyTableName = getMultipleKeyTableName( dbTable );

        return (multipleKeyTableName != null)
                ? Strings.toClassName( multipleKeyTableName ) + "PK"
                : null;
    }


    protected List<FieldData> getSimpleIndex(DBTable dbTable, boolean isTest, List<String> importList)
    {
        List<FieldData> indexDataList = new ArrayList<>();

        for (DBIndex dbIndex : dbTable.getIndexList())
        {
            // is not simple
            if (dbIndex.getColumns().size() != 1)
                continue;

            String column = dbIndex.getColumns().get(0);
            DBColumn dbColumn = dbTable.getColumn(column);

            String property = Strings.toClassName(column);
            String param    = null;
            boolean unique  = dbIndex.isUnique();

            // partial key is not unique
            if (dbColumn.isPrimaryKey())
            {
                if (dbTable.isMultipleKey())
                {
                    property = "Id"+property; // EmbeddedId
                    unique = false;
                }
                else
                    continue;
            }

            // param
            if (!isTest)
                param = getNormalizedType(dbColumn.getClassName(), importList) + " " + Strings.toPropertyName(column);
            else
                param = "DataTestFactory.get" + getNormalizedType(dbColumn.getClassName(), importList) + "()";

            // unique
            if (!unique)
            {
                super.addToList(List.class.getName(), importList, false);

                if (!isTest)
                {
                    super.addToList("org.springframework.data.domain.Page", importList, false);
                    super.addToList("org.springframework.data.domain.Pageable", importList, false);
                }
            }

            FieldData indexData = new FieldData();

            indexData.setProperty( property );
            indexData.setParam( param );
            indexData.setUnique( unique );

            indexDataList.add( indexData );
        }

        return indexDataList;
    }


    protected List<FieldData> getMultipleIndex(DBTable dbTable, boolean isTest, List<String> importList)
    {
        List<FieldData> indexDataList = new ArrayList<>();

        for (DBIndex dbIndex : dbTable.getIndexList())
        {
            // is not multiple
            if (dbIndex.getColumns().size() <= 1)
                continue;

            // is the primary key
            if (super.listContainsSameElements(dbIndex.getColumns(), dbTable.getPrimaryKeyList(), false))
                continue;

            StringBuilder property = new StringBuilder();
            StringBuilder param    = new StringBuilder();
            boolean unique         = dbIndex.isUnique();

            // property and param
            for (String column : dbIndex.getColumns())
            {
                DBColumn dbColumn = dbTable.getColumn( column );

                if (property.length() > 0)
                    property.append("And");

                if (dbColumn.isPrimaryKey())
                    property.append("Id");  // EmbeddedId

                property.append( Strings.toClassName(column) );

                if (param.length() > 0)
                    param.append(", ");

                if (!isTest)
                    param.append( getNormalizedType(dbColumn.getClassName(), importList) + " " + Strings.toPropertyName(column) );
                else
                    param.append( "DataTestFactory.get" + getNormalizedType(dbColumn.getClassName(), importList) + "()" );
            }

            // unique
            if (!unique)
            {
                super.addToList(List.class.getName(), importList, false);

                if (!isTest)
                {
                    super.addToList("org.springframework.data.domain.Page", importList, false);
                    super.addToList("org.springframework.data.domain.Pageable", importList, false);
                }
            }

            FieldData indexData = new FieldData();

            indexData.setProperty( property.toString() );
            indexData.setParam( param.toString() );
            indexData.setUnique( unique );

            indexDataList.add( indexData );

        }

        return indexDataList;
    }


    protected String getNormalizedType(String type, List<String> importList)
    {
        if (Character.class.getName().equals(type)
            ||
            char[].class.getName().equals(type))
        {
            type = String.class.getSimpleName();
        }
        else
        if (BigInteger.class.getName().equals(type))
        {
            type = Long.class.getSimpleName();
        }
        else
        if (BigDecimal.class.getName().equals(type))
        {
            type = BigDecimal.class.getSimpleName();
            super.addToList(BigDecimal.class.getName(), importList, false);
            return type;
        }
        else
        if (UUID.class.getName().equals(type))
        {
            type = UUID.class.getSimpleName();
            super.addToList(UUID.class.getName(), importList, false);
            return type;
        }
        else
        if (java.util.Date.class.getName().equals(type))
        {
            type = java.util.Date.class.getSimpleName();
            super.addToList(java.util.Date.class.getName(), importList, false);
            return type;
        }
        else
        if (java.sql.Date.class.getName().equals(type))
        {
            if (workspace.isUseTimestampLikeDate())
            {
                type = java.util.Date.class.getSimpleName();
                super.addToList(java.util.Date.class.getName(), importList, false);
            }
            else
            {
                type = java.sql.Date.class.getSimpleName();
                super.addToList(java.sql.Date.class.getName(), importList, false);
            }

            return type;
        }
        else
        if (Timestamp.class.getName().equals(type))
        {
            if (workspace.isUseTimestampLikeDate())
            {
                type = java.util.Date.class.getSimpleName();
                super.addToList(java.util.Date.class.getName(), importList, false);
            }
            else
            {
                type = Timestamp.class.getSimpleName();
                super.addToList(Timestamp.class.getName(), importList, false);
            }

            return type;
        }
        else
        if (Time.class.getName().equals(type))
        {
            if (workspace.isUseTimestampLikeDate())
            {
                type = java.util.Date.class.getSimpleName();
                super.addToList(java.util.Date.class.getName(), importList, false);
            }
            else
            {
                type = Time.class.getSimpleName();
                super.addToList(Time.class.getName(), importList, false);
            }
        }
        else
        if (java.sql.Clob.class.getName().equals(type)
            ||
            java.sql.NClob.class.getName().equals(type))
        {
            type = String.class.getSimpleName();
            return type;
        }
        else
        if (java.sql.Blob.class.getName().equals(type))
        {
            type = DBConnection.BYTE_ARRAY_CLASS_NAME;
            return type;
        }

        type = type.replace("java.lang.", "");

        return type;
    }


    protected List<String> getJpaAnnotations(String column, DBColumn dbColumn, DBTable dbTable)
    {
        List<String> annotations = new ArrayList<>();

        // @Column(name = USER_ID_COLUMN, nullable = false, unique = true, length = 32, precision = 0, scale = 0)
        Map<String, String> map = new LinkedHashMap<>();

        // @Id
        if (dbColumn.isPrimaryKey() && !isEmbeddable(dbTable))
        {
            annotations.add( "@Id" );
        }

        if (isAutoGenerated(dbColumn) && !isEmbeddable(dbTable))
        {
            map.put("insertable", "false");
            map.put("updatable", "false");

            if (dbColumn.isUUID() || (dbColumn.isVarchar36() && workspace.isUseVarchar36LikeUuid()))
            {
                // @GenericGenerator(name = "UUID", strategy = "uuid2")
                // @GeneratedValue(generator = "UUID")

                if (workspace.isSpringDataMode())
                {
                    if (workspace.isSpring3())
                    {
                        annotations.add( "@UuidGenerator" );
                    }
                    else
                    {
                        annotations.add( "@GenericGenerator(name = \"UUID\", strategy = \"uuid2\")" );
                        annotations.add( "@GeneratedValue(generator = \"UUID\")" );
                    }
                }
                else
                {
                    annotations.add( "@GeneratedValue(generator = \"UUID\")" );
                }
            }
            else
            {
                // @GeneratedValue(strategy = GenerationType.IDENTITY)
                annotations.add( "@GeneratedValue(strategy = GenerationType.IDENTITY)" );
            }
        }
        else
        if (dbColumn.isPrimaryKey())
        {
            map.put("updatable", "false");
        }

        if (!dbColumn.isNullable())
        {
            map.put("nullable", ""+dbColumn.isNullable());
        }

        if (dbColumn.isUniqueKey())
        {
            map.put("unique", ""+dbColumn.isUniqueKey());
        }

        map.put("length", ""+dbColumn.getSize());

        if (dbColumn.getDecimalDigits() > 0)
        {
            /*
            The precision is the maximum number of digits or characters that are displayed for the data in that column.
            For nonnumeric data, the precision typically refers to the defined length of the column.
            The scale refers to the maximum number of digits that are displayed to the right of the decimal point.
            */
            //map.put("precision", ""+dbColumn.getSize());
            map.put("scale", ""+dbColumn.getDecimalDigits());
        }

        StringBuilder annotation = new StringBuilder();
        annotation.append("@Column(name = " + column + "_COLUMN");
        map.forEach((key, value) -> annotation.append(", " + key + " = " + value));
        annotation.append(")");

        annotations.add( annotation.toString() );

        /*
        //@Type(type = JsonType.TYPE)
        if (dbColumn.isJson())
        {
            annotations.add( "@Type(type = JsonType.TYPE)" );
        }
        */

        if (workspace.isUseAuditing())
        {
            if ("created_at".equalsIgnoreCase(column))
            {
                annotations.add( "@CreatedDate" );
            }
            else
            if ("updated_at".equalsIgnoreCase(column))
            {
                annotations.add( "@LastModifiedDate" );
            }
        }

        // @ToString.Exclude
        if (dbColumn.isInvisible())
        {
            annotations.add( "@ToString.Exclude" );
        }

        return annotations;
    }


    protected List<String> getJpaForeignKeyAnnotations(DBForeignKey dbForeignKey)
    {
        List<String> foreignKeyAnnotations = new ArrayList<>();

        String tab = "";

        String joinColumns = dbForeignKey.isPrimaryKeyJoin() ? "@PrimaryKeyJoinColumns" : "@JoinColumns";
        String joinColumn  = dbForeignKey.isPrimaryKeyJoin() ? "@PrimaryKeyJoinColumn"  : "@JoinColumn";

        boolean isCompositeKey = (dbForeignKey.getForeignKeyRefList().size() > 1); // multi foreign key

        if (isCompositeKey)
        {
            tab = "\t\t";
            foreignKeyAnnotations.add( joinColumns );
            foreignKeyAnnotations.add( "(" );
            foreignKeyAnnotations.add( "    value = " );
            foreignKeyAnnotations.add( "    {" );
        }

        int i = 0;
        for (DBForeignKey.DBForeignKeyRef dbForeignKeyRef : dbForeignKey.getForeignKeyRefList())
        {
            String foreignEntity = Strings.toClassName(dbForeignKeyRef.getReferencedTableName()) + "Entity";
            String foreignColumn = Strings.toColumnName(dbForeignKeyRef.getReferencedColumnName()) + "_COLUMN";

            foreignKeyAnnotations.add( tab + joinColumn );
            foreignKeyAnnotations.add( tab + "(" );
            foreignKeyAnnotations.add( tab + "    name = " + Strings.toColumnName(dbForeignKeyRef.getColumnName()) + "_COLUMN" + "," );

            if (!dbForeignKey.isPrimaryKeyJoin())
            {
                foreignKeyAnnotations.add( tab + "    nullable = " + dbForeignKey.isNullable() + "," );
                foreignKeyAnnotations.add( tab + "    unique = " + dbForeignKey.isUnique() + "," );
                foreignKeyAnnotations.add( tab + "    insertable = false" + "," );
                foreignKeyAnnotations.add( tab + "    updatable = false" + "," );
            }

            if (isCompositeKey)
            {
                foreignKeyAnnotations.add( tab + "    referencedColumnName = " + foreignEntity + "." + foreignColumn);
            }
            else
            {
                foreignKeyAnnotations.add( tab + "    referencedColumnName = " + foreignEntity + "." + foreignColumn + "," );
                foreignKeyAnnotations.add( tab + "    foreignKey = @ForeignKey(name = \"" +
                                                      dbForeignKey.getName() + "\", value = ConstraintMode.CONSTRAINT)" );
            }

            if (i++ < dbForeignKey.getForeignKeyRefList().size()-1)
                foreignKeyAnnotations.add( tab + ")," );
            else
                foreignKeyAnnotations.add( tab + ")" );
        }

        if (isCompositeKey)
        {
            foreignKeyAnnotations.add( "    }," );
            foreignKeyAnnotations.add( "    foreignKey = @ForeignKey(name = \"" +
                                            dbForeignKey.getName() + "\", value = ConstraintMode.CONSTRAINT)" );
            foreignKeyAnnotations.add( ")" );
        }

        return foreignKeyAnnotations;

        /*
        @JoinColumns
        (
            value =
            {
                @JoinColumn
                (
                    name = USER_ID_COLUMN,
                    referencedColumnName = UsersRolesEntity.USER_ID_COLUMN,
                    nullable = true,
                    unique = false,
                    insertable = false,
                    updatable = false
                ),
                @JoinColumn
                (
                    name = ROLE_ID_COLUMN,
                    referencedColumnName = UsersRolesEntity.ROLE_ID_COLUMN,
                    nullable = true,
                    unique = false,
                    insertable = false,
                    updatable = false
                )
            },
            foreignKey = @ForeignKey(name = "FK_users_roles_desc_user_roles", value = ConstraintMode.CONSTRAINT)
        )
        */
    }


    /*
    protected String getColumnComment(DBColumn dbColumn)
    {
        String comment = "";

        if (dbColumn.getClassName().equals(String.class.getName()))
            comment += "(" + Strings.toSpaceSize(dbColumn.getSize()) + ")";
        else
        if (dbColumn.getClassName().equals(DBConnection.BYTE_ARRAY_CLASS_NAME))
            comment += "(" + Strings.toSpaceSize(dbColumn.getSize()) + ")";

        if (dbColumn.isPrimaryKey())
            comment += " (pk)";

        if (dbColumn.isGenerated())
            comment += " (generated)";

        if (dbColumn.isAutoIncrement())
            comment += " (auto)";

        if (dbColumn.isUniqueKey())
            comment += " (unique)";

        if (!dbColumn.isNullable())
            comment += " (required)";

        if (dbColumn.isEncoded())
            comment += " (encoded)";

        // TODO: ??
        //if (isNotEmpty(dbColumn.getForeignKey()))
        //    comment += " (fk -> " + dbColumn.getForeignTable() + "."  + dbColumn.getForeignKey() + ")";

        return comment;
    }
    */


    protected void generate(int index, DBTable dbTable) throws Exception
    {
        String source = getSource(index, dbTable);

        if (isNullOrEmpty(source))
            return;

        String target = getTarget(index, dbTable);

        if (isNullOrEmpty(target))
            return;

        StringBuilder template = Files.readStream( Resources.getResourceAsStream(source) );

        preMapping( template );

        Map<String, Object> mapping = getMapping(index, dbTable);

        if (isNotEmpty(mapping))
            FreemarkerTemplates.getInstance().process(source, template, mapping);

        Files.writeFile(template.toString(), target);
    }


    protected void generate(int index) throws Exception
    {
        generate(index, null);
    }

}

