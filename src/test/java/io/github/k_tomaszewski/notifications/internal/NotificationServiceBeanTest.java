package io.github.k_tomaszewski.notifications.internal;

import io.github.k_tomaszewski.notifications.NotificationsProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.event.Level;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.List;

public class NotificationServiceBeanTest {

    private final AutowireCapableBeanFactory beanFactoryMock = Mockito.mock(AutowireCapableBeanFactory.class);

    @BeforeEach
    void initBeanFactoryMock() {
        Mockito.when(beanFactoryMock.initializeBean(Mockito.any(), Mockito.anyString()))
                .then(invocationOnMock -> invocationOnMock.getArguments()[0]);
    }

    @Test
    void shouldCloseWhenAwaitingForNotifications() {
        // given
        Sender senderMock = Mockito.mock(Sender.class);

        var config = new NotificationsProperties();
        var notificationServiceBean = new NotificationServiceBean(config, List.of(senderMock), beanFactoryMock);

        // when
        notificationServiceBean.start();
        notificationServiceBean.stop();
    }

    @Test
    void shouldInvokeSenderUsingDefaultSendingStrategy() {
        // given
        Sender senderMock = Mockito.mock(Sender.class);

        var config = new NotificationsProperties();
        var notificationServiceBean = new NotificationServiceBean(config, List.of(senderMock), beanFactoryMock);

        // when
        notificationServiceBean.start();
        notificationServiceBean.send(Level.INFO, "Testowe info", "foo");
        notificationServiceBean.send(Level.WARN, "Testowe ostrzeżenie", "bar");

        // then
        var notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        Mockito.verify(senderMock, Mockito.timeout(5000).times(2)).send(notificationCaptor.capture());
        notificationServiceBean.stop();

        var notifications = notificationCaptor.getAllValues();
        Assertions.assertEquals(Level.INFO, notifications.get(0).getLevel());
        Assertions.assertEquals(Level.WARN, notifications.get(1).getLevel());
        Assertions.assertEquals("foo", notifications.get(0).getContent());
        Assertions.assertEquals("bar", notifications.get(1).getContent());
    }
}
