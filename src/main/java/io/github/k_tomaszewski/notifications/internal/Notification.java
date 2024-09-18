package io.github.k_tomaszewski.notifications.internal;

import org.slf4j.event.Level;

public interface Notification {

    Level getLevel();
    String getTitle();
    Object getContent();
}
