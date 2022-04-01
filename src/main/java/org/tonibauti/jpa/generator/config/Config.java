package org.tonibauti.jpa.generator.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.tonibauti.jpa.generator.main.AbstractComponent;

import java.util.Set;


public class Config extends AbstractComponent
{
    @JsonProperty("version")
    @NotBlank
    protected String version;

    @JsonProperty("generator")
    @NotNull
    protected GeneratorConfig generatorConfig;


    public Config()
    {
        super();
    }


    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public GeneratorConfig getGeneratorConfig()
    {
        return generatorConfig;
    }

    public void setGeneratorConfig(GeneratorConfig generatorConfig)
    {
        this.generatorConfig = generatorConfig;
    }

    public Set<ConstraintViolation<?>> validate()
    {
        Set<ConstraintViolation<?>> constraintViolations = super.validate( this );

        if (isNotEmpty(generatorConfig))
            constraintViolations.addAll( generatorConfig.validate() );

        return constraintViolations;
    }

}

