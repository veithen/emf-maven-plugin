package com.github.veithen.cosmos.maven.emf;

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
