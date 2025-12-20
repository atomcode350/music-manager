package music.manager.model.library.gateways;

import java.io.IOException;
import java.util.List;

public interface PlaylistRepository {

    List<String> getPlaylists() throws IOException;
}
