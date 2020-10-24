package pl.lodz.p.it.ssbd2020.mok.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.AccountEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.*;
import pl.lodz.p.it.ssbd2020.exceptions.mok.*;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mok.facades.interfaces.AccountFacadeReadCommittedLocal;
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
import java.util.List;
import java.util.Optional;


/**
 * Fasada udostępniająca operację dla obiektu encji {@link AccountEntity}
 * rozszerza klasę {@link AbstractFacade} oraz implementuje interfejs {@link AccountFacadeReadCommittedLocal}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccountFacadeReadCommitted extends AbstractFacade<AccountEntity> implements AccountFacadeReadCommittedLocal {

    @PersistenceContext(unitName = "ssbd01mokPU")
    private EntityManager em;


    public AccountFacadeReadCommitted() {
        super(AccountEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @PermitAll
    public void create(AccountEntity entity) throws AppException {
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
                if (cause.getMessage().contains(CONSTRAINT_UNIQUE_LOGIN)) {
                    throw new LoginIsAlreadyTakenException(e);
                } else if (cause.getMessage().contains(CONSTRAINT_UNIQUE_EMAIL)) {
                    throw new EmailIsAlreadyTakenException(e);
                } else if (cause.getMessage().contains(CONSTRAINT_UNIQUE_PHONE_NUMBER)) {
                    throw new PhoneNumberIsAlreadyTakenException(e);
                } else if (cause.getMessage().contains(CONSTRAINT_UNIQUE_WORK_PHONE_NUMBER)) {
                    throw new WorkPhoneNumberIsAlreadyTakenException(e);
                } else if (cause.getMessage().contains(CONSTRAINT_UNIQUE_CARD_NUMBER)) {
                    throw new CardNumberIsAlreadyTakenException(e);
                } else {
                    throw new QueryProblemException(e);
                }
            }
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException();
        }
    }

    @Override
    @PermitAll
    public void edit(AccountEntity entity) throws AppException {
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
                if (cause.getMessage().contains(CONSTRAINT_UNIQUE_EMAIL)) {
                    throw new EmailIsAlreadyTakenException(e);
                } else if (cause.getMessage().contains(CONSTRAINT_UNIQUE_PHONE_NUMBER)) {
                    throw new PhoneNumberIsAlreadyTakenException(e);
                } else if (cause.getMessage().contains(CONSTRAINT_UNIQUE_WORK_PHONE_NUMBER)) {
                    throw new WorkPhoneNumberIsAlreadyTakenException(e);
                } else if (cause.getMessage().contains(CONSTRAINT_UNIQUE_CARD_NUMBER)) {
                    throw new CardNumberIsAlreadyTakenException(e);
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
    public void remove(AccountEntity entity) throws AppException {
        super.remove(entity);
    }


    @Override
    @RolesAllowed({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public Optional<AccountEntity> find(Object id) throws AppException {
        return super.find(id);
    }


    @Override
    @PermitAll
    public Optional<AccountEntity> findByLogin(String login) throws AppException {
        TypedQuery<AccountEntity> tq = em.createNamedQuery("Account.findByLogin", AccountEntity.class);
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
    @PermitAll
    public Optional<AccountEntity> findByEmail(String email) throws AppException {
        TypedQuery<AccountEntity> tq = em.createNamedQuery("Account.findByEmail", AccountEntity.class);
        tq.setParameter("email", email);
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
    public List<AccountEntity> findByPhraseInFullName(String phrase) throws AppException {
        TypedQuery<AccountEntity> tq = em.createNamedQuery("Account.findByPhraseInFullName", AccountEntity.class);
        tq.setParameter("phrase", "%" + phrase + "%");
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
    @RolesAllowed("ROLE_ADMIN")
    public List<AccountEntity> findAuthenticated() throws AppException {
        TypedQuery<AccountEntity> tq = em.createNamedQuery("Account.findAuthenticated", AccountEntity.class);
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
    @RolesAllowed({"ROLE_ADMIN"})
    public List<AccountEntity> getAllSortedById() throws AppException {
        TypedQuery<AccountEntity> tq = em.createNamedQuery("Account.findAllSortedById", AccountEntity.class);
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