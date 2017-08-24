/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.snowdrop.licenses.properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class GeneratorProperties {

    private static final String DEFAULT_FILE_PATH = "generator.properties";

    private static final String DEFAULT_REPOSITORY_NAME = "Maven Central";

    private static final String DEFAULT_REPOSITORY_URL = "http://repo1.maven.org/maven2";

    private static final boolean DEFAULT_IS_PROCESS_PLUGINS = false;

    private static final boolean DEFAULT_IS_INCLUDE_OPTIONAL = false;

    private static final String DEFAULT_EXCLUDED_SCOPES = "test,system,provided";

    private static final String DEFAULT_EXCLUDED_CLASSIFIERS = "tests";

    private final Configuration configuration;

    public GeneratorProperties() {
        this(DEFAULT_FILE_PATH);
    }

    public GeneratorProperties(String filePath) {
        try {
            configuration = new Configurations().properties(filePath);
        } catch (ConfigurationException e) {
            throw new GeneratorPropertiesException("Couldn't load application properties", e);
        }
    }

    public Map<String, String> getRepositories() {
        String joinedNames = configuration.getString(PropertyKeys.REPOSITORY_NAMES, DEFAULT_REPOSITORY_NAME);
        String joinedUrls = configuration.getString(PropertyKeys.REPOSITORY_URLS, DEFAULT_REPOSITORY_URL);
        String[] names = joinedNames.split(",");
        String[] urls = joinedUrls.split(",");

        if (names.length != urls.length) {
            throw new GeneratorPropertiesException("Same number of repository names and urls is expected");
        }

        return IntStream.range(0, names.length)
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(names[i], urls[i]))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean isProcessPlugins() {
        return configuration.getBoolean(PropertyKeys.PROCESS_PLUGINS, DEFAULT_IS_PROCESS_PLUGINS);
    }

    public List<String> getExcludedScopes() {
        String joinedScopes = configuration.getString(PropertyKeys.EXCLUDED_SCOPES, DEFAULT_EXCLUDED_SCOPES);
        return Arrays.asList(joinedScopes.split(","));
    }

    public List<String> getExcludedClassifiers() {
        String joinedClassifiers =
                configuration.getString(PropertyKeys.EXCLUDED_CLASSIFIERS, DEFAULT_EXCLUDED_CLASSIFIERS);
        return Arrays.asList(joinedClassifiers.split(","));
    }

    public boolean isIncludeOptional() {
        return configuration.getBoolean(PropertyKeys.INCLUDE_OPTIONAL, DEFAULT_IS_INCLUDE_OPTIONAL);
    }

}