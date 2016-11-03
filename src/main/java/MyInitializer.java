import com.google.inject.persist.PersistService;

import javax.inject.Inject;

/**
 * MyInitializer
 */
public class MyInitializer {

    PersistService service;

    @Inject
    MyInitializer(PersistService service) {
        this.service = service;
    }

    public void start() {
        service.start();
        // At this point JPA is started and ready.
    }

    public void stop() {
        service.stop();
    }
}

