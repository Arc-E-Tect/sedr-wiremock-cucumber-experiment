package com.arc_e_tect.experiments.wiremock.cucumber;

import com.arc_e_tect.experiments.App;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = App.class)
@ActiveProfiles({"bddtest"})
@Import(TestConfig.class)
public class CucumberConfiguration {
}