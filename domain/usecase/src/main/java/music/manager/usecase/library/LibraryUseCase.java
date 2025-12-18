package music.manager.usecase.library;

import lombok.RequiredArgsConstructor;
import music.manager.model.library.gateways.LibraryRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RequiredArgsConstructor
public class LibraryUseCase {
    private final LibraryRepository libraryRepository;

    public List<String> getPlaylists() throws IOException {
        return libraryRepository.getPlaylists();
    }

    public List<String> getPlaylistTracks(String playlistId) throws IOException, URISyntaxException {
        return libraryRepository.getPlaylistTracks(playlistId);
    }
}
