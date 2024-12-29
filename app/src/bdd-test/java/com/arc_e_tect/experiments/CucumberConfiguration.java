package com.arc_e_tect.experiments;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = App.class)
@ActiveProfiles({"bddtest"})
@Import(WireMockConfig.class)
public class CucumberConfiguration {
    WireMockServer wireMockServer;

    protected CucumberConfiguration(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
        wireMockServer.start();
    }

}