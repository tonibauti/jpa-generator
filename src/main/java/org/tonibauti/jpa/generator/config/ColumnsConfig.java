package org.tonibauti.jpa.generator.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotEmpty;
import org.tonibauti.jpa.generator.main.AbstractComponent;

import java.util.List;
import java.util.Set;


public class ColumnsConfig extends AbstractComponent
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

    @JsonProperty("encoded")
    protected List<String> encoded;

    @JsonProperty("invisible")
    protected List<String> invisible;


    public ColumnsConfig()
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

    public List<String> getEncoded()
    {
        return encoded;
    }

    public void setEncoded(List<String> encoded)
    {
        this.encoded = encoded;
    }

    public List<String> getInvisible()
    {
        return invisible;
    }

    public void setInvisible(List<String> invisible)
    {
        this.invisible = invisible;
    }

    public Set<ConstraintViolation<?>> validate()
    {
        Set<ConstraintViolation<?>> constraintViolations = super.validate( this );

        return constraintViolations;
    }

}

