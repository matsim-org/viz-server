package requestHandling;


import java.util.Map;

public interface GetRequestHandler {
    Answer process(Map<String, String[]> parameters);
}
