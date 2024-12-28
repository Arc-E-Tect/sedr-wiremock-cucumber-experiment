package com.arc_e_tect.experiments.web;


import com.arc_e_tect.experiments.web.model.HealthModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/health", produces = "application/json")
public class WebHealthController {

    @GetMapping
    public HealthModel health() {
        log.info("health() called");

        HealthModel response = new HealthModel();
        response.setStatus("up-n-running");

        return response;
    }

}
