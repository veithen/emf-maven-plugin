/*-
 * #%L
 * emf-maven-plugin
 * %%
 * Copyright (C) 2014 - 2020 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.cosmos.maven.emf;

import org.apache.maven.model.Resource;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

@Mojo(name="generate-test-sources", defaultPhase=LifecyclePhase.GENERATE_TEST_SOURCES)
public class GenerateTestSourcesMojo extends GenerateMojo {
    @Override
    protected void addSourceRoot(MavenProject project, String path) {
        project.addTestCompileSourceRoot(path);
    }

    @Override
    protected void addResource(MavenProject project, Resource resource) {
        project.addTestResource(resource);
    }
}
