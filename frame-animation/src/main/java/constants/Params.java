package constants;

public final class Params {

    public static final String ARG_NETWORK = "network";
    public static final String ARG_EVENTS = "events";
    public static final String ARG_PLANS = "plans";
    public static final String ARG_PERIOD = "snapshotPeriod";
    public static final String ARG_PORT = "port";

    public static final String RESPONSETYPE_JSON = "application/json";
    public static final String RESPONSETYPE_TEXT = "text/plain";
    public static final String RESPONSETYPE_OCTET_STREAM = "application/octet-stream";

    public static final int STATUS_OK = 200;
    public static final int STATUS_BADREQUEST = 400;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    private Params() {
    }
}
