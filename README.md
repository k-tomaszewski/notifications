# Notifications
Simple Java library for sending human-readable notifications from an application to a system operator
dedicated for hobbyists with use of e-mail messages.

## Features
1. In-memory queue of notifications to be sent.
2. Requesting to send a notification is fast as the actual sending is done in a dedicated thread.
3. Requesting to send a notification is safe as no exceptions are thrown by the operation itself.
Your business logic will not be ruined by some problem with sending a notification.
4. Redundancy - you can configure many SMTP servers. If one is not available, another will be used.
5. Retrying - a notification sending is retried if error that occurred is not fatal.
6. Spring Boot ready.
7. Simple - you inject and use just one bean that implements `NotificationService` interface, which has one method: `send`.
8. Dependencies kept to minimum. Dependencies are already present for a typical Spring Boot based application.

## Motivation
A hobbyist system is usually lacking a 24/7 dedicated monitoring/operations team like employed for enterprise production systems.
Still a hobbyist system operator/owner needs a handy way to get informed what is happening with his/her system
without a need to look into system logs (which is time-consuming, so it should be avoided if not required). 
Some kind of notifications delivered to an operator/owner are needed.

Next, such notification solution should require minimal effort and cost to deploy and run.
E-mail messages are freely available for a long time, as one can create and use a mailbox account without 
any cost. Moreover, e-mail clients are freely available for different devices (PC, mobile phone, tablet) and 
different operating systems. This constitutes an ecosystem for simple and cheap delivery of notifications.

This library is trying to provide a production grade notifications solution that is based on e-mail
messages. Of course, there are Java libraries for sending e-mails, like implementations of Java Mail API or
https://github.com/bbottema/simple-java-mail, but they lack functionality related to resilience like:
- retrying
- redundancy

Otherwise, these other solutions provide API too complex for a simple task of sending a notifications.

That is why I've started this project.

## Design decisions
- Java 21
- Java Mail
- SMTP
- Spring Boot
