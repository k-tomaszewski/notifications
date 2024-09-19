package io.github.k_tomaszewski.notifications.internal;

import io.github.k_tomaszewski.notifications.FatalNotificationException;
import jakarta.mail.AuthenticationFailedException;

import java.util.Set;

public class Exceptions {

    private static final Set<Class<?>> FATAL_EXCEPTIONS = Set.of(FatalNotificationException.class,
            AuthenticationFailedException.class);

    public static boolean isFatal(Throwable t) {
        for (; t != null; t = t.getCause()) {
            if (isInstanceOfAnyFatalException(t)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInstanceOfAnyFatalException(Throwable t) {
        for (Class<?> exceptionClass : FATAL_EXCEPTIONS) {
            if (exceptionClass.isInstance(t)) {
                return true;
            }
        }
        return false;
    }
}
