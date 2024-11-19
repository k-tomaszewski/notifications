package io.github.k_tomaszewski.notifications;

import io.github.k_tomaszewski.notifications.smtp.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;

import java.util.Set;

import static io.github.k_tomaszewski.notifications.NotificationsConfigTest.assertHaveDefaultProperties;


public class NotificationsAltConfigTest {

    @Test
    void shouldLoadConfigurationWithManySenders() {
        // given
        var yamlPropertiesFactory = new YamlPropertiesFactoryBean();
        yamlPropertiesFactory.setResources(new ClassPathResource("application-test.yml"));

        // when
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(
                new PropertiesPropertySource("my-properties", yamlPropertiesFactory.getObject()));
        context.register(NotificationsConfig.class);
        context.registerShutdownHook();
        context.refresh();
        NotificationsProperties properties = context.getBean(NotificationsProperties.class);

        // then
        Assertions.assertEquals(Set.of("sender1", "sender2"), properties.getSmtp().keySet());

        Config sender1 = properties.getSmtp().get("sender1");
        assertHaveDefaultProperties(sender1.properties());

        Config sender2 = properties.getSmtp().get("sender2");
        assertHaveDefaultProperties(sender2.properties());

        context.stop();
    }
}
