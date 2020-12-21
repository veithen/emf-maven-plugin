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
import java.util.Collections;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

final class EMFUtil {
    private EMFUtil() {}

    static GenModel loadGenModel(File genmodel) throws IOException {
        ResourceSet set = new ResourceSetImpl();
        Resource res = set.getResource(URI.createFileURI(genmodel.getAbsolutePath()), true);
        res.load(Collections.emptyMap());
        for (TreeIterator<EObject> it = res.getAllContents(); it.hasNext(); ) {
            EObject obj = it.next();
            if (obj instanceof GenModel) {
                return (GenModel)obj;
            }
        }
        throw new IllegalArgumentException(String.format("%s doesn't contain any GenModel", genmodel));
    }
}
