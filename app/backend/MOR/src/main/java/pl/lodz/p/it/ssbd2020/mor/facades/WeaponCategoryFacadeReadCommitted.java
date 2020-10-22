package pl.lodz.p.it.ssbd2020.mor.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.WeaponCategoryEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.WeaponCategoryFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import java.sql.SQLNonTransientConnectionException;
import java.util.List;
import java.util.Optional;

/**
 * Fasada udostępniająca operację dla obiektu encji {@link WeaponCategoryEntity}
 * rozszerza klasę {@link AbstractFacade}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class WeaponCategoryFacadeReadCommitted extends AbstractFacade<WeaponCategoryEntity> implements WeaponCategoryFacadeReadCommittedLocal {

    @PersistenceContext(unitName = "ssbd01morPU")
    private EntityManager em;

    public WeaponCategoryFacadeReadCommitted() {
        super(WeaponCategoryEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed("getAllWeaponCategories")
    public List<WeaponCategoryEntity> findAll() throws AppException {
        return super.findAll();
    }

    @Override
    @RolesAllowed({"addWeaponModel", "editWeaponModel"})
    public Optional<WeaponCategoryEntity> findByName(String name) throws AppException {
        try {
            TypedQuery<WeaponCategoryEntity> tq = em.createNamedQuery("WeaponCategoryEntity.findByName", WeaponCategoryEntity.class);
            tq.setParameter("name", name);
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