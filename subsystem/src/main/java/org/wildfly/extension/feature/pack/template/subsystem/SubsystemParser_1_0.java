/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package org.wildfly.extension.feature.pack.template.subsystem;

import static org.jboss.as.controller.PersistentResourceXMLDescription.builder;

import org.jboss.as.controller.PersistentResourceXMLDescription;
import org.jboss.as.controller.PersistentResourceXMLParser;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SubsystemParser_1_0 extends PersistentResourceXMLParser {

    // TODO Rename to something that makes sense and update the template-subsytem.xsd namespaces to match
    public static final String NAMESPACE = "urn:wildfly:template-subsystem:1.0";

    private static final PersistentResourceXMLDescription xmlDescription = builder(TemplateExtension.SUBSYSTEM_PATH, NAMESPACE)
            .build();

    @Override
    public PersistentResourceXMLDescription getParserDescription() {
        return xmlDescription;
    }

}
