package io.github.k_tomaszewski.notifications.smtp;

import io.github.k_tomaszewski.notifications.FatalNotificationException;
import io.github.k_tomaszewski.notifications.internal.Notification;
import io.github.k_tomaszewski.notifications.internal.Sender;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;

import static io.github.k_tomaszewski.notifications.internal.MimeMessageUtil.createMimeMessage;
import static io.github.k_tomaszewski.notifications.internal.MimeMessageUtil.toInternetAddress;
import static io.github.k_tomaszewski.notifications.smtp.Credentials.loadCredentials;


public class EmailSender implements Sender {

    private final String name;
    private final InternetAddress emailFrom;
    private final InternetAddress emailTo;
    private final Session session;
    private final Transport transport;
    private final Runnable connectAction;

    public EmailSender(String name, Config config, InternetAddress emailTo) {
        this.name = name;
        var initResult = initSessionAndTransport(config, name);
        session = initResult.session();
        transport = initResult.transport();
        emailFrom = toInternetAddress(config.emailFrom());
        this.emailTo = emailTo;
        connectAction = createConnectAction(config);
    }

    @Override
    public void send(Notification input) {
        try {
            var msg = createMimeMessage(input, emailFrom, emailTo, session);
            if (!transport.isConnected()) {
                connectAction.run();
            }
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (MessagingException e) {
            throw new RuntimeException("Cannot send notification by SMTP (%s)".formatted(name), e);
        }
    }

    private static InitResult initSessionAndTransport(Config config, String name) {
        Session session = Session.getInstance(config.properties());
        Transport transport;
        try {
            transport = session.getTransport();
        } catch (NoSuchProviderException e) {
            throw new FatalNotificationException("E-mail transport initialization failed (%s).".formatted(name), e);
        }
        return new InitResult(session, transport);
    }

    private Runnable createConnectAction(Config config) {
        final var credentials = loadCredentials(config);
        return () -> {
            try {
                transport.connect(credentials.user(), credentials.password());
            } catch (AuthenticationFailedException e) {
                throw new FatalNotificationException("Authentication failed when connecting to SMTP server (%s).".formatted(name), e);
            } catch (MessagingException|RuntimeException e) {
                throw new RuntimeException("Connecting to SMTP server failed (%s).".formatted(name), e);
            }
        };
    }

    private record InitResult(Session session, Transport transport) {
    }
}
