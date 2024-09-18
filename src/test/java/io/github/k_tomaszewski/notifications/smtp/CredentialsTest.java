package io.github.k_tomaszewski.notifications.smtp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Properties;

public class CredentialsTest {

    @Test
    void shouldLoadCredentialsWhenOnlyPasswordInFile() throws IOException, URISyntaxException {
        // given
        File passFile = getResourceFile("pass.txt");
        var config = new Config("foo@bar.pl", new Properties(), passFile);

        // when
        Credentials.Data result = Credentials.loadCredentials(config);

        // then
        Assertions.assertEquals("foo@bar.pl", result.user());
        Assertions.assertEquals(Files.readString(getResourceFile("pass.txt").toPath()).lines().findFirst().orElseThrow(),
                result.password());
    }

    @Test
    void shouldLoadCredentialsWhenUserAndPasswordPresent() throws URISyntaxException, IOException {
        // given
        File passFile = getResourceFile("user_pass.txt");
        var config = new Config("foo@bar.pl", new Properties(), passFile);

        // when
        Credentials.Data result = Credentials.loadCredentials(config);

        // then
        Assertions.assertEquals("user@foo.pl", result.user());
        Assertions.assertEquals(Files.lines(getResourceFile("user_pass.txt").toPath()).skip(1).findFirst().get(), result.password());
    }

    private File getResourceFile(String resource) throws URISyntaxException {
        return new File(getClass().getClassLoader().getResource(resource).toURI());
    }
}
