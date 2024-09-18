package io.github.k_tomaszewski.notifications.smtp;

import org.apache.commons.lang3.Validate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

class Credentials {

    record Data(String user, String password) {
    }

    static Data loadCredentials(Config config) {
        Validate.notNull(config.credentialsFile(), "Credentials file not configured for %s", config.emailFrom());
        if (config.credentialsFile().getName().endsWith(".txt")) {
            return loadCredentialsFromTxt(config);
        }
        // NOTE add handling of other formats here, if needed
        throw new IllegalArgumentException(
                "Credentials file format for %s not recognized: %s".formatted(config.emailFrom(), config.credentialsFile().getName()));
    }

    private static Data loadCredentialsFromTxt(Config config) {
        try (var input = new FileInputStream(config.credentialsFile())) {
            List<String> tokens = new Scanner(input).useDelimiter("\\s+").tokens().toList();
            return switch (tokens.size()) {
                case 1 -> new Data(config.emailFrom(), tokens.getFirst());
                case 2 -> tokens.getLast().isEmpty() ? new Data(config.emailFrom(), tokens.getFirst())
                        : new Data(tokens.getFirst(), tokens.getLast());
                default -> throw new IllegalArgumentException(
                        "Text file with credentials for %s contains %d tokens".formatted(config.emailFrom(), tokens.size()));
            };
        } catch (IOException e) {
            throw new RuntimeException("Loading credentials for %s from text failed.".formatted(config.emailFrom()), e);
        }
    }

    private Credentials() {
    }
}
