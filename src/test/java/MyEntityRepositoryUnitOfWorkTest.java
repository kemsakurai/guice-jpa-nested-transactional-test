import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.UnitOfWork;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * MyEntityRepositoryUnitOfWorkTest
 */
public class MyEntityRepositoryUnitOfWorkTest {

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
        UnitOfWork unitOfwork = injector.getInstance(UnitOfWork.class);
        unitOfwork.begin();
        try {
            MyEntityRepository repo = injector.getInstance(MyEntityRepository.class);
            repo.begin();
            repo.cleanTable();
            repo.commit();
        } finally {
            unitOfwork.end();
        }
    }

    @Test
    public void testPersistWithUnitOfWork1() {

        UnitOfWork unitOfwork = injector.getInstance(UnitOfWork.class);
        unitOfwork.begin();

        MyEntityRepository repo = injector.getInstance(MyEntityRepository.class);
        try {
            repo.begin();
            repo.notNestedPersistAndThrow();
            // commit はされない
            repo.commit();
        } catch (MyEntityRepository.MyRuntimeException ignored) {
        } finally {
            unitOfwork.end();
        }

        unitOfwork = injector.getInstance(UnitOfWork.class);
        unitOfwork.begin();
        try {
            repo = injector.getInstance(MyEntityRepository.class);
            repo.begin();
            assertNull(repo.findByName("Test 1"));
            assertNull(repo.findByName("Test 2"));
            repo.commit();
        } finally {
            unitOfwork.end();
        }

    }

    @Test
    public void testPersistWithUnitOfWork2() {
        UnitOfWork unitOfwork = injector.getInstance(UnitOfWork.class);
        unitOfwork.begin();
        MyEntityRepository repo = injector.getInstance(MyEntityRepository.class);
        try {
            repo.begin();
            repo.persist("Test 3");
            repo.persist("Test 4");
            repo.persist("Test 5");
            repo.commit();
        } catch (MyEntityRepository.MyRuntimeException ignored) {
        } finally {
            unitOfwork.end();
        }

        unitOfwork = injector.getInstance(UnitOfWork.class);
        unitOfwork.begin();
        repo = injector.getInstance(MyEntityRepository.class);
        try {
            repo.begin();
            assertEquals("Test 3", repo.findByName("Test 3").getName());
            assertEquals("Test 4", repo.findByName("Test 4").getName());
            assertEquals("Test 5", repo.findByName("Test 5").getName());
            repo.commit();
        } finally {
            unitOfwork.end();
        }
    }
}
