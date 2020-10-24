package pl.lodz.p.it.ssbd2020.mok.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.CustomerEntity;
import pl.lodz.p.it.ssbd2020.exceptions.common.*;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.PhoneNumberIsAlreadyTakenException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mok.facades.interfaces.CustomerFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.util.Optional;

/**
 * Fasada udostępniająca operację dla obiektu encji {@link CustomerEntity}
 * rozszerza klasę {@link AbstractFacade} oraz implementuje interfejs {@link CustomerFacadeReadCommittedLocal}
 */
@Stateless(name = "MOKCustomerFacadeReadCommitted")
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class CustomerFacadeReadCommitted extends AbstractFacade<CustomerEntity> implements CustomerFacadeReadCommittedLocal {

    @PersistenceContext(unitName = "ssbd01mokPU")
    private EntityManager em;

    public CustomerFacadeReadCommitted() {
        super(CustomerEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public void create(CustomerEntity entity) throws AppException {
        try {
            super.create(entity);
        } catch (DatabaseException e) {
            if (e.getCause() instanceof SQLNonTransientConnectionException) {
                throw new DatabaseConnectionProblemException(e);
            } else {
                throw new QueryProblemException(e);
            }
        } catch (PersistenceException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DatabaseException) {
                if (cause.getMessage().contains(CONSTRAINT_UNIQUE_PHONE_NUMBER)) {
                    throw new PhoneNumberIsAlreadyTakenException(e);
                } else {
                    throw new QueryProblemException(e);
                }
            }
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException();
        }
    }

    @Override
    @RolesAllowed({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
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
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException();
        }
    }

    @Override
    @DenyAll
    public void remove(CustomerEntity entity) throws AppException {
            super.remove(entity);
    }

    @Override
    @PermitAll
    public Optional<CustomerEntity> findByPhoneNumber(String phoneNumber) throws AppException {
        TypedQuery<CustomerEntity> tq = em.createNamedQuery("CustomerEntity.findByPhoneNumber", CustomerEntity.class);
        tq.setParameter("phoneNumber", phoneNumber);
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