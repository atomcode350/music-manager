package music.manager.api;

import lombok.AllArgsConstructor;
import music.manager.usecase.library.PlaylistUseCase;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * API Rest controller.
 * 
 * Example of how to declare and use a use case:
 * <pre>
 * private final MyUseCase useCase;
 * 
 * public String commandName() {
 *     return useCase.execute();
 * }
 * </pre>
 */
@RestController
@RequestMapping(value = "/api/playlists", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class PlaylistApiRest {
    private final PlaylistUseCase playlistUseCase;

    @GetMapping
    public List<String> getPlaylists() throws IOException {
        return playlistUseCase.getPlaylists();
    }
}
