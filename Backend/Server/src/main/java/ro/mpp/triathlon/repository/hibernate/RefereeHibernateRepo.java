package ro.mpp.triathlon.repository.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ro.mpp.triathlon.model.Referee;
import ro.mpp.triathlon.repository.IRefereeRepo;

import java.util.List;

public class RefereeHibernateRepo implements IRefereeRepo {

    @Override
    public Referee findByUsernameAndPassword(String username, String password) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Referee where name = :n and password = :p", Referee.class)
                    .setParameter("n", username)
                    .setParameter("p", password)
                    .uniqueResult();
        }
    }

    @Override
    public void add(Referee element) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (element.getId() != null && element.getId() == 0) {
                element.setId(null);
            }

            session.persist(element);
            tx.commit();
        } catch (Exception e) {
            System.err.println("Eroare la adăugare: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void remove(Integer id) {
        executeTransaction(session -> {
            Referee referee = session.get(Referee.class, id);
            if (referee != null) session.remove(referee);
        });
    }

    @Override
    public void update(Referee element) {
        executeTransaction(session -> session.merge(element));
    }

    @Override
    public Referee findById(Integer id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.get(Referee.class, id);
        }
    }

    @Override
    public List<Referee> getAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Referee", Referee.class).list();
        }
    }

    @Override
    public void clear() {
        executeTransaction(session -> session.createMutationQuery("delete from Referee").executeUpdate());
    }

    private void executeTransaction(java.util.function.Consumer<Session> action) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            action.accept(session);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}