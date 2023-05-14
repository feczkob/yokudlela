# Cucumber test module
A simple library that defines various operations (step definitions) which can be used in Cucumber tests.

## Technology
- Jackson, JUnit 5, Cucumber, Java HTTP client, BeanUtils

## Requirements
- Add the dependency `com.yokudlela.test` to your project
- Set the system environment variables
- Create and start the test app along with the necessary Docker test containers

### Dependency
Add the dependency of the library to your project.
The library must be built before you can use it.
#### Maven
```
<dependency>
  <groupId>com.yokudlela</groupId>
  <artifactId>test</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
```
#### Gradle
```
testImplementation 'com.yokudlela:test:0.0.1-SNAPSHOT'
```

### Environment variables
The configuration of the library is done via setting the following environment variables:

- Root uri: `root-uri` (e.g. `http://localhost:63453`)
- Keycloak uri: `keycloak-uri` (e.g. `http://localhost:45423/realms/yokudlela/protocol/openid-connect/token`)
- Keycloak client: `keycloak-client` (e.g. `account`)
- Test objects: `test-files-path` (e.g. `src/test/resources/test_objects/`)

#### Spring
Use the `System.setProperty()` function.
Pay attention to do it after the test app has already started, preferably in a method annotated with `@io.cucumber.java.Before`.

### Configuration
#### Spring
Add the following dependencies:
```
<dependencies>
  ...
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>

<dependencyManagement>
  <dependencies>
    ...
    <dependency>
      <groupId>io.cucumber</groupId>
      <artifactId>cucumber-bom</artifactId>
      <version>7.2.3</version>
      <scope>import</scope>
      <type>pom</type>
    </dependency>
  </dependencies>
</dependencyManagement>
```
Configure the test application context for use with Cucumber tests:

- Annotate the class that starts the test app with `@CucumberContextConfiguration`
- Annotate the Cucumber test runner class with `@Suite`, `@SelectClassPathResource`, `@ConfigurationParameter` (and/or create `junit.platform.properties` file in the test resource folder) and `@IncludeEnginges`

#### Quarkus
Add the following dependency:
```
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-cucumber</artifactId>
    <scope>test</scope>
</dependency>
```

Set in the `application.properties`: `quarkus.cucumber.run-mode=client` 

## Step definitions
### REST
#### POST
##### 1. `post {string}`
Invokes HTTP POST at the specified URL.
Saves the response with the default response id that is `def`.
Example:
```
post "/example-endpoint"
"""
{
"example-field": "example-value"
}
"""
```

##### 2. `post {string} into {string}`
Invokes HTTP POST at the specified URL, saves the response with the given response id.
Example:
```
post "/example-endpoint" into "example-response"
"""
{
"example-field": "example-value"
}
"""
```

##### 3. `post {string} with external {string}`
Invokes HTTP POST at the specified URL, the body is read from the given file.
Example:
```
post "/example-endpoint" with external "example-body.json"
```

##### 4. `post {string} with external {string} into {string}`
Invokes HTTP POST at the specified URL, the body is read from the given file, saves the response with the given response id.
Example:
```
post "/example-endpoint" with external "example-body.json" into "example-response"
```

#### GET
##### 1. `get {string}`
Invokes HTTP GET at the specified URL.
Saves the response with the default response id that is `def`.
The id of the object to be retrieved can be obtained using the response id.
Examples:
```
get "/example-endpoint/{id}"
```
Let's assume we already invoked POST, the object is added. The response is stored, so we can use the response id for GET.
In this example the object has a field `id` that is used for the GET endpoint.
```
post "/example-endpoint" into "example-response"
"""
{
"example-field": "example-value"
}
"""
get "/example-endpoint/${response(example-response).id}"
```

##### 2. `get {string} into {string}`
Invokes HTTP GET at the specified URL, saves the response with the given response id.
Example:
```
get "/example-endpoint/{id}" into "example-response"
```

#### PUT
##### 1. `put {string}`
Invokes HTTP PUT at the specified URL.
Saves the response with the default response id that is `def`.
Example:
```
put "/example-endpoint/{id}"
"""
{
"example-field": "example-modified-value"
}
"""
```

##### 2. `put {string} into {string}`
Invokes HTTP PUT at the specified URL, saves the response with the given response id.
Example:
```
post "/example-endpoint/{id}" into "example-response"
"""
{
"example-field": "example-modified-value"
}
"""
```

##### 3. `put {string} with external {string}`
Invokes HTTP PUT at the specified URL, the body is read from the given file.
Example:
```
post "/example-endpoint/{id}" with external "example-body.json"
```

##### 4. `put {string} with external {string} into {string}`
Invokes HTTP PUT at the specified URL, the body is read from the given file, saves the response with the given response id.
Example:
```
post "/example-endpoint/{id}" with external "example-body.json" into "example-modified"
```

#### DELETE
##### 1. `delete {string}`
Invokes HTTP DELETE at the specified URL.
Saves the response with the default response id that is `def`.
Example:
```
delete "/example-endpoint/{id}"
```

##### 2. `delete {string} into {string}`
Invokes HTTP DELETE at the specified URL, saves the response with the given response id.
Example:
```
delete "/example-endpoint/{id}" into "example-deleted"
```


### Additional steps
#### Preparatory
##### 1. `set header param {string} to {string}`
Sets a header key-value pair.
Example:
```
set header "Accept" to "application/json"
```

##### 2. `user {string} identified by {string} is logged in`
Obtains a Keycloak access token using the `keycloak-uri` endpoint and the provided credentials.
Saves the token, sets the Authorization header for the subsequent requests.
Example:
```
user "example-user@example.com" identified by "example-password" is logged in
```

#### Examiner
##### 1. `exists in the response {string}`
Checks if a field is present in the response identified by the default response id (`def`).
Use this only if the response was assigned with the default response id.
Example:
```
exists in the response "example-field"
```

##### 2. `exists in the {string} response {string}`
Checks if a field is present in the response identified by the given parameter.
Use this if the response was assigned a custom response id.
Example:
```
exists in the "example-response" response "example-field"
```

###### 3. `exists in the response {string} with string value {string}`
Checks if the response identified by the default response id (`def`) contains the given field and the value matches the given value.
Example:
```
exists in the response "example-field" with string value "example-value"
```

###### 4. `exists in the {string} response {string} with string value {string}`
Checks if the response identified by the given parameter contains the given field and the value matches the given value.
Example:
```
exists in the "example-response" response "example-field" with string value "example-value"
```

##### 5. `exists in the {string} response`
Checks if the response identified by the given parameter contains the provided object.
Example:
```
exists in the "example-response" response
"""
{
"example-field": "example-value"
}
"""
```

##### 6. `exists in the {string} response with external {string}`
Checks if the response identified by the given parameter contains the provided object given in a file.
Example:
```
exists in the "example-response" response with external "example-body.json"
"""
{
"example-field": "example-value"
}
"""
```

##### 7. `response status {int}`
Checks if the status of the response matches the given parameter.
Example:
```
response status 200
```

## Parse
The library can dynamically resolve certain parameters.
### Response params
In section [GET](#1-get-string) we have already seen the usage of `${response()}`.
The responses obtained by the HTTP client are saved to a map. This map associates response ids and bodies.
The syntax is the following: `${response(response-identifier).field_to_be_obtained}`.

### Random number
We can generate random numbers in a range of `[min, max]`.
To do so, we may use `${random(min, max)}`.

### UUID
The library can generate UUID: `${uuid}`.

### Character
We can replace placeholders in a string with a character from another string with `${char()}`.
Example: `${char(abc, 1)}`.