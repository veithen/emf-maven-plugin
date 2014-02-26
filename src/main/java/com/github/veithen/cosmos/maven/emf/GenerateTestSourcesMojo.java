package com.github.veithen.cosmos.maven.emf;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

@Mojo(name="generate-test-sources", defaultPhase=LifecyclePhase.GENERATE_TEST_SOURCES)
public class GenerateTestSourcesMojo extends GenerateMojo {
    @Override
    protected void addSourceRoot(MavenProject project, String path) {
        project.addTestCompileSourceRoot(path);
    }
}
