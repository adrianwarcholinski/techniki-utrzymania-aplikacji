package pl.lodz.p.it.ssbd2020.mor.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InvalidInputException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.WeaponWithSuchSerialNumberAlreadyExistsException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.WeaponFacadeReadCommittedLocal;
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
import java.util.List;

/**
 * Fasada udostępniająca operację dla obiektu encji {@link WeaponEntity}
 * rozszerza klasę {@link AbstractFacade} oraz implementuje interfejs {@link WeaponFacadeReadCommittedLocal}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class WeaponFacadeReadCommitted extends AbstractFacade<WeaponEntity> implements WeaponFacadeReadCommittedLocal {

    @PersistenceContext(unitName = "ssbd01morPU")
    private EntityManager em;

    public WeaponFacadeReadCommitted() {
        super(WeaponEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @DenyAll
    public void create(WeaponEntity entity) throws AppException {
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
                if (cause.getMessage().contains(CONSTRAINT_UNIQUE_WEAPON_SERIAL_NUMBER)) {
                    throw new WeaponWithSuchSerialNumberAlreadyExistsException(e);
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
    public void edit(WeaponEntity entity) throws AppException {
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
                    throw new QueryProblemException(e);
            }
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException();
        }
    }

    @Override
    @RolesAllowed("getAllActiveWeapons")
    public List<WeaponEntity> findByActive(boolean active) throws AppException {
        TypedQuery<WeaponEntity> tq = em.createNamedQuery("WeaponEntity.findByActive", WeaponEntity.class);
        tq.setParameter("active", active);
        try {
            return tq.getResultList();
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
    @RolesAllowed("getAllActiveWeaponsByModelName")
    public List<WeaponEntity> findByActiveAndWeaponModelName(boolean active, String weaponModelName) throws AppException {
        TypedQuery<WeaponEntity> tq = em.createNamedQuery("WeaponEntity.findByActiveAndModelName", WeaponEntity.class);
        tq.setParameter("active", active);
        tq.setParameter("weaponModelName", weaponModelName);
        try {
            return tq.getResultList();
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
