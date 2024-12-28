# Software Engineering Done Right - Source Code

## Experiment - WireMock Cucumber

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

