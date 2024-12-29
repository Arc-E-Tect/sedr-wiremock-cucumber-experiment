# WireMock, Cucumber, and Spring Boot
## Arc-E-Tect's Software Engineering Done Right definitive guide

In today’s API driven world, it is critical to guarantee the users of your APIs an experience without frustrations as they consume those APIs. For this reason, experienced software engineers apply concepts as Test-Driven Development, TDD and Behavior Driven Development while delivering high quality APIs. Tools like WireMock and Cucumber are indispensable, and frameworks like Spring Boot are key to reduce the cost of delivering software-based products.
Although there are plenty of resources available online that explain how to use WireMock and Spring Boot together or how to use Cucumber and Spring Boot together, there are only few resources to be found that show exactly how to use all three of them in your software development process.
This article will explain how you can configure WireMock and Cucumber to develop your APIs in a truly API-First fashion while applying Test-Driven Development and Behavior-Driven Development practices to guarantee that your APIs are useful, usable, and according to specifications.
Before I go into details on how these three excellent tools and technologies can be configured to get a sum far greater than their individual parts, I will give an explanation on the different concepts that this trio supports. Although I do elaborate on the concepts covered in this article, I do assume that you are already familiar with the tools and technologies mentioned in the article’s title, WireMock, Cucumber and Spring Boot.

## The concepts

