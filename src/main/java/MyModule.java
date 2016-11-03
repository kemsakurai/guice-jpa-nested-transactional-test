import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

import javax.inject.Singleton;

/**
 * MyModule
 */
public class MyModule extends AbstractModule {
    @Override
    protected void configure() {
        // install jpaModule
        JpaPersistModule jpaPersistModule = new JpaPersistModule("myJpaUnit");
        this.install(jpaPersistModule);
        // bind JpaSericeInitializer as Singleton
        this.bind(MyInitializer.class).in(Singleton.class);
    }
}
