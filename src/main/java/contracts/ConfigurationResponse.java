package contracts;

public class ConfigurationResponse {

    private String id;
    private RectContract bounds;

    public ConfigurationResponse(String id, RectContract bounds) {
        this.id = id;
        this.bounds = bounds;
    }

    public String getId() {
        return id;
    }

    public RectContract getBounds() {
        return bounds;
    }
}
