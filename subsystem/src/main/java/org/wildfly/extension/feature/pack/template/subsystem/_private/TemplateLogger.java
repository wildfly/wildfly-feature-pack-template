/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package org.wildfly.extension.feature.pack.template.subsystem._private;

import static org.jboss.logging.Logger.Level.INFO;

import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * Log messages for our template subsystem.
 *
 * @author <a href="kkhan@redhat.com">Kabir Khan</a> (c) 2019 Red Hat inc.
 */
// TODO Make the projectCode in the @MessageLogger annotation some abbreviation to identify your subsystem
@MessageLogger(projectCode = "WFLYGPTMPL", length = 4)
// TODO Rename to something that makes sense for your subsystem.
public interface TemplateLogger extends BasicLogger {

    TemplateLogger LOGGER = Logger.getMessageLogger(TemplateLogger.class, "org.wildfly.extension.galleon.pack.template.subsystem");

    /**
     * Logs an informational message indicating the subsystem is being activated.
     */
    @LogMessage(level = INFO)
    @Message(id = 1, value = "Activating Template Subsystem")
    void activatingSubsystem();


    @Message(id = 2, value = "Deployment %s requires use of the '%s' capability but it is not currently registered")
    DeploymentUnitProcessingException deploymentRequiresCapability(String deploymentName, String capabilityName);
}
