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