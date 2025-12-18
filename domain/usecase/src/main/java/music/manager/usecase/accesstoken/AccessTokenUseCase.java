package music.manager.usecase.accesstoken;

import lombok.RequiredArgsConstructor;
import music.manager.model.accesstoken.gateways.AccessTokenRepository;

import java.io.IOException;

@RequiredArgsConstructor
public class AccessTokenUseCase {
    private final AccessTokenRepository accessTokenRepository;

    public String getAccessToken(String authCode) throws IOException {
        return accessTokenRepository.getAccessToken(authCode);
    }

    public String refreshAccessToken(String refreshToken) throws IOException {
        return accessTokenRepository.refreshAccessToken(refreshToken);
    }
}
