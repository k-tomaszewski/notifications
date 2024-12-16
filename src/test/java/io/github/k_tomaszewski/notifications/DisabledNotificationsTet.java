package io.github.k_tomaszewski.notifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = NotificationsConfig.class, properties = "notifications.enabled=false")
@EnableAutoConfiguration
@ActiveProfiles("test")
public class DisabledNotificationsTet {

    @Autowired
    private NotificationService notificationService;

    @Test
    void shouldProvideNotificationServiceWhenNotificationsDisabled() {
        Assertions.assertNotNull(notificationService);
        notificationService.send(Level.INFO, "title", "content");       // and no error
    }
}
