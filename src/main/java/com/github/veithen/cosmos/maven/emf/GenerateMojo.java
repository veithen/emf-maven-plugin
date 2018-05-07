/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2018 Andreas Veithen
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenerateMojo extends EMFMojo {
    private static final Logger logger = LoggerFactory.getLogger(GenerateMojo.class);

    @Parameter(property="project", required=true, readonly=true)
    private MavenProject project;
    
    @Parameter(property="mojoExecution", required=true, readonly=true)
    private MojoExecution mojoExecution;
    
    @Parameter(required=true)
    private File genmodel;

    @Parameter(required=true, defaultValue="${project.build.directory}/generated-sources/emf")
    private File outputDirectory;
    
    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        try {
            generate(EMFUtil.loadGenModel(genmodel));
        } catch (IOException ex) {
            throw new MojoFailureException(ex.getMessage(), ex);
        }
    }
    
    private void generate(GenModel genmodel) throws MojoExecutionException, MojoFailureException {
        genmodel.reconcile();
        
        Monitor monitor = new DebugMonitor(logger);
        IProgressMonitor progressMonitor = BasicMonitor.toIProgressMonitor(monitor);
        
        String eclipseProjectName = project.getGroupId() + "-" + project.getArtifactId() + "-" + mojoExecution.getGoal() + "-" + mojoExecution.getExecutionId();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject eclipseProject = root.getProject(eclipseProjectName);
        ProjectDescription projectDescription = new ProjectDescription();
        projectDescription.setName(eclipseProjectName);
        projectDescription.setLocationURI(outputDirectory.toURI());
        try {
            eclipseProject.create(projectDescription, progressMonitor);
        } catch (CoreException ex) {
            throw new MojoFailureException("Unable to create Eclipse project", ex);
        }
        try {
            try {
                eclipseProject.open(progressMonitor);
            } catch (CoreException ex) {
                throw new MojoFailureException("Unable to open Eclipse project", ex);
            }
            
            Generator gen = new Generator();
            gen.setInput(genmodel);
            // Setting the plugin ID to null suppresses generation of plugin.xml and related files
            genmodel.setModelPluginID(null);
            genmodel.setCanGenerate(true);
            if (!gen.canGenerate(genmodel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE)) {
                throw new MojoExecutionException("canGenerate returned false");
            }
            genmodel.setModelDirectory(eclipseProjectName);
            Diagnostic diagnostic = gen.generate(genmodel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, monitor);
            if (diagnostic.getSeverity() != Diagnostic.OK) {
                System.out.println("Diagnostic:");
                printDiagnostic(0, diagnostic);
                throw new MojoExecutionException("Code generation failed; see diagnostic for details");
            }
        } finally {
            try {
                eclipseProject.delete(false, true, progressMonitor);
            } catch (CoreException ex) {
                throw new MojoFailureException("Unable to delete Eclipse project", ex);
            }
        }
        
        addSourceRoot(project, outputDirectory.toString());
        Resource resource = new Resource();
        resource.setDirectory(outputDirectory.toString());
        resource.setExcludes(Arrays.asList(".project", "**/*.java"));
        addResource(project, resource);
    }

    private static void printDiagnostic(int level, Diagnostic diagnostic) {
        for (int i=0; i<level; i++) {
            System.out.print("  ");
        }
        System.out.println(diagnostic.getMessage());
        for (Diagnostic child : diagnostic.getChildren()) {
            if (child.getSeverity() != Diagnostic.OK) {
                printDiagnostic(level+1, child);
            }
        }
    }

    protected abstract void addSourceRoot(MavenProject project, String path);
    protected abstract void addResource(MavenProject project, Resource resource);
}
