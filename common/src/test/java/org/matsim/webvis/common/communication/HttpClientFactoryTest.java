package org.matsim.webvis.common.communication;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.matsim.webvis.common.util.TestUtils;

import java.nio.file.Path;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

public class HttpClientFactoryTest {

    @Test(expected = RuntimeException.class)
    public void constructor_keystoreInitFails_runtimeException() {

        Path keystore = TestUtils.getKeystorePath();
        String password = "wrong-password";

        new HttpClientFactory(keystore, password.toCharArray());

        fail("invalid keystore parameter should cause exception");
    }

    @Test
    public void constructor_initSucceeds_instance() {

        HttpClientFactory factory = new HttpClientFactory(TestUtils.getKeystorePath(), TestUtils.getKeystorePassword());

        assertNotNull(factory);
    }

    @Test
    public void createClient_httpClientWithSslFactory() {

        HttpClientFactory factory = new HttpClientFactory(TestUtils.getKeystorePath(), TestUtils.getKeystorePassword());

        CloseableHttpClient client = factory.createClient();

        assertNotNull(client);
    }
}
