package org.tonibauti.jpa.generator.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotNull;
import org.tonibauti.jpa.generator.main.AbstractComponent;
import org.tonibauti.jpa.generator.validators.StringValues;

import java.util.Map;
import java.util.Set;


public class JpaConfig extends AbstractComponent
{
    public static final String PROVIDER_HIBERNATE = "hibernate";

    @JsonProperty("provider")
    @NotNull
    @StringValues(PROVIDER_HIBERNATE)
    protected String provider;

    @JsonProperty("properties")
    protected Map<String, String> properties;


    public JpaConfig()
    {
        super();
    }


    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

    public Set<ConstraintViolation<?>> validate()
    {
        Set<ConstraintViolation<?>> constraintViolations = super.validate( this );

        return constraintViolations;
    }

}

