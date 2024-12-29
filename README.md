# Software Engineering Done Right - Source Code

## Experiment - WireMock, Cucumber, and Spring Boot

This project is intended to demonstrate the use of WireMock in combination with Cucumber and Spring Boot.

This document assumes that you're already familiar with WireMock, Cucumber, and Spring Boot.
These individual components will not be covered as I will focus solely on the integration of these components.

The relevance of such a project lies in the fact that Cucumber uses its own Runner, which renders the common mechanisms for using WireMock in Spring Boot applications unusable. This project demonstrates how to use WireMock in combination with Cucumber and Spring Boot.
Spring Boot's integration of WireMock, which is based on the @InjectWireMock annotation, can’t be used because the inject WireMockServer instance can’t be injected in Cucumber steps using the @Autowired annotation, for example.

The project is set up such that it facilitates a simple Spring Boot application which exposes a REST endpoint.
The focus of this project has not been on delivering a perfect web application but to show how WireMock can be used to provide stubs for REST endpoints that are tested using Cucumber.
This is useful when you want to apply TDD concepts while applying BDD principles.
The approach demonstrated in this project will allow you to first define your Cucumber feature files with the scenarios in which a feature is expected to be used.
Next, you can implement the relevant glue-code, using WireMock to provide the stubs for the REST endpoints that are called by the application under test before they’re implemented for real.

The directory structure is such that the BDD tests are kept in their own subdirectory, `bdd-test`.
The WireMock configuration is also kept in its own subdirectory, `wiremock`.

This allows WireMock stubs to be defined as separate JSON files instead of being implemented programmatically as part of the Cucumber steps.
This provides an arguably better maintainable setup, or at least, allows you to develop the stubs independent of the glue-code, possibly by a different team and based on an OpenAPI description of the endpoint.

The project also demonstrates how to set up WireMock running on a different port, and how to configure its proxy-feature, such that it is possible to gradually replace the stubs with the actual implementation, thus facilitating true agile software delivery based on increments and iterations.

The project is using Gradle as its build system.

### Puzzel pieces

One key requirement for the integration of WireMock, Cucumber, and Spring Boot to work is related the dependency on WireMock.
Although the annotations introduced by WireMock's native support for Spring Boot won't work with Cucumber, the dependency on WireMock is still required
and should therefore be based on `wiremock-spring-boot = "org.wiremock.integrations:wiremock-spring-boot:3.4.0"` instead of the regular `wiremock = "org.wiremock:wiremock:3.10.0"`.
By the way, the project uses Gradle's version catalog method to manage the versions of the dependencies.

As mentioned, the BDD part of this application is located in the `app/src/bdd-test` directory and the WireMock configuration is located in the `app/wiremock` directory.

The WireMock server is created in the `WireMockConfig` class, which is also used to contain some key WireMock configuration settings:
* The port on which WireMock is running, default is `8080` but it is configured using the `application.propperties` file to run on `9090`
* The host for which WireMock is running, defaults is `127.0.0.1` and it is configured using the `application.propperties` file to run on `localhost`

```java
@TestConfiguration
public class WireMockConfig {
    @Value("${wiremock.server.port:8080}")
    private Integer wireMockPort;

    @Value("${wiremock.server.host:127.0.0.1}")
    private String wireMockHost;

    @PostConstruct
    public void initWireMockConfig() {
        log.debug("Initializing WireMock Client with host: {} and port: {}", wireMockHost, wireMockPort);

        WireMock.configureFor(wireMockHost, wireMockPort);
    }

    @Bean
    public WireMockServer wireMockServer() {
        log.debug("Initializing WireMock Server with host: {} and port: {}", wireMockHost, wireMockPort);

        return new WireMockServer(options()
                .port(wireMockPort)
                .bindAddress(wireMockHost)
                .usingFilesUnderDirectory("wiremock")
        );
    }

}
```
After the `WireMockConfig` is constructed, the WireMock client is configured this is handled by the `@PostContruct` annotated method `initWireMockConfig`.
I prefer to handle this as soon as possible, but it is required that the class is constructed and all auto-wired dependencies are injected first.

The WireMock server is created in the `wireMockServer` method, which is annotated with `@Bean` so it can be injected whenever it is needed.
See below for how this is done in the `CucumberConfiguration` and the `Steps` class.
The construction of the WireMockServer is done using the `options()` method, which allows you to set the port and the host on which the WireMock server is running as well as the location where the stubs are located.
Note that `.usingFilesUnderDirectory("wiremock")` points to the location where the mappings and files are located and, it is relative to the project's base directory.
Important to realize is that the files directory must be named `__files`, so it will be `app/wiremock/__files`.
Response body definitions can be stored in this directory and referenced from the stubs that are stored in the `app/wiremock/mappings` directory.

The WireMockServer is injected using `@Autowired` into the `CucumberConfiguration` and the `Steps` class, which is the glue-code that connects the Cucumber feature files with the actual implementation of the steps.

```java
public class CucumberConfiguration {
    @Autowired
    WireMockServer wireMockServer;

    @PostConstruct
    public void postConstruction() {
        log.debug("Steps was constructed");
        wireMockServer.start();
    }
}
```
The WireMockServer is used to start the WireMock server once the tests are started.

```java
public class Steps {
    ...

    @Autowired
    WireMockServer wireMockServer;

    @Before
    public void beforeEach() {
        log.debug("Steps beforeEach");
        wireMockServer.resetAll();
    }
}
```
The WireMockServer is used to reset the server before each test.

The `WireMockConfig` class will also configure the WireMock client which will be used to interact with the WireMock server in the `CucumberConfiguration` and the `Steps` class.

```java
    @Given("the system is up and running")
    public void theSystemIsUpAndRunning() {
        stubFor(
                get(urlMatching("/"))
                        .withHeader("Accept", WireMock.equalTo("application/json"))
                        .willReturn(aResponse().proxiedFrom(
                                testClient.getBaseUrl() + ":" + testClient.getPort()))
        );

        testClient.executeGet();
        systemIsUpAndRunning = testClient.getStepData().getHttpStatus().is2xxSuccessful();
        assertTrue(systemIsUpAndRunning);
    }
```

Note that the code is commented out, because the stub that is created is actually defined as a JSON file in the `app/wiremock` directory.

```json
{
  "request": {
    "method": "GET",
    "url": "/",
    "headers": {
      "Accept": {
        "equalTo": "application/json"
      }
    }
  },
  "response": {
    "proxyBaseUrl": "http://localhost:9090"
  }
}
```

### Building the application

#### Prerequisites

To run build and run the application, it is required to have [Java 17](https://jdk.java.net/17/) installed.
The build is tested with [Amazon Corretto 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html).

#### Build commands

To build the application and run all tests, execute from the commandline: 
```console
./gradlew build
```

To perform a clean build, execute from the commandline: 
```console
./gradlew clean build
```
To run the application, execute from the commandline:
```console
.\gradlew bootRun
```

Verify that the application is running by navigating to [http://localhost:9090](http://localhost:9090) in a web browser.

To update all dependencies, execute from the commandline:
```console
./gradlew refreshVersions
```
After running the command, uncomment the lines with the latest version numbers in the files `versions.properties` and `gradle/lib.versions.toml`

After adding new dependencies, execute from the commandline:
```console
./gradlew refreshVersionsMigrate --mode=VersionsPropertiesAndPlaceholdersInCatalog
```
Followed by the commandline:
```console
./gradlew refreshVersions
```
After running the command, uncomment the lines with the latest version numbers in the files `versions.properties` and `gradle/lib.versions.toml`

