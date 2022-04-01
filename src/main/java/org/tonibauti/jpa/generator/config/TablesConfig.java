package org.tonibauti.jpa.generator.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotEmpty;
import org.tonibauti.jpa.generator.main.AbstractComponent;

import java.util.List;
import java.util.Set;


public class TablesConfig extends AbstractComponent
{
    @JsonProperty("includes")
    @NotEmpty
    protected List<String> includes;

    @JsonProperty("excludes")
    @NotEmpty
    protected List<String> excludes;

    @JsonProperty("catalog-constants")
    @JsonAlias("catalogConstants")
    protected List<String> catalogConstants;


    public TablesConfig()
    {
        super();
    }

    public List<String> getIncludes()
    {
        return includes;
    }

    public void setIncludes(List<String> includes)
    {
        this.includes = includes;
    }

    public List<String> getExcludes()
    {
        return excludes;
    }

    public void setExcludes(List<String> excludes)
    {
        this.excludes = excludes;
    }

    public List<String> getCatalogConstants()
    {
        return catalogConstants;
    }

    public void setCatalogConstants(List<String> catalogConstants)
    {
        this.catalogConstants = catalogConstants;
    }

    public Set<ConstraintViolation<?>> validate()
    {
        Set<ConstraintViolation<?>> constraintViolations = super.validate( this );

        return constraintViolations;
    }

}

