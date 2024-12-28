package com.arc_e_tect.experiments;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@Slf4j
@TestConfiguration
public class WireMockConfig {

    @Bean
    public WireMockServer wireMockServer() {
        log.debug("Initializing WireMockServer with default values.");

        WireMockServer wireMockServer = new WireMockServer();

        return wireMockServer;
    }

}
