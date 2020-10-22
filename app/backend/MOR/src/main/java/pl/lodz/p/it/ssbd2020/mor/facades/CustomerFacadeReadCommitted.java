package pl.lodz.p.it.ssbd2020.mor.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.CustomerEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.PhoneNumberIsAlreadyTakenException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.CustomerFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import java.sql.SQLNonTransientConnectionException;
import java.util.Optional;

/**
 * Fasada udostępniająca operację dla obiektu encji {@link CustomerEntity}
 * rozszerza klasę {@link AbstractFacade} oraz implementuje interfejs {@link CustomerFacadeReadCommittedLocal}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class CustomerFacadeReadCommitted extends AbstractFacade<CustomerEntity> implements CustomerFacadeReadCommittedLocal {

    @PersistenceContext(unitName = "ssbd01morPU")
    private EntityManager em;

    public CustomerFacadeReadCommitted() {
        super(CustomerEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @DenyAll
    public void create(CustomerEntity entity) throws AppException {
        super.create(entity);
    }

    @Override
    @DenyAll
    public void edit(CustomerEntity entity) throws AppException {
        try {
            super.edit(entity);
        } catch (DatabaseException e) {
            if (e.getCause() instanceof SQLNonTransientConnectionException) {
                throw new DatabaseConnectionProblemException(e);
            } else {
                throw new QueryProblemException(e);
            }

        } catch (OptimisticLockException e) {
            throw new AppOptimisticLockException(e);
        } catch (PersistenceException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DatabaseException) {
                if (cause.getMessage().contains(CONSTRAINT_UNIQUE_PHONE_NUMBER)) {
                    throw new PhoneNumberIsAlreadyTakenException(e);
                } else {
                    throw new QueryProblemException(e);
                }
            }
        }
    }

    @Override
    @DenyAll
    public void remove(CustomerEntity entity) throws AppException {
        super.remove(entity);
    }

    @Override
    @RolesAllowed({"addOpinion"})
    public Optional<CustomerEntity> findByLogin(String login) throws AppException {
        TypedQuery<CustomerEntity> tq = em.createNamedQuery("CustomerEntity.findByLogin", CustomerEntity.class);
        tq.setParameter("login", login);
        try {
            return Optional.of(tq.getSingleResult());
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
}
