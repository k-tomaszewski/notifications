package io.github.k_tomaszewski.notifications;

import io.github.k_tomaszewski.notifications.internal.NotificationContext;
import io.github.k_tomaszewski.notifications.internal.Sender;

import java.util.List;

public interface SendingStrategy {

    void send(NotificationContext notificationCtx, List<? extends Sender> senders);
}
