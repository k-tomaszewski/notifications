package io.github.k_tomaszewski.notifications;

import io.github.k_tomaszewski.notifications.internal.BasicRetryStrategy;
import io.github.k_tomaszewski.notifications.internal.BasicSendingStrategy;
import io.github.k_tomaszewski.notifications.smtp.Config;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.InternetAddress;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import static io.github.k_tomaszewski.notifications.internal.MimeMessageUtil.toInternetAddress;

@ConfigurationProperties(prefix = "notifications")
public class NotificationsProperties {

    private static final String DEFAULT_ENTRY = "default";

    private boolean enabled = true;
    private String emailTo;
    private int queueCapacity = 1000;
    private String sendingStrategyClassName = BasicSendingStrategy.class.getName();
    private String retryStrategyClassName = BasicRetryStrategy.class.getName();
    private Map<String, Config> smtp = new LinkedHashMap<>();

    @PostConstruct
    public void applyDefaults() {
        Config smtpDefaults = smtp.remove(DEFAULT_ENTRY);
        if (smtpDefaults != null) {
            for (Config smtpConfig : smtp.values()) {
                applyDefaults(smtpConfig.properties(), smtpDefaults.properties());
            }
        }
    }

    private static void applyDefaults(Properties props, Properties defaults) {
        if (defaults != null) {
            for (String key : defaults.stringPropertyNames()) {
                if (!props.containsKey(key)) {
                    props.setProperty(key, defaults.getProperty(key));
                }
            }
        }
    }

    public InternetAddress getInternetAddressOfEmailTo() {
        Validate.notBlank(emailTo, "Parameter 'emailTo' not configured.");
        return toInternetAddress(emailTo);
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public SendingStrategy getSendingStrategy() {
        return createInstance(sendingStrategyClassName, "Invalid sendingStrategyClassName '%s'");
    }

    public RetryStrategy getRetryStrategy() {
        return createInstance(retryStrategyClassName, "Invalid retryStrategyClassName '%s'");
    }

    public Map<String, Config> getSmtp() {
        return smtp;
    }

    public void setQueueCapacity(int capacity) {
        this.queueCapacity = capacity;
    }

    public void setSendingStrategyClassName(String sendingStrategyClassName) {
        this.sendingStrategyClassName = sendingStrategyClassName;
    }

    public void setRetryStrategyClassName(String retryStrategyClassName) {
        this.retryStrategyClassName = retryStrategyClassName;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @SuppressWarnings("unchecked")
    private static <T> T createInstance(String className, String errorMessageTemplate) {
        try {
            return (T) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(errorMessageTemplate.formatted(className), e);
        }
    }
}
