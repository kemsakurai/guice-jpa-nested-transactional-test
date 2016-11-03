import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

/**
 * MyEntityRepository
 */
public class MyEntityRepository {

    @Inject
    private EntityManager em;

    @Transactional
    public MyEntity persist(String name) {
        MyEntity entity = new MyEntity();
        entity.setName(name);
        em.merge(entity);
        return entity;
    }

    public void begin() {
        EntityTransaction txn = em.getTransaction();
        txn.begin();
    }

    public void commit() {
        EntityTransaction txn = em.getTransaction();
        txn.commit();
    }

    @Transactional
    public MyEntity findById(Integer id) {
        MyEntity entity = em.find(MyEntity.class, (Object) id);
        return entity;
    }

    public MyEntity findByName(String name) {
        Query q = em.createQuery("SELECT m from MyEntity m where m.name = :name");
        q.setParameter("name", name);
        List<MyEntity> ents = q.getResultList();
        return ents.isEmpty() ? null : ents.get(0);
    }

    public void notNestedPersistAndThrow() {
        persist("Test 1");
        persist("Test 2");
        throwRuntimeException();
        persist("Test 3");
    }

    @Transactional
    public void nestedPersistAndThrow() {
        persist("Test 1");
        persist("Test 2");
        throwRuntimeException();
        persist("Test 3");
    }

    @Transactional
    public int cleanTable() {
        Query q = em.createQuery("Delete from MyEntity");
        return q.executeUpdate();
    }

    private void throwRuntimeException() {
        throw new MyRuntimeException();
    }

    public class MyRuntimeException extends RuntimeException {
        public MyRuntimeException() {
            super();
        }
    }
}
