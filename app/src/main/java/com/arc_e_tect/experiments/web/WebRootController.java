package com.arc_e_tect.experiments.web;


import com.arc_e_tect.experiments.web.model.VersionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/", produces = "application/json")
public class WebRootController {

    @GetMapping
    public VersionModel index() {
        log.info("index() called");

        VersionModel response = new VersionModel();
        response.setVersionName("REAL SERVICE");
        response.setVersionCode(1.0);

        return response;
    }

}
