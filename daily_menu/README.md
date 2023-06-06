# dailymenu microservice

Napi menü Quarkus projekt yokudlela3 főprojekt részeként.

Üzleti-technikai leírás:

https://docs.google.com/document/d/1oCqozVdidOphoW2XtchnFszgIFlboYP8O_cOlUbKQL0/edit#

Technológiai stack:

- Quarkus: https://quarkus.io
- RESTEasy JAX-RS: https://quarkus.io/guides/getting-started#the-jax-rs-resources
- SmallRye OpenAPI (OpenAPI - Swagger UI): https://quarkus.io/guides/openapi-swaggerui
- MariaDB JDBC Driver https://quarkus.io/guides/datasource
- Liquibase: https://quarkus.io/guides/liquibase
- Quarkus Extension for Spring Data JPA API https://quarkus.io/guides/spring-data-jpa

## Alkalmazásfuttatás fejlesztői módban:


```shell script
./mvnw compile quarkus:dev
```

Dev UI elérése dev mode-ban: http://localhost:8080/q/dev/

## Alkalmazáscsomag készítés

```shell script
./mvnw package
```

Eredménye egy `quarkus-run.jar` fájl, ami a `target/quarkus-app/` directoryban található.
ezen belül a függőségek itt találhatók: `target/quarkus-app/lib/`

Az így készült alkalmazása futtatása: `java -jar target/quarkus-app/quarkus-run.jar`

Teljes alkalmazáscsomag készítése:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

Eredménye egy _über-jar_ utótagú fájl, amely az alábbi módon futtatható `java -jar target/*-runner.jar`

## Natív futtatható állomány készítése

 
```shell script
./mvnw package -Pnative
```

Illetve ha nem rendelkezel telepített GraalVM-mel, natív futtatható Docker-konténert is kreálhatsz:: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

végeredménye: `./target/dailymenu-1.0.0-SNAPSHOT-runner`




