package org.tonibauti.jpa.generator.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.tonibauti.jpa.generator.cli.Console;
import org.tonibauti.jpa.generator.main.AbstractComponent;
import org.tonibauti.jpa.generator.validators.CheckPath;
import org.tonibauti.jpa.generator.validators.StringValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ProjectConfig extends AbstractComponent
{
    public static final String PROJECT_TYPE_SPRING   = "spring";
    public static final String PROJECT_TYPE_SPRING_3 = "spring3";
    public static final String PROJECT_TYPE_QUARKUS  = "quarkus";

    @JsonProperty("type")
    @NotNull
    @StringValues(PROJECT_TYPE_SPRING + "," + PROJECT_TYPE_SPRING_3)
    protected String type;

    @JsonProperty("path")
    @NotBlank
    @CheckPath(createIfNoExists = true)
    protected String path;

    @JsonProperty("config-package")
    @JsonAlias("configPackage")
    @NotBlank
    protected String configPackage;

    @JsonProperty("persistence-package")
    @JsonAlias("persistencePackage")
    @NotBlank
    protected String persistencePackage;

    @JsonProperty("persistence-test-package")
    @JsonAlias("persistenceTestPackage")
    @NotBlank
    protected String persistenceTestPackage;

    @JsonProperty("entities-package")
    @JsonAlias("entitiesPackage")
    @NotBlank
    protected String entitiesPackage;

    @JsonProperty("repositories-package")
    @JsonAlias("repositoriesPackage")
    @NotBlank
    protected String repositoriesPackage;

    @JsonProperty("use-auditing")
    @JsonAlias("useAuditing")
    @NotNull
    protected Boolean useAuditing;

    @JsonProperty("use-builders")
    @JsonAlias("useBuilders")
    @NotNull
    protected Boolean useBuilders;

    @JsonProperty("use-timestamp-like-date")
    @JsonAlias("useTimestampLikeDate")
    @NotNull
    protected Boolean useTimestampLikeDate;

    @JsonProperty("use-varchar36-like-uuid")
    @JsonAlias("useVarchar36LikeUuid")
    @NotNull
    protected Boolean useVarchar36LikeUuid;

    @JsonAlias("encoder")
    protected String encoder;


    /* TODO: ??
    @JsonProperty("log-aspect")
    @JsonAlias("logAspect")
    @NotNull
    protected Boolean logAspect;
    */

    @JsonProperty("log-annotation")
    @JsonAlias("logAnnotation")
    protected String logAnnotation;

    @JsonProperty("tables")
    @NotNull
    protected TablesConfig tablesConfig;

    @JsonProperty("columns")
    @NotNull
    protected ColumnsConfig columnsConfig;


    public ProjectConfig()
    {
        super();
    }


    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getConfigPackage()
    {
        return configPackage;
    }

    public void setConfigPackage(String configPackage)
    {
        this.configPackage = configPackage;
    }

    public String getPersistencePackage()
    {
        return persistencePackage;
    }

    public void setPersistencePackage(String persistencePackage)
    {
        this.persistencePackage = persistencePackage;
    }

    public String getPersistenceTestPackage()
    {
        return persistenceTestPackage;
    }

    public void setPersistenceTestPackage(String persistenceTestPackage)
    {
        this.persistenceTestPackage = persistenceTestPackage;
    }



    public String getEntitiesPackage()
    {
        return entitiesPackage;
    }

    public void setEntitiesPackage(String entitiesPackage)
    {
        this.entitiesPackage = entitiesPackage;
    }

    public String getRepositoriesPackage()
    {
        return repositoriesPackage;
    }

    public void setRepositoriesPackage(String repositoriesPackage)
    {
        this.repositoriesPackage = repositoriesPackage;
    }


    public Boolean getUseAuditing()
    {
        return useAuditing;
    }

    public Boolean getUseBuilders()
    {
        return useBuilders;
    }

    public void setUseBuilders(Boolean useBuilders)
    {
        this.useBuilders = useBuilders;
    }

    public Boolean getUseTimestampLikeDate()
    {
        return useTimestampLikeDate;
    }

    public void setUseTimestampLikeDate(Boolean useTimestampLikeDate)
    {
        this.useTimestampLikeDate = useTimestampLikeDate;
    }

    public Boolean getUseVarchar36LikeUuid()
    {
        return useVarchar36LikeUuid;
    }

    public void setUseVarchar36LikeUuid(Boolean useVarchar36LikeUuid)
    {
        this.useVarchar36LikeUuid = useVarchar36LikeUuid;
    }

    public String getEncoder()
    {
        return encoder;
    }

    public void setEncoder(String encoder)
    {
        this.encoder = encoder;
    }

    public String getLogAnnotation()
    {
        return logAnnotation;
    }

    public void setLogAnnotation(String logAnnotation)
    {
        this.logAnnotation = logAnnotation;
    }

    public TablesConfig getTablesConfig()
    {
        return tablesConfig;
    }

    public void setTablesConfig(TablesConfig tablesConfig)
    {
        this.tablesConfig = tablesConfig;
    }

    public ColumnsConfig getColumnsConfig()
    {
        return columnsConfig;
    }

    public void setColumnsConfig(ColumnsConfig columnsConfig)
    {
        this.columnsConfig = columnsConfig;
    }

    public Set<ConstraintViolation<?>> validate()
    {
        Set<ConstraintViolation<?>> constraintViolations = super.validate( this );

        if (isNotEmpty(tablesConfig))
            constraintViolations.addAll( tablesConfig.validate() );

        if (isNotEmpty(columnsConfig))
            constraintViolations.addAll( columnsConfig.validate() );


        // validate encoded columns and encoder class
        if (isNotEmpty(getColumnsConfig().getEncoded()))
        {
            List<String> encodedList = new ArrayList<>( getColumnsConfig().getEncoded() );
            removeNullsAndBlanks( encodedList );

            if (isNotEmpty(encodedList) && isNullOrEmpty(encoder))
                Console.throwValidationException("columns encoded are defined but encoder class is undefined");
        }

        return constraintViolations;
    }

}

