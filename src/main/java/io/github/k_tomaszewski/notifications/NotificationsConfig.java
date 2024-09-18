package io.github.k_tomaszewski.notifications;

import io.github.k_tomaszewski.notifications.internal.NotificationServiceBean;
import io.github.k_tomaszewski.notifications.internal.Sender;
import io.github.k_tomaszewski.notifications.smtp.Config;
import io.github.k_tomaszewski.notifications.smtp.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.Objects;

@Configuration
public class NotificationsConfig {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationsConfig.class);

    @Bean
    public NotificationsProperties notificationsProperties() {
        return new NotificationsProperties();
    }

    @Bean
    public NotificationService notificationService(NotificationsProperties config, AutowireCapableBeanFactory beanFactory) {
        return new NotificationServiceBean(config, createSenders(config), beanFactory);
    }

    private static List<? extends Sender>  createSenders(NotificationsProperties config) {
        var targetEmail = config.getInternetAddressOfEmailTo();
        return config.getSmtp().entrySet().stream()
                .map(smtpEntry -> createEmailSender(smtpEntry.getKey(), smtpEntry.getValue(), targetEmail))
                .filter(Objects::nonNull)
                .toList();
    }

    private static EmailSender createEmailSender(String name, Config smtpConfig, InternetAddress emailTo) {
        try {
            return new EmailSender(name, smtpConfig, emailTo);
        } catch (Exception e) {
            LOG.warn("Cannot create SMTP e-mail sender for {}", name, e);
            return null;
        }
    }
}