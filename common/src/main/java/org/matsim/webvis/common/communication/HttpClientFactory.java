package org.matsim.webvis.common.communication;

import org.apache.http.impl.client.CloseableHttpClient;

public interface HttpClientFactory {

    CloseableHttpClient createClient();
}
