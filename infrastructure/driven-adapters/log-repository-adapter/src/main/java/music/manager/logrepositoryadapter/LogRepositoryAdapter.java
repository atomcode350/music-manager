package music.manager.logrepositoryadapter;

import music.manager.model.log.gateways.LogRepository;
import org.springframework.stereotype.Component;

@Component
public class LogRepositoryAdapter implements LogRepository {

    @Override
    public void log(String message) {
        System.out.println(message);
    }
}
