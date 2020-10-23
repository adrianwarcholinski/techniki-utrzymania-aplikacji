package pl.lodz.p.it.ssbd2020.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.SQLNonTransientConnectionException;
import java.util.List;
import java.util.Optional;

/**
 * Fasada zapewniająca dostęp do obiektów encji JPA.
 * Klasa pochodna musi zaimplementować metodę zwracającą obiekt menadżera encji.
 * Implementuje interfejs {@link BasicFacadeOperations}
 *
 * @param <T> typ encji, której obiekty są przetwarzane przez fasadę
 */
public abstract class AbstractFacade<T> implements BasicFacadeOperations<T> {

    public static final String CONSTRAINT_UNIQUE_LOGIN_ACCESS_LEVEL = "access_level_login_level_key";
    public static final String CONSTRAINT_UNIQUE_LOGIN = "account_login_key";
    public static final String CONSTRAINT_UNIQUE_EMAIL = "account_email_key";
    public static final String CONSTRAINT_UNIQUE_TOKEN = "expired_token_token_key";
    public static final String CONSTRAINT_UNIQUE_PHONE_NUMBER = "customer_data_phone_number_key";
    public static final String CONSTRAINT_UNIQUE_WORK_PHONE_NUMBER = "employee_data_work_phone_number_key";
    public static final String CONSTRAINT_UNIQUE_CARD_NUMBER = "admin_data_card_number_key";
    public static final String CONSTRAINT_UNIQUE_RESERVATION_NUMBER = "reservation_reservation_number_key";
    public static final String CONSTRAINT_UNIQUE_ALLEY_NAME = "alley_name_key";
    public static final String CONSTRAINT_UNIQUE_WEAPON_SERIAL_NUMBER = "weapon_serial_number_key";
    public static final String CONSTRAINT_UNIQUE_OPINION_NUMBER = "opinion_opinion_number_key";
    public static final String CONSTRAINT_UNIQUE_CUSTOMER_AND_MODEL = "opinion_customer_model_key";

    /**
     * Klasa encji, której obiekty są przetwarzane przez fasadę.
     */
    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Metoda zwracająca obiekt menadżera encji.
     *
     * @return obiekt menadżera encji
     */
    protected abstract EntityManager getEntityManager();

    @Override
    public void create(T entity) throws AppException {
        getEntityManager().persist(entity);
        getEntityManager().flush();
    }

    @Override
    public void edit(T entity) throws AppException {
        getEntityManager().merge(entity);
        getEntityManager().flush();
    }

    @Override
    public void remove(T entity) throws AppException {
        getEntityManager().remove(getEntityManager().merge(entity));
        getEntityManager().flush();
    }

    @Override
    public Optional<T> find(Object id) throws AppException {
        try {
            return Optional.ofNullable(getEntityManager().find(entityClass, id));
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof DatabaseException && cause.getCause() instanceof SQLNonTransientConnectionException) {
                throw new DatabaseConnectionProblemException(e);
            } else {
                throw new QueryProblemException(e);
            }
        }
    }

    @Override
    public List<T> findAll() throws AppException {
        try {
            CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            return getEntityManager().createQuery(cq).getResultList();
        } catch (PersistenceException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof DatabaseException && cause.getCause() instanceof SQLNonTransientConnectionException) {
                throw new DatabaseConnectionProblemException(e);
            } else {
                throw new QueryProblemException(e);
            }
        }
    }

    @Override
    public int count() throws AppException {
        try {
            CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(getEntityManager().getCriteriaBuilder().count(root));
            Query query = getEntityManager().createQuery(cq);
            return ((Long) query.getSingleResult()).intValue();
        } catch (PersistenceException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof DatabaseException && cause.getCause() instanceof SQLNonTransientConnectionException) {
                throw new DatabaseConnectionProblemException(e);
            } else {
                throw new QueryProblemException(e);
            }
        }
    }
}
