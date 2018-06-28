package org.matsim.webvis.auth.util;

import org.matsim.webvis.auth.config.AppConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

class TestAppConfiguration extends AppConfiguration {

    TestAppConfiguration() {
        try {
            //noinspection ConstantConditions
            this.tokenSigningKeyStore = URLDecoder.decode(
                    this.getClass().getClassLoader().getResource("keystore.jks").getFile(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.tokenSigningKeyStorePassword = "chocopause";
        this.tokenSigningKeyAlias = "selfsigned";
    }
}
