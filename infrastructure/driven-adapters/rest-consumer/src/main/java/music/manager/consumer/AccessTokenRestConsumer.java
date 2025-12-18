package music.manager.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import music.manager.model.accesstoken.gateways.AccessTokenRepository;
import music.manager.model.log.gateways.LogRepository;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
public class AccessTokenRestConsumer implements AccessTokenRepository
{
    private final String urlOrigin;
    private final String urlPathAccessToken = "/api/token";
    private final String clientId = "abc";
    private final String clientSecret = "efg";
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final LogRepository logger;

    public AccessTokenRestConsumer(@Value("${adapter.restconsumer.accesstoken.url}") String urlOrigin, OkHttpClient client, ObjectMapper mapper, LogRepository logger) {
        this.urlOrigin = urlOrigin;
        this.client = client;
        this.mapper = mapper;
        this.logger = logger;
    }

    @CircuitBreaker(name = "spotify", fallbackMethod = "fallbackMethod")
    @Override
    public String getAccessToken(String authCode) throws IOException {
        String body = "grant_type=authorization_code"
                + "&code=" + authCode
                + "&redirect_uri=https://www.google.com"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret;

        String contentType = "application/x-www-form-urlencoded; charset=utf-8";
        RequestBody requestBody = RequestBody.create(body, MediaType.parse(contentType));

        Request request = new Request.Builder()
            .url(urlOrigin + urlPathAccessToken)
            .post(requestBody)
            .addHeader("Content-Type", contentType)
            .build();

        return callAndMap(request, String.class);
    }

    @CircuitBreaker(name = "spotify", fallbackMethod = "fallbackMethod")
    @Override
    public String refreshAccessToken(String refreshToken) throws IOException {

        String credentials = Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes());

        String body = "grant_type=refresh_token&refresh_token=" + refreshToken;
        String contentType = "application/x-www-form-urlencoded; charset=utf-8";
        RequestBody requestBody = RequestBody.create(body, MediaType.parse(contentType));

        Request request = new Request.Builder()
                .url(urlOrigin + urlPathAccessToken)
                .post(requestBody)
                .addHeader("Authorization", "Basic " + credentials)
                .addHeader("Content-Type",contentType)
                .build();

        return callAndMap(request, String.class);
    }

    public String fallbackMethod(Exception e) {
        return "Fallback response due to: " + e.getMessage();
    }

    private <T> T callAndMap(Request request, Class<T> clazz) throws IOException {
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String json = response.body().string();
            logger.log(json);
            return mapper.readValue(json, clazz);
        }
        throw new IOException(response.toString());
    }
}
