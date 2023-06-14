package org.tonibauti.jpa.generator.templates;

import org.tonibauti.jpa.generator.explorer.metada.DBColumn;
import org.tonibauti.jpa.generator.explorer.metada.DBTable;
import org.tonibauti.jpa.generator.main.Workspace;
import org.tonibauti.jpa.generator.templates.base.AbstractTemplate;
import org.tonibauti.jpa.generator.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CatalogConstantsTemplate extends AbstractTemplate
{
    private static final String[] SOURCE =
    {
        "templates/entities/CatalogConstants.fm",
    };

    private static final String[] TARGET =
    {
        "${ClassName}.java",
    };


    public CatalogConstantsTemplate(Workspace workspace, List<DBTable> tables)
    {
        super(workspace, tables);
    }


    @Override
    public String getSource(int index, DBTable dbTable)
    {
        return SOURCE[index];
    }


    @Override
    public String getTarget(int index, DBTable dbTable)
    {
        String className = Strings.toClassName( dbTable.getName() );
        return getWorkspace().getCatalogConstantsDir() + TARGET[index].replace("${ClassName}", className);
    }


    @Override
    protected void preMapping(StringBuilder template)
    {
    }


    @Override
    public Map<String, Object> getMapping(int index, DBTable dbTable)
    {
        Map<String, Object> map = new HashMap<>();

        String className = Strings.toClassName( dbTable.getName() );

        List<String> importList = new ArrayList<>();

        DBColumn pkColumn = dbTable.getColumn( dbTable.getPrimaryKeyList().get(0) );

        String pkType = (super.isIntegerType(pkColumn))
                        ? "int"
                        : super.getNormalizedType(pkColumn.getClassName(), importList);

        map.put("javaPackage", super.getJavaPackage());
        map.put("CatalogConstantsPackage", getWorkspace().getCatalogConstantsPackage());
        map.put("ClassName", className);
        map.put("importList", importList);
        map.put("pkType", pkType);
        map.put("catalogConstantsData", dbTable.getCatalogConstantsData());

        return map;
    }


    @Override
    public void generate() throws Exception
    {
        for (int index=0; index<SOURCE.length; index++)
            for (DBTable table : getTables())
                if (!table.getCatalogConstantsData().isEmpty())
                    super.generate(index, table);
    }

}

