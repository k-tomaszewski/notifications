package io.github.k_tomaszewski.notifications.smtp;

import java.io.File;
import java.util.Properties;

public record Config(
        String emailFrom,
        Properties properties,
        File credentialsFile) {
}
