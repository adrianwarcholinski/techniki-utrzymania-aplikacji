package pl.lodz.p.it.ssbd2020.mor.facades.serializable;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InvalidInputException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.WeaponModelWithSuchNameExistsException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces.WeaponModelFacadeSerializableLocal;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import javax.validation.ConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.util.List;
import java.util.Optional;

/**
 * Fasada udostępniająca operację dla obiektu encji {@link WeaponModelEntity}
 * rozszerza klasę {@link AbstractFacade} oraz implementuje interfejs {@link WeaponModelFacadeSerializableLocal}
 */
@Named("WeaponModelFacadeSerializable")
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class WeaponModelFacadeSerializable extends AbstractFacade<WeaponModelEntity> implements WeaponModelFacadeSerializableLocal {

    @PersistenceContext(unitName = "ssbd01morPU")
    private EntityManager em;

    public static final String CONSTRAINT_UNIQUE_NAME = "weapon_model_name_key";

    public WeaponModelFacadeSerializable() {
        super(WeaponModelEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed("ROLE_EMPLOYEE")
    public void edit(WeaponModelEntity entity) throws AppException {
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
                if (cause.getMessage().contains(CONSTRAINT_UNIQUE_NAME)) {
                    throw new WeaponModelWithSuchNameExistsException(e);
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
    public void create(WeaponModelEntity entity) throws AppException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({"updateOwnReservation", "createWeapon", "makeReservation", "ROLE_EMPLOYEE"})
    public Optional<WeaponModelEntity> findByName(String name) throws AppException {
        TypedQuery<WeaponModelEntity> tq = em.createNamedQuery("WeaponModelEntity.findByName", WeaponModelEntity.class);
        tq.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        tq.setParameter("name", name);
        try {
            return Optional.of(tq.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (DatabaseException e) {
            if (e.getCause() instanceof SQLNonTransientConnectionException) {
                throw new DatabaseConnectionProblemException(e);
            } else {
                throw new QueryProblemException(e);
            }
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