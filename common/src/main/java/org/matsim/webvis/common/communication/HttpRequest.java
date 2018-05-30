package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.auth.ClientAuthentication;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.InternalException;

import java.io.IOException;

public class HttpRequest {

    private static final Logger logger = LogManager.getLogger();
    private static Gson gson = new Gson();

    public static <T> T authenticatedWithJsonResponse(HttpUriRequest request, HttpClientFactory clientFactory, Class<T> responseType, ClientAuthentication authentication) {

        request.addHeader("Authorization", "Bearer " + authentication.getAccessToken());
        try {
            return withJsonResponse(request, clientFactory, responseType);
        } catch (CodedException e) {
            if (e.getErrorCode().equals("unauthorized")) {
                authentication.requestAccessToken();
                request.setHeader("Authorization", "Bearer " + authentication.getAccessToken());
                return withJsonResponse(request, clientFactory, responseType);
            } else
                throw e;
        }
    }

    public static <T> T withJsonResponse(HttpUriRequest request, HttpClientFactory clientFactory, Class<T> responseType) {
        try (CloseableHttpClient client = clientFactory.createClient()) {
            logger.info("Making request to: " + request.getURI().toString());
            try (CloseableHttpResponse response = client.execute(request)) {
                if (!isStatusOk(response))
                    tryThrowWithErrorMessage(response);
                return gson.fromJson(EntityUtils.toString(response.getEntity()), responseType);
            }
        } catch (IOException e) {
            throw new InternalException("Error during http request");
        }
    }

    private static void tryThrowWithErrorMessage(HttpResponse response) {
        try {
            ErrorResponse error = gson.fromJson(EntityUtils.toString(response.getEntity()), ErrorResponse.class);
            throw new CodedException(error.getError(), error.getError_description());
        } catch (IOException | JsonSyntaxException e) {
            throw new InternalException("Error in http request.");
        }
    }

    private static boolean isStatusOk(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }
}
