package music.manager.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import music.manager.model.library.gateways.LibraryRepository;
import music.manager.model.log.gateways.LogRepository;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class LibraryRestConsumer implements LibraryRepository
{
    private final String urlOrigin;
    private static final String accessToken = "pal"; // replace with your valid token
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final LogRepository logger;

    public LibraryRestConsumer(@Value("${adapter.restconsumer.library.url}") String urlOrigin, OkHttpClient client, ObjectMapper mapper, LogRepository logger) {
        this.urlOrigin = urlOrigin;
        this.client = client;
        this.mapper = mapper;
        this.logger = logger;
    }

    @CircuitBreaker(name = "spotify", fallbackMethod = "fallbackMethod")
    @Override
    public List<String> getPlaylists() throws IOException {
        var rootJsonNode = fetchWebApi(urlOrigin + "/v1/me/playlists", "GET", null);
        var playlists = toStream(rootJsonNode.get("items"))
                .map(p -> p.get("id").asText() + " - " + p.get("name").asText());
        return playlists.toList();
    }

    @CircuitBreaker(name = "spotify", fallbackMethod = "fallbackMethod")
    @Override
    public List<String> getPlaylistTracks(String playlistId) throws IOException {
        Stream<String> returned = Stream.empty();
        String next = urlOrigin + "/v1/playlists/" + playlistId + "/tracks";

        while (next != null) {
            var rootJsonNode = fetchWebApi(next, "GET", null);
            var tracks = toStream(rootJsonNode.get("items"))
                    .map(i -> {
                        var track = i.get("track");
                        String trackName = track.get("name").asText();
                        String artistNames = toStream(track.get("artists"))
                                .map(a -> a.get("name").asText())
                                .reduce((n1, n2) -> n1 + ", " + n2).orElse("");
                        return artistNames + " - " + trackName;
                    });
            returned = Stream.concat(returned, tracks);

            next = rootJsonNode.get("next").asText();
            System.out.println("Next !!!: " + next);
        }
        return returned.toList();
    }

    public JsonNode fetchWebApi(String url, String method, String bodyJson) throws IOException {

        var builder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
        ;

        if ("POST".equalsIgnoreCase(method)) {
            String contentType = "application/json";

            RequestBody requestBody = RequestBody.create(bodyJson, MediaType.parse(contentType));
            builder
                    .post(requestBody)
                    .addHeader("Content-Type", contentType);
        } else {
            builder.get();
        }

        Request request = builder.build();

        return callAndMap(request);
    }

    public String fallbackMethod(Exception e) {
        return "Fallback response due to: " + e.getMessage();
    }

    private JsonNode callAndMap(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (response.isSuccessful() && body != null && body.contentLength() > 0) {
                String jsonString = body.string();
                logger.log(jsonString);
                return mapper.readTree(jsonString);
            }
            throw new IOException(response.toString());
        }
    }

    private Stream<JsonNode> toStream(JsonNode arrayNode) {
        return StreamSupport.stream(arrayNode.spliterator(), false);
    }
}
