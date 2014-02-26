package com.github.veithen.cosmos.maven.emf;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

@Mojo(name="generate-sources", defaultPhase=LifecyclePhase.GENERATE_SOURCES)
public class GenerateSourcesMojo extends GenerateMojo {
    @Override
    protected void addSourceRoot(MavenProject project, String path) {
        project.addCompileSourceRoot(path);
    }
}
