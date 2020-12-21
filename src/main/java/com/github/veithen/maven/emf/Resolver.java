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

import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.apache.xml.resolver.Catalog;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDSchemaLocationResolver;

final class Resolver extends AdapterImpl implements XSDSchemaLocationResolver {
    private final Log log;
    private final Catalog catalog;
    
    Resolver(Log log, Catalog catalog) {
        this.log = log;
        this.catalog = catalog;
    }

    @Override
    public String resolveSchemaLocation(XSDSchema xsdSchema, String namespaceURI, String schemaLocationURI) {
        if (log.isDebugEnabled()) {
            log.debug("Resolving " + schemaLocationURI + " (namespace: " + namespaceURI + ") relative to " + xsdSchema.getSchemaLocation());
        }
        String location = XSDConstants.resolveSchemaLocation(xsdSchema.getSchemaLocation(), namespaceURI, schemaLocationURI);
        if (catalog != null) {
            try {
                String altLocation = catalog.resolveURI(location);
                if (altLocation == null) {
                    altLocation = catalog.resolveURI(namespaceURI);
                }
                if (altLocation != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Catalog has alternate location for " + location);
                    }
                    location = altLocation;
                }
            } catch (IOException ex) {
                log.warn("Failed to resolve URI using catalog", ex);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Resolved schema location: " + location);
        }
        return location;
    }

    @Override
    public boolean isAdapterForType(Object type) {
        return type == XSDSchemaLocationResolver.class;
    }
}
