package music.manager.model.accesstoken.gateways;

import java.io.IOException;

public interface AccessTokenRepository {
    String getAccessToken(String authCode) throws IOException;
    String refreshAccessToken(String refreshToken) throws IOException;
}
