package org.tonibauti.jpa.generator.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotNull;
import org.tonibauti.jpa.generator.main.AbstractComponent;

import java.util.Set;


public class GeneratorConfig extends AbstractComponent
{
    @JsonProperty("generate-config")
    @JsonAlias("generateConfig")
    @NotNull
    protected Boolean generateConfig;

    @JsonProperty("generate-entities")
    @JsonAlias("generateEntities")
    @NotNull
    protected Boolean generateEntities;

    @JsonProperty("generate-crud-repositories")
    @JsonAlias("generateCrudRepositories")
    @NotNull
    protected Boolean generateCrudRepositories;

    @JsonProperty("generate-crud-repositories-test")
    @JsonAlias("generateCrudRepositoriesTest")
    @NotNull
    protected Boolean generateCrudRepositoriesTest;

    @JsonProperty("generate-crud-native-repositories")
    @JsonAlias("generateCrudNativeRepositories")
    @NotNull
    protected Boolean generateCrudNativeRepositories;

    @JsonProperty("generate-crud-native-repositories-test")
    @JsonAlias("generateCrudNativeRepositoriesTest")
    @NotNull
    protected Boolean generateCrudNativeRepositoriesTest;

    @JsonProperty("generate-joins")
    @JsonAlias("generateJoins")
    @NotNull
    protected Boolean generateJoins;

    @JsonProperty("generate-catalog-constants")
    @JsonAlias("generateCatalogConstants")
    protected Boolean generateCatalogConstants;

    @JsonProperty("datasource")
    @NotNull
    protected DataSourceConfig dataSourceConfig;

    @JsonProperty("jpa")
    @NotNull
    protected JpaConfig jpaConfig;

    @JsonProperty("project")
    @NotNull
    protected ProjectConfig projectConfig;


    public GeneratorConfig()
    {
        super();
    }


    public Boolean getGenerateConfig()
    {
        return generateConfig;
    }

    public void setGenerateConfig(Boolean generateConfig)
    {
        this.generateConfig = generateConfig;
    }

    public Boolean getGenerateEntities()
    {
        return generateEntities;
    }

    public void setGenerateEntities(Boolean generateEntities)
    {
        this.generateEntities = generateEntities;
    }

    public Boolean getGenerateCrudRepositories()
    {
        return generateCrudRepositories;
    }

    public void setGenerateCrudRepositories(Boolean generateCrudRepositories)
    {
        this.generateCrudRepositories = generateCrudRepositories;
    }

    public Boolean getGenerateCrudRepositoriesTest()
    {
        return generateCrudRepositoriesTest;
    }

    public void setGenerateCrudRepositoriesTest(Boolean generateCrudRepositoriesTest)
    {
        this.generateCrudRepositoriesTest = generateCrudRepositoriesTest;
    }

    public Boolean getGenerateCrudNativeRepositories()
    {
        return generateCrudNativeRepositories;
    }

    public void setGenerateCrudNativeRepositories(Boolean generateCrudNativeRepositories)
    {
        this.generateCrudNativeRepositories = generateCrudNativeRepositories;
    }

    public Boolean getGenerateCrudNativeRepositoriesTest()
    {
        return generateCrudNativeRepositoriesTest;
    }

    public void setGenerateCrudNativeRepositoriesTest(Boolean generateCrudNativeRepositoriesTest)
    {
        this.generateCrudNativeRepositoriesTest = generateCrudNativeRepositoriesTest;
    }

    public Boolean getGenerateJoins()
    {
        return generateJoins;
    }

    public void setGenerateJoins(Boolean generateJoins)
    {
        this.generateJoins = generateJoins;
    }

    public Boolean getGenerateCatalogConstants()
    {
        return generateCatalogConstants;
    }

    public void setGenerateCatalogConstants(Boolean generateCatalogConstants)
    {
        this.generateCatalogConstants = generateCatalogConstants;
    }

    public DataSourceConfig getDataSourceConfig()
    {
        return dataSourceConfig;
    }

    public void setDataSourceConfig(DataSourceConfig dataSourceConfig)
    {
        this.dataSourceConfig = dataSourceConfig;
    }

    public JpaConfig getJpaConfig()
    {
        return jpaConfig;
    }

    public void setJpaConfig(JpaConfig jpaConfig)
    {
        this.jpaConfig = jpaConfig;
    }

    public ProjectConfig getProjectConfig()
    {
        return projectConfig;
    }

    public void setProjectConfig(ProjectConfig projectConfig)
    {
        this.projectConfig = projectConfig;
    }

    public Set<ConstraintViolation<?>> validate()
    {
        Set<ConstraintViolation<?>> constraintViolations = super.validate( this );

        if (isNotEmpty(dataSourceConfig))
            constraintViolations.addAll( dataSourceConfig.validate() );

        if (isNotEmpty(projectConfig))
            constraintViolations.addAll( projectConfig.validate() );

        return constraintViolations;
    }

}

