package music.manager.usecase.library;

import lombok.RequiredArgsConstructor;
import music.manager.model.library.gateways.PlaylistRepository;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class PlaylistUseCase {
    private final PlaylistRepository playlistRepository;

    public List<String> getPlaylists() throws IOException {
        return playlistRepository.getPlaylists();
    }
}
