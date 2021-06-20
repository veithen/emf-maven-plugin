/*-
 * #%L
 * emf-maven-plugin
 * %%
 * Copyright (C) 2014 - 2021 Andreas Veithen
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

import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.slf4j.Logger;

final class DebugMonitor extends BasicMonitor {
    private final Logger logger;

    DebugMonitor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void beginTask(String name, int totalWork) {
        if (logger.isDebugEnabled() && name != null && !name.isEmpty()) {
            logger.debug(">>> " + name);
        }
    }

    @Override
    public void setTaskName(String name) {
        if (logger.isDebugEnabled() && name != null && !name.isEmpty()) {
            logger.debug("<>> " + name);
        }
    }

    @Override
    public void subTask(String name) {
        if (logger.isDebugEnabled() && name != null && !name.isEmpty()) {
            logger.debug(">>  " + name);
        }
    }

    @Override
    public void setBlocked(Diagnostic reason) {
        super.setBlocked(reason);
        if (logger.isDebugEnabled()) {
            logger.debug("#>  " + reason.getMessage());
        }
    }

    @Override
    public void clearBlocked() {
        if (logger.isDebugEnabled()) {
            logger.debug("=>  " + getBlockedReason().getMessage());
        }
        super.clearBlocked();
    }
}
