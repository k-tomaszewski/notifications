package io.github.k_tomaszewski.notifications.internal;

import org.slf4j.event.Level;

/**
 * Notification data accompanied by some context.
 */
public class NotificationContext implements Notification {

    private final Level level;
    private final String title;
    private final Object content;
    private final long startMillis = System.currentTimeMillis();
    private int retryCount = 0;

    NotificationContext(Level level, String title, Object content) {
        this.level = level;
        this.title = title;
        this.content = content;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Object getContent() {
        return content;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int incrementRetryCount() {
        return ++retryCount;
    }
}
