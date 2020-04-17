package org.wildfly.extension.feature.pack.template.example;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.wildfly.feature.pack.template.dependency.ExampleQualifier;
import org.wildfly.feature.pack.template.dependency.Message;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@RequestScoped
@Path("/")
public class JaxRsResource {

    @Inject
    @ExampleQualifier
    Message greeting;


    @GET
    @Path("/greeting")
    public String getGreeting() {
        return greeting.getMessage() + "!";
    }
}
