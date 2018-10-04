package org.matsim.webvis.files.notifications;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.webvis.error.InvalidInputException;
import org.matsim.webvis.files.util.TestUtils;

import javax.ws.rs.client.Client;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.*;

public class NotifierTest {

    private static Client jerseyClient = new JerseyClientBuilder().build();
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();
    private Notifier testObject;

    private static List<NotificationType> getSomeTypes() {
        List<NotificationType> result = new ArrayList<>();
        result.add(new NotificationType("first"));
        result.add(new NotificationType("second"));
        return result;
    }

    @Before
    public void setUp() {
        this.testObject = new Notifier(jerseyClient, new NotificationDAO(TestUtils.getPersistenceUnit()));
        TestUtils.getNotificationDAO().persistTypes(getSomeTypes());
    }

    @After
    public void tearDown() {
        TestUtils.removeSubscriptions();
        TestUtils.removeNotificationTypes();
    }

    @Test
    public void createNotificationTypes() {

        List<NotificationType> types = new ArrayList<>();
        types.add(new NotificationType("some"));
        types.add(new NotificationType("type"));

        List<NotificationType> persisted = testObject.createNotificationTypes(types);

        assertEquals(types.size(), persisted.size());
        persisted.forEach(type -> assertNotNull(type.getId()));
    }

    @Test
    public void createSubscription_alreadySubscribed_newExpiryDate() {

        final String type = "first";
        final URI callback = URI.create("http://some.uri");

        Subscription firstAttempt = testObject.createSubscription(type, callback);
        Subscription secondAttempt = testObject.createSubscription(type, callback);

        assertTrue(secondAttempt.getExpiresAt().isAfter(firstAttempt.getExpiresAt()));
    }

    @Test(expected = InvalidInputException.class)
    public void createSubscriptions_noCallback_exception() {

        testObject.createSubscription("first", null);

    }

    @Test(expected = InvalidInputException.class)
    public void createSubscriptions_noNotificationType_exception() {


        testObject.createSubscription(null, URI.create("http://some.uri"));
    }

    @Test(expected = InvalidInputException.class)
    public void createSubscriptions_notificationTypeDoesntExsist_exception() {

        testObject.createSubscription("type-that-doesnt-exist", URI.create("http://some.uri"));
    }

    @Test
    public void createSubscription_allGood_subscription() {


        final String type = "first";
        final URI callback = URI.create("http://some.uri");
        Subscription result = testObject.createSubscription(type, callback);

        assertEquals(type, result.getType().getName());
        assertEquals(callback, result.getCallback());
        // the subscription should expire in the future (more than a few milliseconds after creation)
        assertTrue(result.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    public void dispatch_everyoneIsNotified() {


        final String type = "first";
        final URI callback = URI.create("http://localhost:" + wireMockRule.port() + "/callback");
        final URI otherCallback = URI.create("http://localhost:" + wireMockRule.port() + "/other-callback");

        wireMockRule.stubFor(post(WireMock.anyUrl()));
        testObject.createSubscription(type, callback);
        testObject.createSubscription(type, otherCallback);

        testObject.dispatch(new Notification() {
            @Override
            public String getType() {
                return "first";
            }

            @Override
            public String getMessage() {
                return "test";
            }
        });

        wireMockRule.verify(postRequestedFor(urlEqualTo(callback.getPath())));
        wireMockRule.verify(postRequestedFor(urlEqualTo(otherCallback.getPath())));
    }

}
