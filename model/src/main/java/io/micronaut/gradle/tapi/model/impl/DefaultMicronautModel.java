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

import io.micronaut.gradle.tapi.model.MicronautModel;
import io.micronaut.gradle.tapi.model.MicronautTask;
import io.micronaut.gradle.tapi.model.ProjectKind;

import java.io.Serializable;
import java.util.List;

public class DefaultMicronautModel implements MicronautModel, Serializable {
    private final String projectPath;
    private final ProjectKind projectKind;
    private final List<MicronautTask> tasks;

    public DefaultMicronautModel(String projectPath, ProjectKind projectKind, List<MicronautTask> tasks) {
        this.projectPath = projectPath;
        this.projectKind = projectKind;
        this.tasks = tasks;
    }

    @Override
    public ProjectKind getKind() {
        return projectKind;
    }

    @Override
    public List<MicronautTask> getMicronautTasks() {
        return tasks;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Project path: ").append(projectPath).append("\n");
        sb.append("Kind: ").append(projectKind).append("\n");
        sb.append("Tasks:\n");
        for (MicronautTask task : tasks) {
            sb.append("    Path: ").append(task.getTaskPath()).append("\n");
            sb.append("    Description: ").append(task.getDescription()).append("\n");
            sb.append("    Kind: ").append(task.getKind()).append("\n");
            sb.append("    Environment: ").append(task.getEnvironment()).append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }
}
