package org.matsim.webvis.files.notifications;

import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.files.entities.DAO;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO extends DAO {


    public NotificationDAO(PersistenceUnit persistenceUnit) {
        super(persistenceUnit);
    }

    Subscription peristSubscription(Subscription subscription) {
        return database.persist(subscription);
    }

    List<NotificationType> persistTypes(List<NotificationType> types) {
        return database.persistMany(types);
    }

    public List<Subscription> findSubscriptionsForNotificationType(NotificationType notificationType) {
        return new ArrayList<>();
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
                .where(QSubscription.subscription.type.eq(type)).fetch()
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
}
