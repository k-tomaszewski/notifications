# Notifications
Simple Java library for sending human-readable notifications from an application to a system operator,
dedicated for hobbyists, with use of e-mail messages as transport.

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

Otherwise, these other solutions provide API too complex for a simple task of sending notifications.

That is why I've started this project.

## Design decisions
- Java 21
- Java Mail
- SMTP
- Spring Boot

## Usage
### Dependency
Add dependency to Notifications library (use the latest version):
```xml
<dependency>
  <groupId>io.github.k_tomaszewski</groupId>
  <artifactId>notifications</artifactId>
  <version>1.2.0</version>
</dependency>
```
As of now the library is published only in GiHub Packages repository, so you need to add additional Maven repository in your `pom.xml` like this:
```xml
<repositories>
    <repository>
        <id>github_k-tomaszewski_notifications</id>
        <url>https://maven.pkg.github.com/k-tomaszewski/notifications</url>
    </repository>
</repositories>
```
Moreover, it seems that GitHub Packages repository requires authentication even for public
artifacts, so you need to have a GitHub account and you need to setup your credentials
(GitHub username and access token) for Maven in the `settings.xml` file. Ref:
- https://maven.apache.org/guides/mini/guide-multiple-repositories.html
- https://maven.apache.org/guides/mini/guide-deployment-security-settings.html
- https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token

Example:
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>github_k-tomaszewski_notifications</id>
      <username>MY_GITHUB_USERNAME</username>
      <password>MY_GITHUB_ACCESS_TOKEN</password>
    </server>
  </servers>
</settings>
```
Please note that the server ID must be the same as repository ID configured in `pom.xml`.

### Spring Boot properties
In your Spring Boot properties file (usually `application.yml`) add configuration for
Notifications library. They are all under the key "notifications". The configuration
consists of specifying:
- Target e-mail address for receiving e-mails with notifications ("notifications.emailTo")
- Set of SMTP accounts used for sending notification e-mails ("notifications.smtp"). This set
cannot be empty and can have any number of items with arbitrary keys (like "sender1"). Each account 
is defined by sender e-mail address, Java Mail properties and credentials.

For now credentials can be provided only by specifying a path ("credentialsFile") to a text file containing one or two lines
of text. If only one line is present this is interpreted as a password and the username is assumed to be the same
as `emailFrom` property. If two lines of text are present then the first line is username
and the second line is the password.

Example:
```yaml
notifications:
  emailTo: target@somemail.com
  smtp:
    sender1:
      emailFrom: sender1@host1.pl
      properties:
        mail.smtp.host: smtp.foo.pl
        mail.smtp.port: 465
        mail.smtp.ssl.enable: true
      credentialsFile: /secret/resources/pass.txt
    sender2:
      emailFrom: sender2@host2.com
      properties:
        mail.smtp.host: smtp-mail.bar.com
        mail.smtp.port: 587
        mail.smtp.starttls.required: true
      credentialsFile: /secret/resources/user_pass.txt
    default:
      properties:
        mail.smtp.auth: true
        mail.smtp.ssl.protocols: TLSv1.2
        mail.smtp.connectiontimeout: 1000
        mail.smtp.timeout: 5000
```

### Spring Boot bean
Include the configuration class for Notifications library, `io.github.k_tomaszewski.notifications.NotificationsConfig`, into your application's Spring Boot configuration. Example:
```java
@Configuration
@Import(NotificationsConfig.class)
public class MyConfig {
  // ...
}
```
Then you can inject a Spring bean implementing `io.github.k_tomaszewski.notifications.NotificationService` interface, which is the one
you use for sending notifications.

### Disabling notifications
In certain cases, for example tests, it is handy to have notifications disabled. To disable notifications
you need to set following Spring Boot property:
```yaml
notifications.enabled: false
```
