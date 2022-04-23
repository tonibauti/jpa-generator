# JPA Generator

[[English](./README.md)]

Es un generador de código java, que a partir de una conexión jdbc a una base de datos, 
escanea su tablas, relaciones, claves primarias, claves foráneas, índices, etc.
y crea la capa de persistencia en JPA.

Se genera lo siguiente:

- Configuración
- Entidades
- Relaciones
- Repositorios (tipo spring data y tipo sql nativo)
- Tests

Actualmente, los proveedores soportados son los siguientes:

- Hikari (para el datasource)
- Hibernate (para el jpa)
- Spring (para el tipo de proyecto)

Forma de uso:

```
java -jar jpa-generator.jar -h

java -jar jpa-generator.jar -f myconfig.yml
```

Ejemplo de fichero de configuración:

```Yaml
version: "1.0"

generator:
  generate-config: true
  generate-entities: true
  generate-crud-repositories: true
  generate-crud-repositories-test: true
  generate-crud-native-repositories: false
  generate-crud-native-repositories-test: false
  generate-joins: true

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
    path: "/tmp/jpa-generator/example"
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

    columns:    # formato: tabla.columna
      includes:
        - "*"
      excludes:
        - ""
      encoded:
        - ""
      invisible:
        - "*.password"
```
