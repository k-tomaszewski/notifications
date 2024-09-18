package io.github.k_tomaszewski.notifications;

import org.slf4j.event.Level;

public interface NotificationService {
    /**
     * Send a notification.
     * @param level A level of this notification. Don't use values OFF and ALL.
     * @param title Notification's title - must be a plain text (no HTML).
     * @param content This object's toString() method will be used to obtain notification content. It may contain HTML.
     */
    void send(Level level, String title, Object content);
}
