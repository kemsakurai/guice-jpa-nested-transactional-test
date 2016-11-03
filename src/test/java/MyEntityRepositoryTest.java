import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * MyEntityRepositoryTest
 */
public class MyEntityRepositoryTest {

    private static Injector injector;

    @BeforeClass
    public static void setUpInjector() {
        List<AbstractModule> modules = new ArrayList<>();
        modules.add(new MyModule());
        injector = Guice.createInjector(modules);
        MyInitializer myInitializer = injector.getInstance(MyInitializer.class);
        myInitializer.start();
    }

    @AfterClass
    public static void discardInjector() {
        injector = null;
    }

    @Before
    public void cleanTable() {
        MyEntityRepository repo = injector.getInstance(MyEntityRepository.class);
        repo.cleanTable();
    }

    @Test
    public void testNestedPersistAndTrow() {

        MyEntityRepository repo = injector.getInstance(MyEntityRepository.class);
        try {
            repo.nestedPersistAndThrow();
        } catch (MyEntityRepository.MyRuntimeException ignored) {
        }
        repo = injector.getInstance(MyEntityRepository.class);
        assertNull(repo.findByName("Test 1"));
        assertNull(repo.findByName("Test 2"));
    }

    @Test
    public void testNotNestedPersistAndTrow() {

        MyEntityRepository repo = injector.getInstance(MyEntityRepository.class);
        try {
            repo.notNestedPersistAndThrow();
        } catch (MyEntityRepository.MyRuntimeException ignored) {
        }
        repo = injector.getInstance(MyEntityRepository.class);
        assertEquals("Test 1", repo.findByName("Test 1").getName());
        assertEquals("Test 2", repo.findByName("Test 2").getName());
    }
}
