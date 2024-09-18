package io.github.k_tomaszewski.notifications.internal;

import io.github.k_tomaszewski.notifications.NotificationService;
import io.github.k_tomaszewski.notifications.NotificationsProperties;
import io.github.k_tomaszewski.notifications.RetryStrategy;
import io.github.k_tomaszewski.notifications.SendingStrategy;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import static io.github.k_tomaszewski.notifications.internal.Exceptions.isFatal;


public class NotificationServiceBean implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceBean.class);

    private final BlockingQueue<NotificationContext> notifications;
    private final Semaphore semaphore;
    private final List<? extends Sender> senders;
    private final Thread senderThread;
    private final SendingStrategy sendingStrategy;
    private final RetryStrategy retryStrategy;
    private volatile boolean running = false;
    private volatile boolean sleeping = false;

    public NotificationServiceBean(NotificationsProperties config, List<? extends Sender> senders, AutowireCapableBeanFactory beanFactory) {
        sendingStrategy = initializeBean(config.getSendingStrategy(), "notificationsSendingStrategy", beanFactory);
        retryStrategy = initializeBean(config.getRetryStrategy(), "notificationsRetryStrategy", beanFactory);
        notifications = new ArrayBlockingQueue<>(config.getQueueCapacity());
        semaphore = new Semaphore(0);
        this.senders = senders;
        if (senders.isEmpty()) {
            LOG.warn("No senders available for sending notifications.");
        }
        senderThread = new Thread(this::run, "notifications");
    }

    @Override
    public void send(Level level, String title, Object content) {
        if (notifications.offer(new NotificationContext(level, title, content))) {
            semaphore.release();
        } else {
            LOG.warn("Notification not sent due to no capacity: {} {}", level, title);
        }
    }

    @PostConstruct
    public void start() {
        LOG.info("Starting notifications thread...");
        running = true;
        senderThread.start();
    }

    @PreDestroy
    public void stop() {
        LOG.info("Stopping notifications thread...");
        running = false;
        if (notifications.isEmpty()) {
            semaphore.release();
        }
        if (sleeping) {
            senderThread.interrupt();
        }
    }

    private void run() {
        LOG.info("Notifications thread started.");
        try {
            while (running) {
                var notificationCtx = waitToPeekNotification();
                if (notificationCtx != null) {
                    handleSending(notificationCtx);
                }
            }
        } catch (InterruptedException e) {
            LOG.info("Notifications thread interrupted.");
        } catch (RuntimeException e) {
            LOG.warn("Notifications thread failure.", e);
        }
        LOG.info("Notifications thread stopped. In queue: {}", notifications.size());
    }

    private void handleSending(NotificationContext notificationCtx) throws InterruptedException {
        try {
            sendingStrategy.send(notificationCtx, senders);
            LOG.debug("Notification sent ({}).", notificationCtx.getLevel());
            remove(notificationCtx);
        } catch (RuntimeException e) {
            if (!isFatal(e) && retryStrategy.goingToRetry(notificationCtx)) {
                LOG.warn("Notification sending failed, but will be retried.", e);
                sleepBeforeRetry(notificationCtx);
            } else {
                LOG.warn("Notification sending failed.", e);
                remove(notificationCtx);
            }
        }
    }

    private void sleepBeforeRetry(NotificationContext notificationCtx) throws InterruptedException {
        sleeping = true;
        try {
            Thread.sleep(retryStrategy.millisToRetry(notificationCtx));
        } finally {
            sleeping = false;
        }
    }

    private NotificationContext waitToPeekNotification() throws InterruptedException {
        semaphore.acquire();
        return notifications.peek();
    }

    private void remove(NotificationContext notification) {
        Validate.isTrue(notifications.remove() == notification, "Given notification not the same as head in queue.");
    }

    @SuppressWarnings("unchecked")
    private static <T> T initializeBean(T obj, String beanName, AutowireCapableBeanFactory beanFactory) {
        return (T) beanFactory.initializeBean(obj, beanName);
    }
}
