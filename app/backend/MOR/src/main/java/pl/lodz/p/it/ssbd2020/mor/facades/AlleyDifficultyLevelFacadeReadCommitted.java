package pl.lodz.p.it.ssbd2020.mor.facades;

import org.eclipse.persistence.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2020.entities.AlleyDifficultyLevelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.DatabaseConnectionProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.QueryProblemException;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.AlleyDifficultyLevelFacadeReadCommittedLocal;
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
 * Fasada udostępniająca operację dla obiektu encji {@link AlleyDifficultyLevelEntity}
 * rozszerza klasę {@link AbstractFacade} oraz implementuje  interfejs {@link AlleyDifficultyLevelFacadeReadCommittedLocal}
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AlleyDifficultyLevelFacadeReadCommitted extends AbstractFacade<AlleyDifficultyLevelEntity> implements AlleyDifficultyLevelFacadeReadCommittedLocal {

    @PersistenceContext(unitName = "ssbd01morPU")
    private EntityManager em;

    public AlleyDifficultyLevelFacadeReadCommitted() {
        super(AlleyDifficultyLevelEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed({"ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public List<AlleyDifficultyLevelEntity> findAll() throws AppException {
        return super.findAll();
    }

    @Override
    @RolesAllowed({"ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public Optional<AlleyDifficultyLevelEntity> findByName(String name) throws AppException {
        TypedQuery<AlleyDifficultyLevelEntity> tq = em.createNamedQuery("AlleyDifficultyLevelEntity.findByName", AlleyDifficultyLevelEntity.class);
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
