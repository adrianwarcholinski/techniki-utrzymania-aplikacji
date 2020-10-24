package pl.lodz.p.it.ssbd2020.mor.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InvalidInputException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.ReservationFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fasada udostępniająca operację dla obiektu encji {@link ReservationEntity}
 * rozszerza klasę {@link AbstractFacade} oraz implementuje  interfejs {@link }
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ReservationFacadeReadCommitted extends AbstractFacade<ReservationEntity> implements ReservationFacadeReadCommittedLocal {

    @PersistenceContext(unitName = "ssbd01morPU")
    private EntityManager em;

    public ReservationFacadeReadCommitted() {
        super(ReservationEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @DenyAll
    public void create(ReservationEntity reservationEntity) throws AppException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({"ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public void edit(ReservationEntity entity) throws AppException {
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
    @RolesAllowed({"ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public Optional<ReservationEntity> findByReservationNumber(long reservationNumber) {
        TypedQuery<ReservationEntity> tq = em.createNamedQuery("ReservationEntity.findByReservationNumber", ReservationEntity.class);
        tq.setParameter("reservationNumber", reservationNumber);
        try {
            return Optional.of(tq.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @RolesAllowed("ROLE_EMPLOYEE")
    public List<ReservationEntity> findAll(boolean getCanceled, boolean getPast) throws AppException {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ReservationEntity> cq = cb.createQuery(ReservationEntity.class);
        Root<ReservationEntity> reservation = cq.from(ReservationEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (!getCanceled)
            predicates.add(cb.equal(reservation.get("active"), true));
        if (!getPast)
            predicates.add(cb.greaterThan(reservation.get("endDate"), LocalDateTime.now()));
        cq.select(reservation).where(predicates.toArray(new Predicate[]{}));
        cq.orderBy(cb.asc(reservation.get("reservationNumber")));

        try {
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
    @RolesAllowed("ROLE_CUSTOMER")
    public List<ReservationEntity> findByCustomer(String login, boolean getCanceled, boolean getPast) throws AppException {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ReservationEntity> cq = cb.createQuery(ReservationEntity.class);
        Root<ReservationEntity> reservation = cq.from(ReservationEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(reservation.get("customer").get("account").get("login"), login));
        if (!getCanceled)
            predicates.add(cb.equal(reservation.get("active"), true));
        if (!getPast)
            predicates.add(cb.greaterThan(reservation.get("endDate"), LocalDateTime.now()));
        cq.select(reservation).where(predicates.toArray(new Predicate[]{}));
        cq.orderBy(cb.asc(reservation.get("reservationNumber")));
        try {
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
    @RolesAllowed({"ROLE_CUSTOMER"})
    public List<ReservationEntity> findConflictReservationsByWeaponModel(LocalDateTime startDate, LocalDateTime endDate,
                                                                         String weaponModelName, String alleyName) throws AppException {
        TypedQuery<ReservationEntity> tq = em.createNamedQuery("ReservationEntity.findConflictReservationsByWeaponModel", ReservationEntity.class);
        tq.setParameter("startDate", startDate);
        tq.setParameter("endDate", endDate);
        tq.setParameter("weaponModelName", weaponModelName);
        tq.setParameter("alleyName", alleyName);
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
    @RolesAllowed("ROLE_EMPLOYEE")
    public List<ReservationEntity> findConflictReservationsByWeapon(LocalDateTime startDate, LocalDateTime endDate, String weaponSerialNumber, String alleyName) throws AppException {
        TypedQuery<ReservationEntity> tq = em.createNamedQuery("ReservationEntity.findConflictReservationsByWeapon", ReservationEntity.class);
        tq.setParameter("startDate", startDate);
        tq.setParameter("endDate", endDate);
        tq.setParameter("weaponSerialNumber", weaponSerialNumber);
        tq.setParameter("alleyName", alleyName);
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


