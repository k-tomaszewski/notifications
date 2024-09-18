package io.github.k_tomaszewski.notifications.internal;

import io.github.k_tomaszewski.notifications.FatalNotificationException;
import io.github.k_tomaszewski.notifications.SendingStrategy;

import java.util.LinkedList;
import java.util.List;

import static io.github.k_tomaszewski.notifications.internal.Exceptions.isFatal;

/**
 * Tries all senders until there is a success.
 */
public class BasicSendingStrategy implements SendingStrategy {

    @Override
    public void send(NotificationContext notificationCtx, List<? extends Sender> senders) {
        if (senders == null || senders.isEmpty()) {
            throw new FatalNotificationException("No senders available");
        }
        List<RuntimeException> exceptions = null;
        for (Sender sender : senders) {
            try {
                sender.send(notificationCtx);
                return;
            } catch (RuntimeException e) {
                if (exceptions == null) {
                    exceptions = new LinkedList<>();
                }
                exceptions.add(e);
            }
        }

        if (exceptions.stream().allMatch(Exceptions::isFatal)) {
            throw getFirstWithOthersAsSuppressed(exceptions);
        } else {
            throw getFirstWithOthersAsSuppressed(exceptions.stream().filter(e -> !isFatal(e)).toList());
        }
    }

    private static RuntimeException getFirstWithOthersAsSuppressed(List<RuntimeException> exceptions) {
        var firstException = exceptions.getFirst();
        exceptions.stream().skip(1).forEach(firstException::addSuppressed);
        return firstException;
    }
}
