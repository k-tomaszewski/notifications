# Notifications
Simple Java library for sending human-readable notifications from an application to a system operator
dedicated for hobbyists with use of e-mail messages.

## Motivation
A hobbyist system is usually lacking a 24/7 dedicated monitoring team like employed for enterprise production systems.
Still a hobbyist system operator/owner needs a handy way to get informed what is happening with his/her system
without a need to look into system logs (which is time-consuming, so it should be avoided if not required). 
Some kind of notifications delivered to an operator/owner are needed.

Next, such notification solution should require minimal effort and cost to deploy and run.
E-mail messages are freely available for a long time, as one can create and use a mailbox account without 
any cost. Moreover, e-mail clients are freely available for different devices (PC, mobile phone) and 
different operating systems. This constitutes an ecosystem for simple and cheap delivery of notifications.

This library is trying to provide a production grade notifications solution that is based on e-mail
messages. Of course, there are Java libraries for sending e-mails, like implementations of Java Mail API or
https://github.com/bbottema/simple-java-mail, but they lack functionality related to resilience like:
- retrying
- redundancy

Otherwise, these other solutions provide to complex API for a simple task of sending a notifications.

That is why I've started this project.

## Design decisions
- Java 21
- Java Mail
- SMTP
- Spring Boot
