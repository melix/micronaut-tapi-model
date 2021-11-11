/*
 * Copyright 2003-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.gradle.tapi.model.impl;

import io.micronaut.gradle.tapi.model.EnvironmentKind;
import io.micronaut.gradle.tapi.model.MicronautTask;
import io.micronaut.gradle.tapi.model.TaskKind;

import java.io.Serializable;

public class DefaultMicronautTask implements MicronautTask, Serializable {
    private final TaskKind kind;
    private final EnvironmentKind environment;
    private final String path;
    private final String description;

    public DefaultMicronautTask(TaskKind kind,
                                EnvironmentKind environment,
                                String path,
                                String description) {
        this.kind = kind;
        this.environment = environment;
        this.path = path;
        this.description = description;
    }

    @Override
    public TaskKind getKind() {
        return kind;
    }

    @Override
    public EnvironmentKind getEnvironment() {
        return environment;
    }

    @Override
    public String getTaskPath() {
        return path;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
