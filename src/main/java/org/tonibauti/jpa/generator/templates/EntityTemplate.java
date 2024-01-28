package org.tonibauti.jpa.generator.templates;

import org.tonibauti.jpa.generator.explorer.metada.DBColumn;
import org.tonibauti.jpa.generator.explorer.metada.DBForeignKey;
import org.tonibauti.jpa.generator.explorer.metada.DBIndex;
import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.main.Workspace;
import org.tonibauti.jpa.generator.templates.base.AbstractTemplate;
import org.tonibauti.jpa.generator.templates.base.FieldData;
import org.tonibauti.jpa.generator.utils.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class EntityTemplate extends AbstractTemplate
{
    private static final String[] SOURCE =
    {
        //"templates/entities/BaseEntity.fm",
        //"templates/entities/ExtendsPostgreSQLDialect.fm",
        "templates/entities/Json.fm",
        "templates/entities/JsonType.fm",
        "templates/entities/Entity.fm",
        "templates/entities/EntityPK.fm",
        "templates/entities/Dto.fm",
    };

    private static final String[] TARGET =
    {
        //"BaseEntity.java",
        //"ExtendsPostgreSQLDialect.java",
        "Json.java",
        "JsonType.java",
        "${ClassName}Entity.java",
        "${MultipleKey}.java", // EntityPK
        "${ClassName}Dto.java",
    };


    public EntityTemplate(Workspace workspace, List<DBTable> tables)
    {
        super(workspace, tables);
    }


    @Override
    public String getSource(int index, DBTable dbTable)
    {
        if (SOURCE[index].contains("Entity") && workspace.isNativeSqlMode())
        {
            return SOURCE[index].replace("Entity", "EntityNative");
        }

        return SOURCE[index];
    }


    @Override
    public String getTarget(int index, DBTable dbTable)
    {
        if (TARGET[index].startsWith("${ClassName}"))
        {
            String className = Strings.toClassName( dbTable.getName() );
            return getWorkspace().getEntitiesDir() + TARGET[index].replace("${ClassName}", className);
        }
        else
        if (TARGET[index].startsWith("${MultipleKey}"))
        {
            String multipleKey = super.getMultipleKey( dbTable );

            if (multipleKey == null)
                return null;

            if (!dbTable.getName().equals(super.getMultipleKeyTableName(dbTable)))
                return null;

            return getWorkspace().getEntitiesDir() + TARGET[index].replace("${MultipleKey}", multipleKey);
        }
        else
        if (TARGET[index].startsWith("Json") && !dbTable.isJson())
        {
            return null;
        }
        else
        {
            return getWorkspace().getPersistenceDir() + TARGET[index];
        }
    }


    @Override
    protected void preMapping(StringBuilder template)
    {
    }


    public static class ForeignKey
    {
        DBForeignKey fk;
        String mode;
        String mappedBy;
        String suffix;
        List<String> annotations;
        public DBForeignKey getFk() { return fk; }
        public String getMode() { return mode; }
        public String getMappedBy() { return mappedBy; }
        public String getSuffix() { return suffix; }
        public List<String> getAnnotations() { return annotations; }
    }


    private List<ForeignKey> readForeignKeys(DBTable dbTable, boolean isExternalFK, List<String> importList)
    {
        List<ForeignKey> foreignKeyList = new ArrayList<>();

        String className = Strings.toClassName( dbTable.getName() );

        Map<String, List<ForeignKey>> auxForeignKey = new LinkedHashMap<>();

        Collection<DBForeignKey> dbForeignKeyList = (isExternalFK)
                                                        ? dbTable.getExternalForeignKeyList()
                                                        : dbTable.getForeignKeyList();

        for (DBForeignKey dbForeignKey : dbForeignKeyList)
        {
            ForeignKey foreignKey = new ForeignKey();
            foreignKey.fk = dbForeignKey;
            foreignKey.mappedBy = className+"Entity.NAME";
            foreignKey.suffix = "";
            foreignKey.annotations = super.getJpaForeignKeyAnnotations( dbForeignKey );

            if (isExternalFK)
                foreignKey.mode = dbForeignKey.isMany() ? "@OneToMany" : "@OneToOne";
            else
                foreignKey.mode = dbForeignKey.isMany() ? "@ManyToOne" : "@OneToOne";

            if (isExternalFK && dbForeignKey.isMany())
                super.addToList(List.class.getName(), importList, false);

            foreignKeyList.add( foreignKey );

            if (dbForeignKey.getForeignKeyRefList().size() == 1) // single foreign key
            {
                auxForeignKey.computeIfAbsent(dbForeignKey.getTable() + dbForeignKey.getForeignTable(),
                                              (key) -> new ArrayList<>()).add( foreignKey );
            }
        }

        for (Collection<ForeignKey> list : auxForeignKey.values())
        {
            if (list.size() > 1)
            {
                for (ForeignKey foreignKey : list)
                {
                    foreignKey.suffix = ("By" + Strings.toClassName( (isExternalFK)
                                                                        ? foreignKey.fk.getForeignColumn()
                                                                        : foreignKey.fk.getColumn()) );

                    foreignKey.mappedBy += ("+" + "\"" + foreignKey.suffix + "\"");
                }
            }
        }

        return foreignKeyList;
    }

    @Override
    public Map<String, Object> getMapping(int index, DBTable dbTable)
    {
        Map<String, Object> map = new HashMap<>();

        String quoteId = workspace.getDbConnection().getQuoteId();

        String tableName  = dbTable.isQuoted() ? (quoteId + dbTable.getName() + quoteId) : dbTable.getName();
        String className  = Strings.toClassName( dbTable.getName() );
        String objectName = Strings.toPropertyName( dbTable.getName() );

        List<FieldData> fieldDataList   = new ArrayList<>();
        List<FieldData> pkFieldDataList = new ArrayList<>();
        List<FieldData> indexDataList   = new ArrayList<>();
        //List<FieldData> generatedKeyDataList = new ArrayList<>();

        boolean isPersistable = dbTable.isMultipleKey();
        boolean isGenericGenerator = false;

        List<FieldData> encodedDataList = new ArrayList<>();
        List<String> importList = new ArrayList<>();
        boolean isJson = false;

        List<FieldData> encodedDataListPK = new ArrayList<>();
        List<String> importListPK = new ArrayList<>();
        boolean isJsonPK = false;


        List<ForeignKey> externalForeignKeyList = readForeignKeys(dbTable, true, importList);
        List<ForeignKey> foreignKeyList = readForeignKeys(dbTable, false, importList);
        /*
        foreignKeyList.forEach(foreignKey -> {
            if (foreignKey.fk.isPrimaryKeyJoin())
                System.err.println( foreignKey.fk.getTable() + "." +  foreignKey.fk.getColumn());
        });
        */


        for (DBColumn dbColumn : dbTable.getColumnList())
        {
            FieldData fieldData = new FieldData();

            String columnName = dbColumn.isQuoted() ? (quoteId + dbColumn.getName() + quoteId) : dbColumn.getName();

            fieldData.setName( columnName );
            fieldData.setColumn( Strings.toColumnName(dbColumn.getName()) );
            fieldData.setProperty( Strings.toPropertyName(dbColumn.getName()) );
            fieldData.setAnnotations( super.getJpaAnnotations(fieldData.getColumn(), dbColumn, dbTable, foreignKeyList) );
            fieldData.setPk( dbColumn.isPrimaryKey() );
            fieldData.setGenerated( super.isAutoGenerated(dbColumn) );

            //fieldData.setComment( super.getColumnComment(dbColumn) );

            //if (super.isAutoGenerated(dbColumn))
            //    generatedKeyDataList.add( fieldData );


            if (!isPersistable)
                if ("id".equals(fieldData.getProperty()))
                    isPersistable = true;

            // isUUID && !isMultipleKey
            if (!isGenericGenerator)
                if (dbColumn.isUUID() || (dbColumn.isVarchar36() && workspace.isUseVarchar36LikeUuid()))
                    if (!dbTable.isMultipleKey())
                        isGenericGenerator = true;


            if (dbColumn.isPrimaryKey())
            {
                pkFieldDataList.add( fieldData );
                fieldData.setType( super.getNormalizedType(dbColumn.getClassName(), importListPK) );
                isJsonPK |= dbColumn.isJson();

                if (dbColumn.isEncoded())
                    encodedDataListPK.add( fieldData );
            }
            else
            {
                fieldData.setType( super.getNormalizedType(dbColumn.getClassName(), importList) );
                isJson |= dbColumn.isJson();

                if (dbColumn.isEncoded())
                    encodedDataList.add( fieldData );
            }

            fieldDataList.add( fieldData );
        }


        if (!super.isEmbeddable(dbTable))
        {
            encodedDataList.addAll(0, encodedDataListPK);
            importList.addAll(0, importListPK);
            isJson |= isJsonPK;
        }


        for (DBIndex dbIndex : dbTable.getIndexList())
        {
            for (String column : dbIndex.getColumns())
            {
                FieldData fieldData = new FieldData();
                fieldData.setName( dbIndex.getName() );
                fieldData.setColumn( Strings.toColumnName(column) );
                fieldData.setUnique( dbIndex.isUnique() );
                indexDataList.add( fieldData );
            }
        }


        String keyType = dbTable.isMultipleKey()
                            ? super.getMultipleKey( dbTable )
                            : super.getSimpleKey(dbTable, importList);

        map.put("javaPackage", super.getJavaPackage());
        map.put("PersistencePackage", getWorkspace().getPersistencePackage());
        map.put("EntitiesPackage", getWorkspace().getEntitiesPackage());
        map.put("Table", tableName);
        map.put("Catalog", Strings.getTrimNotNull(dbTable.getCatalog()));
        map.put("Schema", Strings.getTrimNotNull(dbTable.getSchema()));
        map.put("Dto", className+"Dto");
        map.put("Entity", className+"Entity");
        map.put("entity", objectName+"Entity");
        map.put("Key", keyType);
        map.put("isPersistable", isPersistable);
        map.put("isGenericGenerator", isGenericGenerator && !workspace.isSpring3());
        map.put("isUuidGenerator", isGenericGenerator && workspace.isSpring3());
        map.put("encodedDataList", encodedDataList);
        map.put("importList", importList);
        map.put("isJson", isJson);
        map.put("encodedDataListPK", encodedDataListPK);
        map.put("importListPK", importListPK);
        map.put("isJsonPK", isJsonPK);
        map.put("fieldDataList", fieldDataList);
        map.put("pkFieldDataList", pkFieldDataList);
        map.put("indexDataList", indexDataList);

        map.put("isGenerateJoins", getWorkspace().isGenerateJoins());
        //map.put("generatedKeyDataList", generatedKeyDataList);
        map.put("externalForeignKeyList", externalForeignKeyList);
        map.put("foreignKeyList", foreignKeyList);

        map.put("isMultipleKey", dbTable.isMultipleKey());
        map.put("multipleKeyWithDifferentNames", super.getMultipleKeyWithDifferentNames(dbTable));
        map.put("auditing", getWorkspace().isUseAuditing());
        map.put("builder", getWorkspace().isUseBuilders());
        map.put("encoder", getWorkspace().getEncoder());

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

