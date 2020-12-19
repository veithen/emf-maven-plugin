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
