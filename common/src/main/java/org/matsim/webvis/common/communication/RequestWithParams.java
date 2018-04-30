package org.matsim.webvis.common.communication;

import spark.QueryParamsMap;

public abstract class RequestWithParams {

    protected String extractOptionalValue(String key, QueryParamsMap params) {
        if (params.hasKey(key)) {
            return params.get(key).value();
        }
        return "";
    }

    protected String extractRequiredValue(String key, QueryParamsMap params) throws RequestException {
        if (!params.hasKey(key))
            throw new RequestException(RequestError.INVALID_REQUEST, key + " missing");
        return params.get(key).value();
    }
}
