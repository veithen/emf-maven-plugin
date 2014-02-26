package com.github.veithen.cosmos.maven.emf;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;

final class CustomXSDEcoreBuilder extends XSDEcoreBuilder {
    private final Resolver resolver;
    
    public CustomXSDEcoreBuilder(Resolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected ResourceSet createResourceSet() {
        ResourceSet resourceSet = super.createResourceSet();
        resourceSet.getAdapterFactories().add(new ResolverAdapterFactory(resolver));
        return resourceSet;
    }
}
