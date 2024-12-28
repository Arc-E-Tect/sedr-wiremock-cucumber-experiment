package com.arc_e_tect.experiments.wiremock.cucumber.steps;

import com.arc_e_tect.experiments.wiremock.cucumber.StepData;
import com.arc_e_tect.experiments.wiremock.cucumber.TestConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Import(TestConfig.class)
public class StepsRetrieveSystemHealthInformation {
    boolean systemIsUpAndRunning = false;

    @Autowired
    SystemHealthHttpClient systemHealthHttpClient;

    @Autowired
    StepData stepData;

    @PostConstruct
    public void postConstruction() {
        log.debug("StepsRetrieveSystemHealthInformation was constructed");
    }

    @Given("the system is up and running")
    public void theSystemIsUpAndRunning() {
        systemHealthHttpClient.executeGet();
        systemIsUpAndRunning = systemHealthHttpClient.getStepData().getHttpStatus().is2xxSuccessful();
        assertTrue(systemIsUpAndRunning);
    }

    @When("the system health information is retrieved")
    public void theSystemHealthInformationIsRetrieved() {
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
