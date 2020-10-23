package pl.lodz.p.it.ssbd2020.mor.facades.serializable;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InvalidInputException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces.AlleyFacadeSerializableLocal;
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
import java.util.Optional;

/**
 * Fasada udostępniająca operację dla obiektu encji {@link AlleyEntity}
 * rozszerza klasę {@link AbstractFacade} oraz implementuje interfejs {@link AlleyFacadeSerializableLocal}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AlleyFacadeSerializable extends AbstractFacade<AlleyEntity> implements AlleyFacadeSerializableLocal {

    @PersistenceContext(unitName = "ssbd01morPU")
    private EntityManager em;

    public AlleyFacadeSerializable() {
        super(AlleyEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @DenyAll
    public List<AlleyEntity> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    @DenyAll
    public void create(AlleyEntity entity) throws AppException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({"removeAlley"})
    public void edit(AlleyEntity entity) throws AppException {
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
    @RolesAllowed({"removeAlley", "updateReservation", "updateOwnReservation", "makeReservation"})
    public Optional<AlleyEntity> findByName(String name) throws AppException {
        TypedQuery<AlleyEntity> tq = em.createNamedQuery("AlleyEntity.findByName", AlleyEntity.class);
        tq.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        tq.setParameter("name", name);
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
