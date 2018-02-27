package token;


import java.util.Map;

public class TokenRequest {
    public Map<String, String> parameters;

    TokenRequest(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
