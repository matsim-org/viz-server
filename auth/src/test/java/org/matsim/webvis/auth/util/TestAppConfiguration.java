package org.matsim.webvis.auth.util;

import org.matsim.webvis.auth.config.AppConfiguration;

import java.net.URISyntaxException;

public class TestAppConfiguration extends AppConfiguration {

    TestAppConfiguration() {
        try {
            this.tokenSigningKeyStore = this.getClass().getClassLoader().getResource("keystore.jks").toURI().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.tokenSigningKeyStorePassword = "123456";
        this.tokenSigningKeyAlias = "selfsigned";
    }
}
