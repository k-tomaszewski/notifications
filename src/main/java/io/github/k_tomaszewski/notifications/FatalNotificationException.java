package io.github.k_tomaszewski.notifications;

/**
 * Exceptions related to sending notifications that denote error situations in which we cannot retry.
 */
public class FatalNotificationException extends RuntimeException {

    public FatalNotificationException(String message) {
        this(message, null);
    }

    public FatalNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
