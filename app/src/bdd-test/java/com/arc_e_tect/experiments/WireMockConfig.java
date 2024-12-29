package com.arc_e_tect.experiments;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Slf4j
@TestConfiguration
public class WireMockConfig {
    @Value("${wiremock.server.port:8080}")
    private Integer wireMockPort;

    @Value("${wiremock.server.host:127.0.0.1}")
    private String wireMockHost;

    public void WireMockConfig() {
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
