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
package com.github.veithen.maven.emf;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.CatalogManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;

@Mojo(name="xsd2ecore")
public class XSD2EcoreMojo extends EMFMojo {
    @Parameter(required=true)
    private File input;
    
    @Parameter
    private File catalog;
    
    /**
     * Specifies the output file name for the generated Ecore model. If the conversion generates
     * multiple packages (because the input XML schema imports other namespaces), then they will all
     * be put into the same output file. Note that you should avoid this if you want to create a
     * generator model for the resulting Ecore model.
     */
    @Parameter
    private File output;
    
    /**
     * The output directory to write generated Ecore models to. This parameter is only used if
     * <tt>output</tt> is not set.
     */
    @Parameter(defaultValue="${project.build.directory}/model")
    private File outputDirectory;

    /**
     * Specifies whether the generated packages should have qualified names. Note that you should
     * avoid qualified package names if you want to create a generator model for the Ecore model.
     */
    @Parameter(defaultValue="false")
    private boolean useQualifiedPackageNames;
    
    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        Catalog catalog;
        if (this.catalog == null) {
            catalog = null;
        } else {
            CatalogManager catalogManager = new CatalogManager();
            catalogManager.setCatalogFiles(this.catalog.getAbsolutePath());
            catalog = catalogManager.getCatalog();
        }
        XSDEcoreBuilder xsdEcoreBuilder = new CustomXSDEcoreBuilder(new Resolver(getLog(), catalog));
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource commonResource = output == null ? null : createXMIResource(resourceSet, output);
        for (EObject element : xsdEcoreBuilder.generate(URI.createFileURI(input.getAbsolutePath()))) {
            EPackage ePackage = (EPackage)element;
            if (!useQualifiedPackageNames) {
                String name = ePackage.getName();
                ePackage.setName(name.substring(name.lastIndexOf('.')+1));
            }
            Resource resource = commonResource == null ? createXMIResource(resourceSet, new File(outputDirectory, ePackage.getNsPrefix() + ".ecore")) : commonResource;
            resource.getContents().add(element);
        }
        try {
            for (Resource resource : resourceSet.getResources()) {
                resource.save(null);
            }
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }
    
    private static Resource createXMIResource(ResourceSet resourceSet, File file) {
        // Don't use ResourceSet#createResource: this gives us control over the resource type being created
        // (XMI) and we don't need to register a ResourceFactory (which would be problematic because
        // the user may specify any suffix for the output file).
        Resource resource = new XMIResourceImpl(URI.createFileURI(file.getAbsolutePath()));
        resourceSet.getResources().add(resource);
        return resource;
    }
}
