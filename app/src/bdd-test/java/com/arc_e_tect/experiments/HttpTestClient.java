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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class HttpTestClient {
    @Getter
    protected WebClient apiClient;

    @Getter
    @Value("${server.baseurl}")
    private String baseUrl;

    @Getter
    @Value("${wiremock.server.port}")
    private Integer port;

    @Value("${response.timeout}")
    private Integer responseTimeout;

    @Getter
    protected final StepData stepData;

    @PostConstruct
    public WebClient initApiClient() {
        log.debug("Initializing WebClient");

        log.debug(
                "Base URL: {}, Mock Port: {}, Response Timeout: {}",
                baseUrl,
                port,
                responseTimeout
        );

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, responseTimeout)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(3))
                                .addHandlerLast(new WriteTimeoutHandler(3)));

        apiClient = WebClient.builder()
                .baseUrl(baseUrl + ":" + port)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Arc-E-Tect SEDR IFF")
                .clientConnector(new ReactorClientHttpConnector(httpClient))  // timeout
                .build();

        return apiClient;
    }

    public String apiEndpoint() {
        return baseUrl + ":" + port + getApiEndpoint();
    }

    protected final String getApiEndpoint() {return "/";}

    protected void executeGet() {
        executeGet(apiEndpoint());
    }

    public void getSystemHealth() {
        executeGet(apiEndpoint()+"health");
    }

    public void executeGet(String url) {
        log.debug("GET: {}", url);

        WebClient.ResponseSpec spec = getApiClient().get()
                .uri(url)
                .retrieve();

        ResponseEntity<String> response = spec
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(String.class)
                .block();

        stepData.setResponseEntity(response);

        log.debug("Response: {}", response);
        log.debug("Http Status: {}", getHttpStatus());
        log.debug("ResponseBody: {}", getBody());
    }

    public String getBody() {
        return stepData.getResponseString();
    }

    public HttpStatusCode getHttpStatus() {
        return stepData.getHttpStatus();
    }

    public <T extends RepresentationModel<T>> T getBodyAsResource(T representationModel) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        try {
            return (T) mapper.readValue(getBody(), new TypeReference<T>() {});
        } catch (IOException e) {
            log.info("Error converting response to resource: {}", e.getMessage());
        }
        return null;
    }

}
