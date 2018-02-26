package requests;

class ErrorResponse {

    private int statusCode;
    private String message;

    ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
