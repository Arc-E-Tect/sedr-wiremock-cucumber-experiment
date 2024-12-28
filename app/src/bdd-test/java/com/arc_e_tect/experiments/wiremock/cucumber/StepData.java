package com.arc_e_tect.experiments.wiremock.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Slf4j
@Data
@NoArgsConstructor
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class StepData {

    private ResponseEntity<JsonNode> responseEntity;
    private HttpEntity<String> request;
    private HttpStatusCode httpStatus;

    public void setResponseEntity(ResponseEntity<JsonNode> responseEntity) {
        this.responseEntity = responseEntity;
        this.httpStatus = (responseEntity != null ? responseEntity.getStatusCode() : null);
    }
    public JsonNode getResponseJsonNode() {
        return responseEntity.getBody();
    }

    public RepresentationModel<?> getResponseRepresentationModel(RepresentationModel<?> modelRef) {
        try {
            return new ObjectMapper().readValue(getResponseString(), modelRef.getClass());
        } catch (JsonProcessingException e) {
            log.info("Error converting response to resource: {}", e.getMessage());
        }
        return null;
    }

    public String getResponseString() {
        return (responseEntity != null && responseEntity.getBody() != null ? responseEntity.getBody().toString() : "");
    }

}