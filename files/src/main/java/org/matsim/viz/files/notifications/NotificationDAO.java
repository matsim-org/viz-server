package org.matsim.viz.files.notifications;

import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.files.entities.DAO;

import java.net.URI;
import java.time.Instant;
import java.util.List;

public class NotificationDAO extends DAO {


    public NotificationDAO(PersistenceUnit persistenceUnit) {
        super(persistenceUnit);
    }

    Subscription peristSubscription(Subscription subscription) {
        return database.persist(subscription);
    }

    NotificationType persistType(NotificationType type) {
        return database.persist(type);
    }

    Subscription findSubscription(NotificationType type, URI callback) {
        return database.executeQuery(query -> query.selectFrom(QSubscription.subscription)
                .where(QSubscription.subscription.type.eq(type)
                        .and(QSubscription.subscription.callback.eq(callback)))
                .fetchFirst()
        );
    }

    List<Subscription> findAllSubscriptionsForType(NotificationType type) {
        return database.executeQuery(query -> query.selectFrom(QSubscription.subscription)
                .where(QSubscription.subscription.type.eq(type)
                        .and(QSubscription.subscription.expiresAt.after(Instant.now()))
                ).fetch()
        );
    }

    NotificationType findType(String type) {

        QNotificationType types = QNotificationType.notificationType;
        return database.executeQuery(query -> query.selectFrom(types).where(types.name.eq(type)).fetchFirst());
    }

    public void removeAllNotificationTypes() {
        QNotificationType types = QNotificationType.notificationType;
        database.executeTransactionalQuery(query -> query.delete(types).execute());
    }

    public void removeAllSubscriptions() {
        QSubscription subscriptions = QSubscription.subscription;

        database.executeTransactionalQuery(query -> query.delete(subscriptions).execute());
    }

    void removeExpiredSubscriptions() {
        QSubscription subscriptions = QSubscription.subscription;
        database.executeTransactionalQuery(query -> query.delete(subscriptions)
                .where(subscriptions.expiresAt.before(Instant.now()))
                .execute());
    }
}
