package org.tonibauti.jpa.generator.templates.base;

import org.tonibauti.jpa.generator.utils.Strings;

import java.util.List;


public class FieldData
{
    private String name;
    private String column;
    private String type;
    private String property;
    private String param;
    private boolean unique;
    private List<String> annotations;
    private String getter;
    private String setter;
    private String findBy;
    /*
    private String comment;
    */


    public FieldData() {}


    public FieldData(String name, String property, String column, String type)
    {
        setName( name );
        setProperty( property );
        setColumn( column );
        setType( type );
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getColumn()
    {
        return column;
    }

    public void setColumn(String column)
    {
        this.column = column;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty(String property)
    {
        this.property = property;
        this.getter   = "get"+Strings.capitalizeFirstChar(property);
        this.setter   = "set"+Strings.capitalizeFirstChar(property);
        this.findBy   = "findBy"+Strings.capitalizeFirstChar(property);
    }

    public String getParam()
    {
        return param;
    }

    public void setParam(String param)
    {
        this.param = param;
    }

    public boolean isUnique()
    {
        return unique;
    }

    public void setUnique(boolean unique)
    {
        this.unique = unique;
    }

    public List<String> getAnnotations()
    {
        return annotations;
    }

    public void setAnnotations(List<String> annotations)
    {
        this.annotations = annotations;
    }

    public String getGetter()
    {
        return getter;
    }

    public String getSetter()
    {
        return setter;
    }

    public String getFindBy()
    {
        return findBy;
    }

    /*
    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }
    */

}

