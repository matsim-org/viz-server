package org.matsim.webvis.files.notifications;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.error.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.RollbackException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Notifier {

    private static Logger logger = LoggerFactory.getLogger(Notifier.class);
    private static ExecutorService scheduler = Executors.newSingleThreadExecutor();

    private final Client client;
    private final NotificationDAO dao;

    public Notifier(Client client, NotificationDAO dao) {
        this.client = client;
        this.dao = dao;
    }

    public List<NotificationType> createNotificationTypes(List<NotificationType> possibleTypes) {

        return possibleTypes.stream().map(type -> {
            try {
                return this.dao.persistType(type);
            } catch (RollbackException e) {
                return this.dao.findType(type.getName());
            }
        }).collect(Collectors.toList());
    }

    Subscription createSubscription(String type, URI callback) {

        if (StringUtils.isBlank(type) || callback == null)
            throw new InvalidInputException("type and callback must be supplied");

        Instant expiresAt = Instant.now().plus(Duration.ofDays(1));
        NotificationType notificationType = dao.findType(type);

        try {
            Subscription subscription = dao.findSubscription(notificationType, callback);
            if (subscription == null) {
                subscription = new Subscription(notificationType, callback, expiresAt);
            } else {
                subscription.setExpiresAt(expiresAt);
            }

            return this.dao.peristSubscription(subscription);
        } catch (RollbackException | IllegalArgumentException e) {
            throw new InvalidInputException("Valid Notification type and callback must be supplied");
        }
    }

    public void dispatchAsync(Notification notification) {

        scheduler.execute(() -> this.dispatch(notification));
    }

    void dispatch(Notification notification) {

        NotificationType type = dao.findType(notification.getType());
        List<Subscription> subscriptions = dao.findAllSubscriptionsForType(type);
        subscriptions.forEach(sub -> {
            try {
                client.target(sub.getCallback()).request().post(Entity.json(notification));
            } catch (Exception e) {
                logger.info("Failed to dispatch notification to " + sub.getCallback().toString() + " with message: " + e.getMessage());
            }
        });
    }
}
