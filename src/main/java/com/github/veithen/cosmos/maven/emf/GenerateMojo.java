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
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

public abstract class GenerateMojo extends AbstractMojo {
    @Parameter(defaultValue="${project}", required=true, readonly=true)
    private MavenProject project;
    
    @Parameter(required=true)
    private File genmodel;

    @Parameter(required=true, defaultValue="${project.build.directory}/generated-sources/emf")
    private File outputDirectory;
    
    static {
        // Ensure that the GenModel package is registered in the package registry
        GenModelPackage.eINSTANCE.eClass();
    }
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ResourceSet set = new ResourceSetImpl();
        EcoreResourceFactoryImpl ecoreFactory = new EcoreResourceFactoryImpl();
        Resource.Factory.Registry registry = set.getResourceFactoryRegistry();
        Map<String,Object> map = registry.getExtensionToFactoryMap();
        map.put("ecore", ecoreFactory);
        map.put("genmodel", ecoreFactory);

        Resource res = set.getResource(URI.createFileURI(genmodel.getAbsolutePath()), true);
        try {
            res.load(new HashMap());
        } catch (IOException ex) {
            throw new MojoFailureException(ex.getMessage(), ex);
        }
        GenModel genmodel = null;
        for (TreeIterator<EObject> it = res.getAllContents(); it.hasNext(); ) {
            EObject obj = it.next();
            if (obj instanceof GenModel) {
                genmodel = (GenModel)obj;
                break;
            }
        }
        genmodel.reconcile();
        
        Monitor monitor = new BasicMonitor.Printing(System.out);
        
        EcorePlugin.getPlatformResourceMap().put("out", URI.createFileURI(outputDirectory.getAbsolutePath() + "/"));
        
        Generator gen = new Generator();
//      gen.getOptions().resourceSet = set;
        gen.getAdapterFactoryDescriptorRegistry().addDescriptor("http://www.eclipse.org/emf/2002/GenModel", GenModelGeneratorAdapterFactory.DESCRIPTOR);
        gen.setInput(genmodel);
        // Setting the plugin ID to null suppresses generation of plugin.xml and related files
        genmodel.setModelPluginID(null);
        genmodel.setCanGenerate(true);
        System.out.println(gen.canGenerate(genmodel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE));
//      genModel.setModelDirectory(URI.createFileURI(new File("gen-src").getAbsolutePath()).toString());
        genmodel.setModelDirectory("out");
        gen.generate(genmodel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, monitor);
        
        addSourceRoot(project, outputDirectory.toString());
    }

    protected abstract void addSourceRoot(MavenProject project, String path);
}
