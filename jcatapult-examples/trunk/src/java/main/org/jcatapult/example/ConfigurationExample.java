package org.jcatapult.example;

import org.apache.commons.configuration.Configuration;

import com.google.inject.Inject;

public class ConfigurationExample {
    private final Configuration configuration;

    @Inject
    public ConfigurationExample(Configuration configuration) {
        this.configuration = configuration;

        int number = this.configuration.getInt("number.of.turns");
    }
}