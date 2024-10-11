package io.github.k_tomaszewski.notifications;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

/**
 * To be run manually only for real integration testing. It requires preparing external configuration files and changing the property
 * 'spring.config.location' in @SpringBootTest annotation.
 */
@SpringBootTest(classes = NotificationsConfig.class, properties = {"spring.config.location=${user.home}/PROJEKTY/notifications-it.yml"})
@EnableAutoConfiguration
@Disabled
@ActiveProfiles("it")
public class SmtpIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Test
    void shouldSendEmail() throws InterruptedException {
        // when
        notificationService.send(Level.INFO, "Test, " + LocalDateTime.now(), "Some test content sent from"
                + "<br/>user:   " + System.getProperty("user.name")
                + "<br/>system: " + System.getProperty("os.name")
                + "<br/>Java:   " + System.getProperty("java.version")
                + "<br/>PID:    " + System.getProperty("PID"));

        // then just wait for sending thread, including some time for possible timeout
        Thread.sleep(5000);
    }
}
