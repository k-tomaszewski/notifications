package io.github.k_tomaszewski.notifications.internal;

import io.github.k_tomaszewski.notifications.RetryStrategy;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

public class BasicRetryStrategy implements RetryStrategy {

    @Value("${notifications.retry.duration}")
    private Duration retryingDuration = Duration.ofDays(7);

    @Override
    public boolean goingToRetry(NotificationContext notificationCtx) {
        return Duration.ofMillis(System.currentTimeMillis() - notificationCtx.getStartMillis()).compareTo(retryingDuration) < 0;
    }

    @Override
    public long millisToRetry(NotificationContext notificationCtx) {
        return 0;
    }
}
