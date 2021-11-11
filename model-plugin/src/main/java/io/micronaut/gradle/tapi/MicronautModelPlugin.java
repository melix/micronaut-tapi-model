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
package io.micronaut.gradle.tapi;

import io.micronaut.gradle.tapi.model.EnvironmentKind;
import io.micronaut.gradle.tapi.model.MicronautModel;
import io.micronaut.gradle.tapi.model.MicronautTask;
import io.micronaut.gradle.tapi.model.ProjectKind;
import io.micronaut.gradle.tapi.model.TaskKind;
import io.micronaut.gradle.tapi.model.impl.DefaultMicronautModel;
import io.micronaut.gradle.tapi.model.impl.DefaultMicronautTask;
import org.gradle.api.NamedDomainObjectCollectionSchema;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.reflect.TypeOf;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MicronautModelPlugin implements Plugin<Project> {
    private final ToolingModelBuilderRegistry registry;

    @Inject
    public MicronautModelPlugin(ToolingModelBuilderRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void apply(Project target) {
        registry.register(new MicronautModelBuilder());
    }

    private static class MicronautModelBuilder implements ToolingModelBuilder {
        @Override
        public boolean canBuild(String modelName) {
            return MicronautModel.class.getName().equals(modelName);
        }

        @Override
        public Object buildAll(String modelName, Project project) {
            ProjectKind projectKind = kindOf(project);
            TaskContainer taskCollection = project.getTasks();

            List<MicronautTask> tasks = StreamSupport.stream(
                            taskCollection
                                    .getCollectionSchema()
                                    .getElements()
                                    .spliterator(), false
                    ).map(task -> {
                        TypeOf<?> publicType = task.getPublicType();
                        String strType = publicType.toString();
                        switch (strType) {
                            // Micronaut Gradle 2.x
                            case "io.micronaut.gradle.graalvm.NativeImageTask":
                                return asMicronautTask(taskCollection, task, TaskKind.COMPILE, EnvironmentKind.NATIVE);

                            // Micronaut Gradle 3.x
                            case "org.graalvm.buildtools.gradle.tasks.NativeRunTask":
                                return asMicronautTask(taskCollection, task,
                                        task.getName().endsWith("Test") ? TaskKind.TEST : TaskKind.RUN,
                                        EnvironmentKind.NATIVE);
                            // Standard Java
                            case "org.gradle.api.tasks.testing.Test":
                                return asMicronautTask(taskCollection, task, TaskKind.TEST, EnvironmentKind.JVM);
                            case "org.gradle.api.tasks.JavaExec":
                                return asMicronautTask(taskCollection, task, TaskKind.RUN, EnvironmentKind.JVM);

                                // docker
                            case "com.bmuschko.gradle.docker.tasks.image.DockerBuildImage":
                                return asMicronautTask(taskCollection, task, TaskKind.RUN, EnvironmentKind.DOCKER);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())
                    .stream()
                    .map(Supplier::get)
                    .collect(Collectors.toList());
            return new DefaultMicronautModel(project.getPath(), projectKind, tasks);
        }

        private Supplier<MicronautTask> asMicronautTask(
                TaskCollection<?> tasks,
                NamedDomainObjectCollectionSchema.NamedDomainObjectSchema task,
                TaskKind taskKind,
                EnvironmentKind env) {
            return () -> {
                Task realized = tasks.findByName(task.getName());
                return new DefaultMicronautTask(taskKind, env, realized.getPath(), realized.getDescription());
            };
        }

        private ProjectKind kindOf(Project project) {
            ProjectKind kind = ProjectKind.UNKNOWN;
            Plugin plugin = project.getPlugins().findPlugin("io.micronaut.application");
            if (plugin != null) {
                kind = ProjectKind.MICRONAUT_APPLICATION;
            } else {
                plugin = project.getPlugins().findPlugin("io.micronaut.library");
                if (plugin != null) {
                    kind = ProjectKind.MICRONAUT_LIBRARY;
                }
            }
            return kind;
        }
    }
}
