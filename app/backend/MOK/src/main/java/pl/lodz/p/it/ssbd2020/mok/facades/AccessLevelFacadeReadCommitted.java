package pl.lodz.p.it.ssbd2020.mok.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.AccessLevelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InvalidInputException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.AccessLevelAlreadyExistsException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mok.facades.interfaces.AccessLevelFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import javax.validation.ConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.util.Optional;


/**
 * Fasada udostępniająca operację dla obiektu encji {@link AccessLevelEntity}, która
 * rozszerza klasę {@link AbstractFacade} oraz implementuje interfejs {@link AccessLevelFacadeReadCommittedLocal}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccessLevelFacadeReadCommitted extends AbstractFacade<AccessLevelEntity> implements AccessLevelFacadeReadCommittedLocal {

    @PersistenceContext(unitName = "ssbd01mokPU")
    private EntityManager em;

    public AccessLevelFacadeReadCommitted() {
        super(AccessLevelEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public void create(AccessLevelEntity entity) throws AppException {
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
            if (cause instanceof DatabaseException && cause.getMessage().contains(CONSTRAINT_UNIQUE_LOGIN_ACCESS_LEVEL)) {
                throw new AccessLevelAlreadyExistsException(e);
            } else {
                throw new QueryProblemException(e);
            }
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException();
        }
    }

    @Override
    @RolesAllowed({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public void edit(AccessLevelEntity entity) throws AppException {
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
            if (cause instanceof DatabaseException && cause.getMessage().contains(CONSTRAINT_UNIQUE_LOGIN_ACCESS_LEVEL)) {
                throw new AccessLevelAlreadyExistsException(e);
            } else {
                throw new QueryProblemException(e);
            }
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException();
        }
    }

    @Override
    @DenyAll
    public void remove(AccessLevelEntity entity) throws AppException {
        super.remove(entity);
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public Optional<AccessLevelEntity> findByAccessLevelAndLogin(String accessLevel, String login) throws AppException {
        TypedQuery<AccessLevelEntity> tq = em.createNamedQuery("AccessLevelEntity.findByAccessLevelAndLogin", AccessLevelEntity.class);
        tq.setParameter("level", accessLevel);
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

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public Optional<Long> countAccountRoles(String login) throws AppException {
        TypedQuery<Long> tq = em.createNamedQuery("AccessLevelEntity.countAccountRoles", Long.class);
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