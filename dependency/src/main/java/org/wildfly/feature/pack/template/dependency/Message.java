package org.wildfly.feature.pack.template.dependency;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class Message {
    private final String message;

    public Message() {
        this.message = null;
    }

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
