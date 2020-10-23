package pl.lodz.p.it.ssbd2020.mor.facades.serializable;

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
import pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces.WeaponFacadeSerializableLocal;
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
 * Fasada udostępniająca operację dla obiektu encji {@link WeaponEntity}
 * rozszerza klasę {@link AbstractFacade} oraz implementuje interfejs {@link WeaponFacadeSerializableLocal}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class WeaponFacadeSerializable extends AbstractFacade<WeaponEntity> implements WeaponFacadeSerializableLocal {

    @PersistenceContext(unitName = "ssbd01morPU")
    private EntityManager em;

    public WeaponFacadeSerializable() {
        super(WeaponEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed("createWeapon")
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
    @RolesAllowed({"removeWeapon"})
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
    @RolesAllowed({"removeWeapon", "updateReservation", "createWeapon", "makeReservation"})
    public Optional<WeaponEntity> findBySerialNumber(String serialNumber) throws AppException {
        TypedQuery<WeaponEntity> tq = em.createNamedQuery("WeaponEntity.findBySerial", WeaponEntity.class);
        tq.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        tq.setParameter("serialNumber", serialNumber);
        try {
            return Optional.of(tq.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}