The concepts discussed in this article as well as the tools and technologies covered in this article are in great detail discussed in my book \[Software Engineering Done Right – the practice of delivering useful and usable software\]. You can request access to the electronic version of the book by becoming one of the first members of [Software Engineering done Right on discord](https://discord.gg/HcH7YzTSGH).

### Test-Driven Development

Test-Driven Development or TDD, is the practice of developing software by first developing a test to validate the correctness of the software and second to develop the software itself. Thus the software is guaranteed to be testable and tested. 

Besides the benefit of having automated tests for practically every feature you develop, it also adds the benefit of having clarity on the requirements related to the feature. Unless the requirements are clear, it is impossible to develop or even define the tests that will validate that the software meets all requirements.  
It is also more cost efficient to first get clarity on the requirements and then develop the software that meets it, then to first develop the software and get clarity along the way as that would most likely require rework because of gained insights. In addition, the requirements engineers will be more prone to have a discussion during or shortly after they document the requirements, then days, weeks, or even months after they completed the requirement definition and have moved on to the next assignment. This reduced cycletime related to requirements definition supports business agility significantly.

I have written about the concept of TDD before, and you can read the first of three articles here: [Test-Driven Development for sceptics](https://medium.com/geekculture/test-driven-development-for-sceptics-1-3-122c22e442c1?sk=60f9e9776ac40b9b6978428d8c638b56).  
In this series of three articles I debunk the most common misconceptions around TDD and explain through logic and experience why TDD reduces the cost of and time needed to deliver production-grade software.

### Behavior-Driven Development

Behavior-Driven Development, or BDD, is the practice of defining requirements in terms of features of a product, how these features are expected to be used, and consequently how the software behaves in those scenarios.  
This approach of defining requirements puts the focus on actual features of a product, therefore how useful a product is. Because BDD describes the intended or expected use of a feature in certain scenarios, BDD is also addressing the usability of the features. BDD is therefore an excellent way to ensure that your product is both useful and usable.  
Because APIs can be considered features of an application, BDD is very suitable to define APIs and how they are expected to be used by an API consumer to accomplish specific tasks. By applying BDD practices and principles, an APIs usefulness and usability become clear even during the requirements definition phase even before the API is defined and most definitely before the API is implemented.

An additional benefit of BDD is that it can result in automated tests that validate the correct implementation of a feature when used in specific scenarios. Tools like Cucumber, covered in this article, will take as input the feature definition files written in Gherkin to run automated tests. When you define the features of your product as APIs and you apply API-First principles (see the next section) then you will want to be able to mock your API implementations before implementing your API for real, thus test your API’s usability and have the opportunity to refine the API before spending all that time on its implementation.

I have written various articles on topic of BDD, among them is this article: [Benefits of Being a Software Vegetarian](https://medium.com/codex/software-engineering-done-right-ad1f194a5f3b?sk=988d7c7b1433efa70477efe21e6d5f75) which explains the concept within the context of developing an API-only application.

### API-First

API-First and API-only are two sides of the same coin. I have written this article on topic of API-only: [API dvelopment through behavior](https://medium.com/codex/api-development-through-behavior-4db409612acf?sk=e71e2edb50b703e8695bcfee85391b74).  
That article explains how thinking of an application that exposes its features as APIs as an API-only application improves its usability. Within this context, API-First implies that there is something following, which is detrimental to the quality of the API.

An alternative way of thinking about API-First, which is also a more mature view on APIs in my opinion, is to consider an API and its implementation as two sides of the same coin. One side being the API which is the interface or contract and the other side being the implementation of the interface. Within this context, API-First means that first you define the contract, the interface, and second you develop the implementation that respects that contract.  
Because API-First means that the API is defined independent and before its implementation, it also means that the API’s definition will not have a bias towards a specific implementation. It will therefore be more based on how the API consumer will use the API, and in general resulting in a more usable API.
The technologies

## Applying WireMock, Cucumber to develop Spring Boot applications

TDD, BDD, and API-First are concepts critical to the success of delivering useful and usable software. To enable you developing useful and usable software by applying these three concepts, you are likely to use WireMock, Cucumber, and Spring Boot. Unfortunately, these three tools, although very common in the world of software engineering, don’t work together as a trio as seamless as one would expect.  
The project is intended to demonstrate the use of WireMock in combination with Cucumber and Spring Boot.
These individual components will not be covered as I will focus solely on the integration of all three components into a single application exposing APIs.
As mentioned before, there are plenty resources available online that will explain how to use the individual components as well as how to use WireMock in your Spring Boot application or how to use Cucumber in your Spring Boot application. At the time of this writing though, I have not encountered a single online resource that explains how to use WireMock in your Spring Boot application during your BDD testing using Cucumber. I tried several Gen AI tools to give me the information needed, none of them were able to understand the intricacies of these three tools and why they don’t work together as one would expect or hope. This has motivated me to write this article, as it describes just that.

## Tooling

Besides WireMock, Cucumber, and Spring Boot, I have used Java 17, Amazon Corretto’s implementation, Gradle, and IntelliJ Idea CE. I will try to keep all dependencies, including tooling as up to date as possible, ensuring that you will run the smallest risk as possible of being impacted by vulnerabilities. Still, whenever you decide to clone the repository and run the application.

## The project

The application you will find in the afore mentioned GitHub repository exposes two APIs. In fact, the application is in its very early stages and delivers two APIs that are as simple as “Hello World” but a bit more relevant. The APIs are described in the OpenAPI document named `openapi.json` located in the directory `app/src/main/resources`. I am using the OpenAPI 3.1.1 specification.  
Although I’m not using this OpenAPI description, it is relevant because the Cucumber feature files as well as the WireMock stubs are based on this OpenAPI description. Especially the example values defined in the OpenAPI description are of relevance.

I am covering API-First and how to apply it in developing applications that expose APIs in my book “Software Engineering Done Right – the practice of delivering useful and usable software”. You can request access to the electronic version of the book through my discord server: [Software Engineering done Right](https://discord.gg/HcH7YzTSGH).

### WireMock and Spring Boot

WireMock has native support for Spring Boot, or maybe it is more accurate to say that Spring Boot has native support for WireMock. Either way, there is a WireMock library available that allows you to use WireMock in your Spring Boot applications to stub external APIs.

The intention of this library is to make you less dependent on APIs delivered by services that are not delivered by you. In a microservice architecture, this could be microservices developed by other teams, or possibly microservices that are not available in your development environment.

I wrote an article on the importance of ensuring your software, especially when developing microservices, is deployable. When you have to deal with microservices that are not available in your development environment, share this article with your colleagues and point out to them that they are costing your organization millions of €s because they either didn’t read the article or didn’t take it to heart. [The forgotten abilities—Deployability](https://medium.com/geekculture/the-forgotten-abilities-deployability-634819e83428?sk=532f77c18a6b4200bc6f76f89c5e68fd).

By using WireMock, you can replace every API call to the external service with a WireMock stub, thus simulating the external service’s existence. Of course, you’ll need to have an accurate description of the external API for this to work.  
WireMock integration with Spring Boot is primarily intended to test your application. It is superb in that it provides programmatic access to WireMock, so you can configure it dynamically as per the specific cases required by your application. For this purpose, I would recommend against using WireMock in a standalone configuration, for example, containerized or as a separate Java application, as that would be less flexible and definitely harder to share in a shared environment like a UAT setup.

You can find the WireMock integration libraries for Spring Boot here: [org.wiremock.integrations](https://mvnrepository.com/artifact/org.wiremock.integrations)

### Cucumber and Spring Boot

Cucumber has native support for Spring Boot, or maybe it is more accurate to say that Spring Boot has native support for Cucumber. Either way, there is a Cucumber library available that allows you to use Cucumber in your Spring Boot applications to test your Spring Boot application’s behavior as described in Cucumber feature files.

The intention of this library is to use Cucumber as a jUnit Jupiter platform runner for SpringBootTest annotated tests. It will replace the standard jUnit runners with the Cucumber runner and will give you access to the Cucumber ecosystem, which is vast. This integration also allows the Spring Boot application context to be injected into your glue-code and therefore make your steps more versatile.
The library will allow you to test the behavior of your Spring Boot application based on the Cucumber feature files. So, like the WireMock integration library for Spring Boot, this library is also intended to test your Spring Boot application. The Cucumber integration is also superb in that it gives programmatic access to develop the most effective and efficient tests.

You can find the Cucumber integration libraries for Spring Boot here: [io.cucumber/cucumber-spring](https://mvnrepository.com/artifact/io.cucumber/cucumber-spring).

### WireMock and Cucumber for API-First

As explained above, both tools have integration libraries for Spring Boot to test your Spring Boot application. But what if you don’t yet have an application to test?

This may sound like a silly question when you’re not using an API-First approach in developing APIs. But when you do develop your products API-First, and I don’t mean the misnomer for API-only, but the true API-First approach where you first describe a feature, next the API that provides access to the feature, validate that it is useful and usable, before implementing it. This to ensure that you’re not wasting your precious time on developing something nobody will ever want to use.

When you are applying API-First, you will want a way to ensure that the feature you defined using a Cucumber feature file, when exposed through an API described in an OpenAPI document is useful and usable. For this purpose, WireMock is excellent, as it allows you to create mock implementations of APIs. It will allow you to define a stub for every API your application will expose, eventually, even before it is implemented. This way, you can consider the WireMock stub as the API’s first implementation, which comes at an extremely low cost.  
You will still want to have a Spring Boot application, in its most basic incarnation, available because you will want to have some way to gradually implement that application’s feature set. You will want to incrementally add its APIs, iteratively, one API at a time. Because you will have WireMock stubs for all APIs not yet implemented, you can have a complete application available from day one that is fully functional without an API exposed by it and have it expose all APIs on the final day and in between one or more APIs are ‘implemented’ by WireMock stubs.

The combination of WireMock and Cucumber is therefore not intended to test your application, but to validate the quality of your APIs in terms of usefulness and usability, even before they are implemented. Neither the WireMock integration libraries with Spring Boot nor the Cucumber integration libraries with Spring Boot cater for this intention.

## Getting it all to work

I will not go into details what is not working when you try to use the official ways to integrate WireMock and Spring Boot or Cucumber and WireMock or all three together. I will also not explain why this is not working. There is a discussion in the WireMock GitHub project on using WireMock in Cucumber glue-code to test Spring Boot applications using the WireMock integration libraries and why it doesn’t work, in case you’re interested. See [wiremock-spring-boot with a BDD Cucumber step definition](https://github.com/wiremock/wiremock-spring-boot/discussions/22).

### Project structure

The example project on GitHub that has all source code for this article focusses on how WireMock can be used to provide stubs for APIs that are tested using Cucumber. The intention was not to write picture perfect source code, although I did use common sense and did do some refactoring once I got everything working to tidy up the code.

The approach demonstrated in this project will allow you to apply API-First by first defining your Cucumber feature files with the scenarios in which a feature is expected to be used.  
Next, you can implement the relevant glue-code, using WireMock to provide the stubs for the API endpoints that are called by the application under test before they’re implemented for real. This will allow you to test your APIs and how they can be used to expose your applications features before spending time on implementing them.
I used a feature of Gradle to place all BDD tests in their own directory called `bdd-test`, which falls under `app/src/`.

The WireMock configuration is also kept in its own subdirectory, `wiremock`, which falls directly under `app/`.
 This allows WireMock stubs to be defined as separate JSON files instead of being implemented programmatically as part of the Cucumber steps. Because these files are not in the `app/src/` subtree, they can be easily shared across filesets. It would allow the same stub definitions to be used for BDD tests, unit tests, integration tests, etc. Alternatively, I could have placed them in the `resources` directory of the `bdd-test` subdirectory, but that would have implied that the stubs are specific for the BDD tests, and that was not my intention.

By putting the stub definitions outside of the `app/src/` subtree, the stubs are easier to maintain  as they are explicitly independent of the glue-code. They can be defined and maintained by a different team and be based on the OpenAPI description of the endpoint.

### WireMock configuration

I have made sure that WireMock is configured to run on a different port than the standard port `8080` because that is also the standard port for servlet containers like Apache Tomcat.

In the set up found on GitHub, you will find that I’ve already implemented one of the two APIs and therefore use WireMock’s proxy-feature. This shows how to configure WireMock to gradually replace stubs with the actual implementations, facilitating true agile software delivery based on increments and iterations.  
Although the annotations introduced by WireMock’s native support for Spring Boot won’t work with Cucumber, the dependency on WireMock is still required and should therefore be based on `“org.wiremock.integrations:wiremock-spring-boot”` library instead of the regular `“org.wiremock:wiremock”` library. With the latter I had problems sharing WireMock definitions across different classes in my Cucumber glue-code.  
Switching to the former library solved all issues. This was pretty unintuitive, since the WireMock Spring Boot integration using the @InjectWireMock annotation, which is the advertised way of injecting WireMock objects doesn’t work in conjunction with Cucumber. One would think that the Spring Boot integration would not be needed and the standard WireMock library would suffice, but that turned out to be a wrong assumption.

I’m using Gradle’s version catalog way of defining dependencies as it gives more flexibility on managing dependency versions.

As mentioned, the BDD part of this application is located in the `app/src/bdd-test` directory and the WireMock configuration is located in the `app/wiremock` directory.

## The source code

The WireMock server is created in the `WireMockConfig` class, which is also used to contain some key WireMock configuration settings:
* The port on which WireMock is running, default is `8080` but it is configured using the `application.propperties` file to run on `9090`.
* The host for which WireMock is running, defaults is `127.0.0.1` and it is configured using the `application.propperties` file to run on `localhost`.
 
```java
@TestConfiguration
public class WireMockConfig {
    @Value(“${wiremock.server.port:8080}”)
    private Integer wireMockPort;
    
    @Value(“${wiremock.server.host:127.0.0.1}”)
    private String wireMockHost;

    @PostConstruct
    public void initWireMockConfig() {
        log.debug(“Initializing WireMock Client with host: {} and port: {}”, wireMockHost, wireMockPort);
        WireMock.configureFor(wireMockHost, wireMockPort);
    }
    
    @Bean
    public WireMockServer wireMockServer() {
        log.debug(“Initializing WireMock Server with host: {} and port: {}”, wireMockHost, wireMockPort);
        return new WireMockServer(options()
                .port(wireMockPort)
                .bindAddress(wireMockHost)
                .usingFilesUnderDirectory(“wiremock”)
        );
    }
}
```

As the `WireMockConfig` is constructed, the WireMock client is configured as well.
I prefer to handle this as soon as possible, but it is required that this is handled before the WireMockServer is created.

The WireMock server is created in the `wireMockServer` method, which is annotated with `@Bean` so it can be injected whenever needed.

See below for how this is done in the `CucumberConfiguration` and the `Steps` class.
The construction of the WireMockServer is done using the `options()` method, which allows you to set the port and the host on which the WireMock server is running as well as the location where the stubs are located.
Note that `.usingFilesUnderDirectory(“wiremock”)` points to the location where the mappings and files are located and, it is relative to the project’s base directory.

Important to realize is that the files directory must be named `__files`, so it will be `app/wiremock/__files`.
Response body definitions can be stored in this directory and referenced from the stubs that are stored in the `app/wiremock/mappings` directory.

The WireMockServer is injected using `@Autowired` into the `CucumberConfiguration` and the `Steps` class, which is the glue-code that connects the Cucumber feature files with the actual implementation of the steps.

```java
public class CucumberConfiguration {
    WireMockServer wireMockServer;

    protected CucumberConfiguration(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
        wireMockServer.start();
    }
}
```

The WireMockServer is used to start the WireMock server once the Cucumber context is created and the Runner started.

```java
@Slf4j
@RequiredArgsConstructor
@Scope(SCOPE_CUCUMBER_GLUE)
public class Steps {
    boolean systemIsUpAndRunning = false;

    private final ApplicationContext context;

    private final HttpTestClient testClient;

    private final WireMockServer wireMockServer;

    @Before
    public void beforeEach() {
        log.debug("Steps beforeEach");
        wireMockServer.resetAll();
    }
…
}
```
The wireMockServer is used to reset the server before each test.
The `WireMockConfig` class will also configure the WireMock client which will be used to interact with the WireMock server in the `CucumberConfiguration` and the `Steps` class.

```java
    @Given(“the system is up and running”)
    public void theSystemIsUpAndRunning() {
        stubFor(
                get(urlMatching(“/”))
                .withHeader(“Accept”, WireMock.equalTo(“application/json”))
                .willReturn(aResponse().proxiedFrom(
                        testClient.getBaseUrl() + “:” + testClient.getPort()))
        );
        
        testClient.executeGet();
        systemIsUpAndRunning = testClient.getStepData().getHttpStatus().is2xxSuccessful();
        assertTrue(systemIsUpAndRunning);
 }
```

Note that the code is commented out because the stub that is created is actually defined as a JSON file in the `app/wiremock` directory.

```json
{
  “request”: {
    “method”: “GET”,
    “url”: “/”,
    “headers”: {
    “Accept”: {
    “equalTo”: “application/json”
    }
    }
  }
},
  “response”: {
    “proxyBaseUrl”: “http://localhost:9090”
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

