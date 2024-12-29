/*
 * Copyright (c) 2024.
 *
 * MIT License
 *
 * © 2024 Iwan Eising - Arc-E-Tect
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.arc_e_tect.experiments;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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
