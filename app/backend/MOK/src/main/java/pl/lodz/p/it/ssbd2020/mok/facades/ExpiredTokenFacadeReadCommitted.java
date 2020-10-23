package pl.lodz.p.it.ssbd2020.mok.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.ExpiredTokenEntity;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.ExpiredTokenException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mok.facades.interfaces.ExpiredTokenFacadeReadCommittedLocal;
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
 * Fasada udostępniająca operację dla obiektu encji {@link ExpiredTokenEntity}, która
 * rozszerza klasę {@link AbstractFacade} oraz implementuje interfejs {@link ExpiredTokenFacadeReadCommittedLocal}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ExpiredTokenFacadeReadCommitted extends AbstractFacade<ExpiredTokenEntity> implements ExpiredTokenFacadeReadCommittedLocal {

    @PersistenceContext(unitName = "ssbd01mokPU")
    private EntityManager em;

    public ExpiredTokenFacadeReadCommitted() {
        super(ExpiredTokenEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed("changeOwnEmail")
    public void create(ExpiredTokenEntity entity) throws AppException {
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
            if (cause instanceof DatabaseException && cause.getMessage().contains(CONSTRAINT_UNIQUE_TOKEN)) {
                throw new ExpiredTokenException(e);
            } else {
                throw new QueryProblemException(e);
            }
        }
    }

    @Override
    @DenyAll
    public void edit(ExpiredTokenEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    @DenyAll
    public void remove(ExpiredTokenEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed("changeOwnEmail")
    public Optional<ExpiredTokenEntity> findByToken(String token) throws AppException{
        TypedQuery<ExpiredTokenEntity> tq = em.createNamedQuery("ExpiredTokenEntity.findByToken", ExpiredTokenEntity.class);
        tq.setParameter("token", token);
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
