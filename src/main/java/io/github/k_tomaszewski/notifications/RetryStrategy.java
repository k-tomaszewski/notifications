package io.github.k_tomaszewski.notifications;

import io.github.k_tomaszewski.notifications.internal.NotificationContext;

public interface RetryStrategy {

    boolean goingToRetry(NotificationContext notificationCtx);
    long millisToRetry(NotificationContext notificationCtx);
}
