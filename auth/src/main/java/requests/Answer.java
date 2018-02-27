package requests;

public class Answer {

    private int statusCode;
    private String text;

    private Answer(int statusCode, String text) {
        this.statusCode = statusCode;
        this.text = text;
    }

    public static Answer ok(String message) {
        return new Answer(HttpStatus.STATUS_OK, message);
    }

    public static Answer badRequest(String errorMessage) {
        return new Answer(HttpStatus.STATUS_BADREQUEST, errorMessage);
    }

    public static Answer internalError(String errorMessage) {
        return new Answer(HttpStatus.STATUS_INTERNAL_SERVER_ERROR, errorMessage);
    }

    public static Answer unauthorized(String message) {
        return new Answer(HttpStatus.UNAUTHORIZED, message);
    }

    public static Answer forbidden(String message) {
        return new Answer(HttpStatus.FORBIDDEN, message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getText() {
        return text;
    }

    public boolean isOk() {
        return (this.statusCode == HttpStatus.STATUS_OK);
    }
}
