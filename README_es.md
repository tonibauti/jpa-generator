# JPA Generator

[ [English](./README.md) ]

JPA Generator es un generador de código java que analiza la estructura de una base de datos (a partir de una conexión jdbc), 
escanea su tablas, relaciones, claves primarias, claves foráneas, índices, etc.
y genera la capa de persistencia JPA que incluye:

- Configuración
- Entidades
- Relaciones
- Repositorios (tipo spring data o tipo sql nativo)
- Tests de los Respositorios

(Los tipos de datos "json" y "uuid" se han probado en bases de datos MySQL y PostgreSQL)

Actualmente, los proveedores soportados son los siguientes:

| Componente | Proveedor |
|------------|-----------|
| datasource | hikari    |
| jpa        | hibernate |
| proyecto   | spring    | 

### Forma de uso:

```
java -jar jpa-generator.jar -h

java -jar jpa-generator.jar -f myconfig.yml

java -jar jpa-generator.jar -f myconfig.yml -e myconfig.env
```

#### Ejemplo de fichero de configuración:

```yaml
version: "1.0"
#version: {{ version }}   # myconfig.env --> version = 1.0

generator:
  generate-config: true
  generate-mode: "spring-data"  # spring-data | native-sql
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
    type: "spring3"  # spring | spring3
    path: "/myprojects/example"
    config-package: "org.myorganization.example.config.database"
    persistence-package: "org.myorganization.example.persistence"
    persistence-test-package: "org.myorganization.example.test.persistence"
    #encoder: "org.myorganization.example.utils.encoder.XOREncoder"
    use-builders: true
    use-timestamp-like-date: true
    use-varchar36-like-uuid: true    # "generador uuid" cuando no se soporta el tipo uuid

    tables:
      includes:
        - "*"
      excludes:
        - ""

    columns:      # formato: tabla.columna
      includes:
        - "*"
      excludes:
        - ""
      encoded:
        - ""
      invisible:  # no se muestra en los logs
        - "*.password"
```

---

#### Configuración de la aplicación:

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

#### Dependencias del proyecto:

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
  
    <!-- Spring Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

