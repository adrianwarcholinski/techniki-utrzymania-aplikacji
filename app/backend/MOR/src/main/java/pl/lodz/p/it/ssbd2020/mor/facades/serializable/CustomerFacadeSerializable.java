package pl.lodz.p.it.ssbd2020.mor.facades.serializable;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.CustomerEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.CustomerFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces.CustomerFacadeSerializableLocal;
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
 * Fasada udostępniająca operację dla obiektu encji {@link CustomerEntity}
 * rozszerza klasę {@link AbstractFacade} oraz implementuje interfejs {@link CustomerFacadeSerializableLocal}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class CustomerFacadeSerializable extends AbstractFacade<CustomerEntity> implements CustomerFacadeSerializableLocal {

    @PersistenceContext(unitName = "ssbd01morPU")
    private EntityManager em;

    public CustomerFacadeSerializable() {
        super(CustomerEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @DenyAll
    public void create(CustomerEntity entity) throws AppException {
        super.create(entity);
    }

    @Override
    @DenyAll
    public void edit(CustomerEntity entity) throws AppException {
        throw new UnsupportedOperationException();
    }

    @Override
    @DenyAll
    public void remove(CustomerEntity entity) throws AppException {
        super.remove(entity);
    }

    @Override
    @RolesAllowed({"ROLE_CUSTOMER"})
    public Optional<CustomerEntity> findByLogin(String login) throws AppException {
        TypedQuery<CustomerEntity> tq = em.createNamedQuery("CustomerEntity.findByLogin", CustomerEntity.class);
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
