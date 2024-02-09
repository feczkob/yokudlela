# README #

Simple Kotlin Springboot (kitchen) and Java Quarkus (daily_menu) backends with JUnit (using Testcontainers) and Cucumber tests.
The daily menu microservice is the work of Károly Szalai. The artifact produced by the `test` directory is used by the other two projects.

### What is this repository for? ###

* Demo

### How to set up? ###

1. run `mvn clean install` in the `test` directory
2. run `mvn clean install` in the `kitchen` directory
3. run `mvn clean install -DskipTests && mvn test` in the `daily_menu` directory