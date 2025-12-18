package music.manager.usecase.spotifyauth;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.util.Base64;

@RequiredArgsConstructor
public class SpotifyAuthUseCase {
    public static String generateAuthorizationUrl() {
        String clientId = "abc";
        String redirectUri = "https://www.google.com";
        String scopes = "playlist-read-private";
        String state = generateRandomString(16);

        return String.format(
                "https://accounts.spotify.com/authorize?response_type=code&client_id=%s&scope=%s&redirect_uri=%s&state=%s",
                clientId, scopes, redirectUri, state
        );
    }

    private static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[60];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, length);
    }
}