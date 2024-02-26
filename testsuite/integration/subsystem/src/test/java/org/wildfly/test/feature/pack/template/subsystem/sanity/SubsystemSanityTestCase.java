/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package org.wildfly.test.feature.pack.template.subsystem.sanity;

import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.feature.pack.template.dependency.ExampleQualifier;
import org.wildfly.feature.pack.template.dependency.Message;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@RunWith(Arquillian.class)
public class SubsystemSanityTestCase {

    @Inject
    @ExampleQualifier
    Message greeting;

    @Deployment
    public static WebArchive getDeployment() {
        return ShrinkWrap.create(WebArchive.class, "sanity-test.war")
                .addAsWebInfResource(new StringAsset("<beans bean-discovery-mode=\"all\"></beans>"), "beans.xml")
                .addPackage(SubsystemSanityTestCase.class.getPackage());
    }

    @Test
    public void testAllOk() {
        // Nothing much is happening here yet - we just check we can load the class
        Assert.assertNotNull(greeting);
        Assert.assertEquals("Welcome! (English)", greeting.getMessage());
    }
}
