/*
 * Copyright 2019 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.extension.feature.pack.template.subsystem;

import static org.jboss.as.controller.OperationContext.Stage.RUNTIME;
import static org.jboss.as.server.deployment.Phase.DEPENDENCIES;
import static org.wildfly.extension.feature.pack.template.subsystem.TemplateExtension.WELD_CAPABILITY_NAME;

import java.util.Collection;
import java.util.Collections;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.ModelOnlyRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.dmr.ModelNode;
import org.wildfly.extension.feature.pack.template.subsystem._private.TemplateLogger;
import org.wildfly.extension.feature.pack.template.subsystem.deployment.DependencyProcessor;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class TemplateSubsystemDefinition extends PersistentResourceDefinition {

    // TODO Change this to something that makes sense for your subsystem
    private static final String TEMPLATE_SUBSYSTEM_CAPABILITY_NAME = "org.wildfly.extras.template";

    private static final RuntimeCapability<Void> CONTEXT_PROPAGATION_CAPABILITY = RuntimeCapability.Builder
            .of(TEMPLATE_SUBSYSTEM_CAPABILITY_NAME)
            .addRequirements(WELD_CAPABILITY_NAME)
            .build();

    public TemplateSubsystemDefinition() {
        super(
                new SimpleResourceDefinition.Parameters(
                        TemplateExtension.SUBSYSTEM_PATH,
                        TemplateExtension.getResourceDescriptionResolver(TemplateExtension.SUBSYSTEM_NAME))
                .setAddHandler(AddHandler.INSTANCE)
                .setRemoveHandler(new ModelOnlyRemoveStepHandler())
                .setCapabilities(CONTEXT_PROPAGATION_CAPABILITY)
        );
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Collections.emptyList();
    }

    @Override
    public void registerAdditionalRuntimePackages(ManagementResourceRegistration resourceRegistration) {
        super.registerAdditionalRuntimePackages(resourceRegistration);
        //TODO - If your feature-pack needs any other modules, you should add those here, and remove the line above
        /*
        resourceRegistration.registerAdditionalRuntimePackages(
                // Required dependencies are always added
                RuntimePackageDependency.required("my.required.module"),
                // Optional and passive modules depend on the 'optional-packages' mode. See the Galleon
                // documentation for more details as this is an advanced feature
                RuntimePackageDependency.optional("my.optional.module"),
                RuntimePackageDependency.passive("my.passive.module")
        );*/
    }

    static class AddHandler extends AbstractBoottimeAddStepHandler {

        static AddHandler INSTANCE = new AddHandler();

        private AddHandler() {
            super(Collections.emptyList());
        }

        @Override
        protected void performBoottime(OperationContext context, ModelNode operation, ModelNode model) throws OperationFailedException {
            super.performBoottime(context, operation, model);

            context.addStep(new AbstractDeploymentChainStep() {
                public void execute(DeploymentProcessorTarget processorTarget) {
                    final int DEPENDENCIES_TEMPLATE = 6304;
                    processorTarget.addDeploymentProcessor(TemplateExtension.SUBSYSTEM_NAME, DEPENDENCIES, DEPENDENCIES_TEMPLATE, new DependencyProcessor());
                }
            }, RUNTIME);

            TemplateLogger.LOGGER.activatingSubsystem();
        }
    }
}
