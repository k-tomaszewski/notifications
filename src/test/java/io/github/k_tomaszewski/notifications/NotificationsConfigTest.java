package io.github.k_tomaszewski.notifications;

import io.github.k_tomaszewski.notifications.smtp.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Properties;
import java.util.Set;

@SpringBootTest(classes = NotificationsConfig.class)
@EnableAutoConfiguration
@ActiveProfiles("test")
public class NotificationsConfigTest {

    @Autowired
    private NotificationsProperties properties;

    @Test
    void shouldLoadConfigurationWithManySenders() {
        Assertions.assertEquals(Set.of("sender1", "sender2"), properties.getSmtp().keySet());

        Config sender1 = properties.getSmtp().get("sender1");
        assertHaveDefaultProperties(sender1.properties());

        Config sender2 = properties.getSmtp().get("sender2");
        assertHaveDefaultProperties(sender2.properties());
    }

    private static void assertHaveDefaultProperties(Properties props) {
        Assertions.assertEquals("true", props.get("mail.smtp.auth"));
        Assertions.assertEquals("TLSv1.2", props.get("mail.smtp.ssl.protocols"));
        Assertions.assertEquals("1000", props.get("mail.smtp.connectiontimeout"));
        Assertions.assertEquals("5000", props.get("mail.smtp.timeout"));
    }
}
