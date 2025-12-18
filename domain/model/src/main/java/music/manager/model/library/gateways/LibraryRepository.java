package music.manager.model.library.gateways;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface LibraryRepository {

    List<String> getPlaylists() throws IOException;

    List<String> getPlaylistTracks(String playlistId) throws IOException, URISyntaxException;
}
