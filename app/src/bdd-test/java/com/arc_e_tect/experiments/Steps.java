package com.arc_e_tect.experiments;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
    HttpTestClient systemHealthHttpClient;

    @Autowired
    StepData stepData;

    @Autowired
    WireMockServer wireMockServer;

    @PostConstruct
    public void postConstruction() {
        log.debug("Steps was constructed");
        wireMockServer.start();
    }

    @BeforeEach
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
                        .willReturn(okJson("[]"))
        );

        systemHealthHttpClient.executeGet();
        systemIsUpAndRunning = systemHealthHttpClient.getStepData().getHttpStatus().is2xxSuccessful();
        assertTrue(systemIsUpAndRunning);
    }

    @When("the system health information is retrieved")
    public void theSystemHealthInformationIsRetrieved() {
        log.debug("When the system health information is retrieved");

        stubFor(
                get("/health")
                        .withHeader("Accept", WireMock.equalTo("application/json"))
                        .willReturn(okJson("[]"))
        );

        systemHealthHttpClient.getSystemHealth();
        stepData = systemHealthHttpClient.getStepData();
    }

    @Then("the system reports that it is {string}")
    public void theSystemReportsThatItIs(String expectedHealthStatus) {
        JsonNode node = stepData.getResponseJsonNode();
        String healthStatus = node.get("status").asText();
        assertEquals(expectedHealthStatus, healthStatus);
    }
}
