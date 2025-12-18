package music.manager.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;


public class AccessTokenRestConsumerTest {

    private static AccessTokenRestConsumer accessTokenRestConsumer;

    private static MockWebServer mockBackEnd;


    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        OkHttpClient client = new OkHttpClient.Builder().build();

        String url = mockBackEnd.url("url").toString();
        accessTokenRestConsumer = new AccessTokenRestConsumer(url, client, new ObjectMapper(), message -> {});
    }

    @AfterAll
    static void tearDown() throws IOException {

        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Validate method")
    void validateMethod() throws IOException {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody("{\"state\" : \"ok\"}"));

        var response = accessTokenRestConsumer.getAccessToken("");

        Assertions.assertEquals("{\"state\" : \"ok\"}", response);
    }
}