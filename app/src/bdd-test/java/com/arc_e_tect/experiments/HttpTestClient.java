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
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
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

    @Value("${wiremock.server.port}")
    private Integer mockPort;

    @Value("${response.timeout}")
    private Integer responseTimeout;

    @Getter
    protected final StepData stepData;

    @Getter
    @Value("${local.server.port}")
    protected int port;

    @PostConstruct
    public WebClient initApiClient() {
        log.debug("Initializing WebClient");

        log.debug(
                "Base URL: {}, Mock Port: {}, Response Timeout: {}",
                baseUrl,
                mockPort,
                responseTimeout
        );
        log.debug("Local Server Port: {}", port);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, responseTimeout)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(3))
                                .addHandlerLast(new WriteTimeoutHandler(3)));

        apiClient = WebClient.builder()
                .baseUrl(baseUrl + ":" + mockPort)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Arc-E-Tect SEDR IFF")
                .clientConnector(new ReactorClientHttpConnector(httpClient))  // timeout
                .build();

        return apiClient;
    }

    public String apiEndpoint() {
        return baseUrl + ":" + mockPort + getApiEndpoint();
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

        ResponseEntity<JsonNode> response = spec
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(JsonNode.class)
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
        mapper.registerModule(new Jackson2HalModule());

        try {
            return (T) mapper.readValue(getBody(), new TypeReference<T>() {});
        } catch (IOException e) {
            log.info("Error converting response to resource: {}", e.getMessage());
        }
        return null;
    }

}
