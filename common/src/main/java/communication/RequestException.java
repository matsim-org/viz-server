package communication;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequestException extends Exception {

    private final String errorCode;

    public RequestException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
