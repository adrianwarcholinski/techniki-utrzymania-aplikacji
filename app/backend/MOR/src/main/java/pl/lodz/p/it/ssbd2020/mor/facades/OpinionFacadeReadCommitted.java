package pl.lodz.p.it.ssbd2020.mor.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.OpinionEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InvalidInputException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.CustomerHasAlreadyAddedOpinionOnThisWeaponModelException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.OpinionNumberAlreadyExistsException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.OpinionFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import javax.validation.ConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.util.List;
import java.util.Optional;

/**
 * Fasada udostępniająca operację dla obiektu encji {@link OpinionEntity}
 * rozszerza klasę {@link AbstractFacade}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class OpinionFacadeReadCommitted extends AbstractFacade<OpinionEntity> implements OpinionFacadeReadCommittedLocal {

    @PersistenceContext(unitName = "ssbd01morPU")
    private EntityManager em;

    public OpinionFacadeReadCommitted() {
        super(OpinionEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed("addOpinion")
    public void create(OpinionEntity entity) throws AppException {
        try {
            super.create(entity);
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
                if (cause.getMessage().contains(CONSTRAINT_UNIQUE_OPINION_NUMBER)) {
                    throw new OpinionNumberAlreadyExistsException();
                } else if (cause.getMessage().contains(CONSTRAINT_UNIQUE_CUSTOMER_AND_MODEL)) {
                    throw new CustomerHasAlreadyAddedOpinionOnThisWeaponModelException();
                } else {
                    throw new QueryProblemException(e);
                }
            }
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException();
        }
    }

    @Override
    @RolesAllowed("editOpinion")
    public void edit(OpinionEntity entity) throws AppException {
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
            throw new QueryProblemException(e);
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException();
        }
    }

    @Override
    @RolesAllowed("removeOpinion")
    public void remove(OpinionEntity entity) throws AppException {
        try {
            super.remove(entity);
        } catch (DatabaseException e) {
            if (e.getCause() instanceof SQLNonTransientConnectionException) {
                throw new DatabaseConnectionProblemException(e);
            } else {
                throw new QueryProblemException(e);
            }
        }
    }

    @Override
    @RolesAllowed("editOpinion")
    public Optional<OpinionEntity> find(Object id) throws AppException {
        return super.find(id);
    }

    @Override
    @RolesAllowed("addOpinion")
    public List<OpinionEntity> findAll() throws AppException {
        return super.findAll();
    }

    @Override
    @RolesAllowed("removeOpinion")
    public Optional<OpinionEntity> findByOpinionNumber(long opinionNumber) throws AppException {
        TypedQuery<OpinionEntity> tq = em.createNamedQuery("OpinionEntity.findByOpinionNumber", OpinionEntity.class);
        tq.setParameter("opinionNumber", opinionNumber);
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
    @RolesAllowed("getOwnOpinionForWeaponModel")
    public Optional<OpinionEntity> findByWeaponModelAndCustomerLogin(String weaponModelName, String login) throws AppException {
        TypedQuery<OpinionEntity> tq = em.createNamedQuery("OpinionEntity.findByWeaponModelNameAndCustomer", OpinionEntity.class);
        tq.setParameter("weaponModelName", weaponModelName);
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