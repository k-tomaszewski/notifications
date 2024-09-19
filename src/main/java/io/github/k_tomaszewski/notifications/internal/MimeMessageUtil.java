package io.github.k_tomaszewski.notifications.internal;

import io.github.k_tomaszewski.notifications.FatalNotificationException;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.event.Level;

public class MimeMessageUtil {

    private MimeMessageUtil() {
    }

    public static MimeMessage createMimeMessage(Notification src, InternetAddress emailFrom, InternetAddress emailTo, Session session) {
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(emailFrom);
            msg.addRecipient(Message.RecipientType.TO, emailTo);
            msg.setSubject((src.getLevel() != Level.INFO) ? String.format("[%s] %s", src.getLevel(), src.getTitle()) : src.getTitle());
            msg.setContent(src.getContent(), "text/html");
            msg.setHeader("Level", src.getLevel().name());
            if (src.getLevel() != Level.INFO) {
                msg.setHeader("Importance", toImportanceHeaderValue(src.getLevel()));
            }
            return msg;
        } catch (Exception e) {
            throw new FatalNotificationException("Cannot create MIME message", e);
        }
    }

    public static InternetAddress toInternetAddress(String email) {
        try {
            return new InternetAddress(email);
        } catch (AddressException e) {
            throw new RuntimeException("Invalid e-mail: '%s'".formatted(email), e);
        }
    }

    private static String toImportanceHeaderValue(Level level) {
        return switch (level) {
            case ERROR, WARN -> "High";
            case INFO -> "normal";
            case DEBUG, TRACE -> "low";
        };
    }
}
