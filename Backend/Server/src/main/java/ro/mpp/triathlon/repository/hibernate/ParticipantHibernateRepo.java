package ro.mpp.triathlon.repository.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ro.mpp.triathlon.model.Participant;
import ro.mpp.triathlon.repository.IParticipantRepo;

import java.util.List;

public class ParticipantHibernateRepo implements IParticipantRepo {

    @Override
    public List<Participant> getAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Participant", Participant.class).list();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Participant> findAllSortedByPoints() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Participant p order by p.totalPoints desc", Participant.class).list();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Participant findById(Integer id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.get(Participant.class, id);
        }
    }

    @Override
    public void add(Participant element) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (element.getId() != null && element.getId() == 0) {
                element.setId(null);
            }

            session.persist(element);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("Hibernate Error (add participant): " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(Participant element) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(element);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }

    @Override
    public void remove(Integer id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Participant p = session.get(Participant.class, id);
            if (p != null) session.remove(p);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }

    @Override
    public void clear() {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery("delete from Participant").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }
}