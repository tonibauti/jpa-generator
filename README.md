# JPA Generator

[ [Español](./README_es.md) ]

JPA Generator is a java code generator that analyses a database structure (provided with a jdbc connection), 
scans its tables, relationships, primary keys, foreign keys, indexes, etc. 
and generates the JPA persistence layer which includes:

- Configuration
- Entities
- Relationships
- Repositories (spring data type or native sql type)
- Repositories Tests

Currently, the following providers are supported:

| Component  | Provider   |
|------------|------------|
| datasource | hikari     |
| jpa        | hibernate  |
| project    | spring     | 

### How to use:

```
java -jar jpa-generator.jar -h

java -jar jpa-generator.jar -f myconfig.yml

java -jar jpa-generator.jar -f myconfig.yml -e myconfig.env 
```

#### Configuration file example:

```yaml
version: "1.0"
#version: {{ version }}   # myconfig.env --> version = 1.0

generator:
  generate-config: true
  generate-mode: "native-sql"  # spring-data | native-sql
  generate-entities: true
  generate-joins: true
  generate-crud-repositories: true
  generate-crud-repositories-test: true
  
  datasource:
    provider: "hikari"
    name: "example"
    driver-jar-and-dependencies:
      - "./libs/mysql/mysql-connector-java-8.0.23.jar"
    driver-class-name: "com.mysql.cj.jdbc.Driver"
    jdbc-url: "jdbc:mysql://localhost:3306/example"
    username: "example"
    password: "example"
    properties:
      autoCommit: false
      readOnly: false
      minimumIdle: 2
      maximumPoolSize: 4
      connectionTimeout: 30000
      idleTimeout: 600000
      maxLifetime: 1800000
      dataSourceProperties.cachePrepStmts: true
      dataSourceProperties.prepStmtCacheSize: 250
      dataSourceProperties.prepStmtCacheSqlLimit: 2048

  jpa:
    provider: "hibernate"
    properties:
      open-in-view: false
      hibernate.format_sql: true
      hibernate.show_sql: true
      hibernate.use_sql_comments: false
      hibernate.connection.charSet: "UTF-8"
      hibernate.hbm2ddl.delimiter: ";"
      hibernate.dialect: "org.hibernate.dialect.MySQL57Dialect"
      #javax.persistence.schema-generation.create-source: "metadata"
      #javax.persistence.schema-generation.scripts.action: "create"
      #javax.persistence.schema-generation.scripts.create-target: "example.sql"

  project:
    type: "spring"
    path: "/myprojects/example"
    config-package: "org.myorganization.example.config.database"
    persistence-package: "org.myorganization.example.persistence"
    persistence-test-package: "org.myorganization.example.test.persistence"
    #encoder: "org.myorganization.example.utils.encoder.XOREncoder"
    use-builders: true
    use-timestamps-like-dates: true

    tables:
      includes:
        - "*"
      excludes:
        - ""

    columns:    # format: table.column
      includes:
        - "*"
      excludes:
        - ""
      encoded:
        - ""
      invisible:
        - "*.password"
```

---

#### Application config:

```java
@SpringBootApplication
@EnableAutoConfiguration(exclude =
{
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
})
@EnableTransactionManagement
@EnableConfigurationProperties
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }   
}
```

---

#### Project dependencies:

```xml
<dependencies>
    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Spring JDBC -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    
    <!-- Spring JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.22</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

