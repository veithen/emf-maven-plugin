package com.github.veithen.cosmos.maven.emf;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.xsd.util.XSDSchemaLocationResolver;

final class ResolverAdapterFactory extends AdapterFactoryImpl {
    private final Resolver resolver;

    ResolverAdapterFactory(Resolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public boolean isFactoryForType(Object type) {
        return type == XSDSchemaLocationResolver.class;
    }

    @Override
    public Adapter adaptNew(Notifier target, Object type) {
        return resolver;
    }
}
