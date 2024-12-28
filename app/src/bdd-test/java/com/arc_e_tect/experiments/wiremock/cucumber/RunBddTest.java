package com.arc_e_tect.experiments.wiremock.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/arc_e_tect/book/sedr")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty," +
        "html:build/reports/bdd-test/Cucumber.html")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @ignore and not @wip")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.arc_e_tect.experiments")
public class RunBddTest {
}
