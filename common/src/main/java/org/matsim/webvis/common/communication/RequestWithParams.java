package org.matsim.webvis.common.communication;

import org.matsim.webvis.common.errorHandling.InvalidInputException;
import spark.QueryParamsMap;

public abstract class RequestWithParams {

    protected String extractOptionalValue(String key, QueryParamsMap params) {
        if (params.hasKey(key)) {
            return params.get(key).value();
        }
        return "";
    }

    protected String extractRequiredValue(String key, QueryParamsMap params) {
        if (!params.hasKey(key))
            throw new InvalidInputException(key + " missing");
        return params.get(key).value();
    }
}
