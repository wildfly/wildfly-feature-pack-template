package org.wildfly.feature.pack.template.dependency;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ExampleProducer {
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private static final List<String> MESSAGES = Collections.unmodifiableList(Arrays.asList(
            "Welcome! (English)",
            "Willkommen! (German)",
            "Velkommen! (Norwegian)",
            "Bienvenidos! (Spanish)"));

    @Produces
    @RequestScoped
    @ExampleQualifier
    public Message getWelcomeMessage() {
        int counter = COUNTER.getAndIncrement();
        int index = counter % 4;
        String msg = MESSAGES.get(index);
        return new Message(msg);
    }
}
