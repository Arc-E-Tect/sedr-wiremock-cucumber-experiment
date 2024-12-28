package com.arc_e_tect.experiments;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Scope(SCOPE_CUCUMBER_GLUE)
public class Steps {
    boolean systemIsUpAndRunning = false;

    @Autowired
    private ApplicationContext context;

    @Autowired
    HttpTestClient testClient;

    @Autowired
    StepData stepData;

    @Autowired
    WireMockServer wireMockServer;

    @PostConstruct
    public void postConstruction() {
        log.debug("Steps was constructed");
        wireMockServer.start();
    }

    @Before
    public void beforeEach() {
        log.debug("Steps beforeEach");
        wireMockServer.resetAll();
    }

    @Given("the application is deployed")
    public void theApplicationsIsDeployed() {
        log.debug("Given the application is deployed");

        assertNotNull(context);
    }

    @Given("the system is up and running")
    public void theSystemIsUpAndRunning() {
        log.debug("Given the system is up and running");

        log.info("Setting up WireMock stubs to proxy to the real application at {} on port {}",
                testClient.getBaseUrl(),
                testClient.getPort());

//        stubFor(
//                get(urlMatching("/"))
//                        .withHeader("Accept", WireMock.equalTo("application/json"))
//                        .willReturn(aResponse().proxiedFrom(
//                                testClient.getBaseUrl() + ":" + testClient.getPort()))
//        );

        testClient.executeGet();
        systemIsUpAndRunning = testClient.getStepData().getHttpStatus().is2xxSuccessful();
        assertTrue(systemIsUpAndRunning);
    }

    @When("the system health information is retrieved")
    public void theSystemHealthInformationIsRetrieved() {
        log.debug("When the system health information is retrieved");

        testClient.getSystemHealth();
        stepData = testClient.getStepData();
    }

    @Then("the system reports that it is {string}")
    public void theSystemReportsThatItIs(String expectedHealthStatus) {
        JsonNode node = stepData.getResponseJsonNode();
        String healthStatus = node.get("status").asText();
        assertEquals(expectedHealthStatus, healthStatus);
    }
}
