package com.arc_e_tect.experiments;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Scope(SCOPE_CUCUMBER_GLUE)
public class Steps {
    boolean systemIsUpAndRunning = false;

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

    @Given("the system is up and running")
    public void theSystemIsUpAndRunning() {
        log.debug("Given the system is up and running");

        stubFor(
                get("/")
                        .withHeader("Accept", WireMock.equalTo("application/json"))
                        .willReturn(okJson(
                                """       
                                        {
                                            "versionName" : "WIREMOCK",
                                            "versionCode" : 1.0
                                        }
                                      """
                        ))
        );

        testClient.executeGet();
        systemIsUpAndRunning = testClient.getStepData().getHttpStatus().is2xxSuccessful();
        assertTrue(systemIsUpAndRunning);
    }

    @When("the system health information is retrieved")
    public void theSystemHealthInformationIsRetrieved() {
        log.debug("When the system health information is retrieved");

        stubFor(
                get("/health")
                        .withHeader("Accept", WireMock.equalTo("application/json"))
                        .willReturn(okJson(
                                """
                                        {
                                          "status" : "up and running"
                                        }
                                      """
                        ))
        );

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
