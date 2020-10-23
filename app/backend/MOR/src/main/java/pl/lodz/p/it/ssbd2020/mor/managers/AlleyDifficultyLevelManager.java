package pl.lodz.p.it.ssbd2020.mor.managers;

import pl.lodz.p.it.ssbd2020.entities.AlleyDifficultyLevelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.AlleyDifficultyLevelDoesNotExistException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.NoAlleyDifficultyLevelsException;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.AlleyDifficultyLevelFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.AlleyDifficultyLevelManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.security.enterprise.SecurityContext;
import java.util.List;

/**
 * Menadżer odpowiedzialny za operacje na poziomach trudności torów.
 */
@Stateful
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AlleyDifficultyLevelManager implements AlleyDifficultyLevelManagerLocal {

    /**
     * Kontekst bezpieczeństwa, który zwraca informacje na temat zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na poziomach trudności torów, z poziomem izolacji read committed.
     **/
    @Inject
    private AlleyDifficultyLevelFacadeReadCommittedLocal alleyDifficultyLevelFacadeReadCommitted;

    /**
     * Pole reprezentujące wartość logiczna określająca, czy ostatnia transakcja w tym menadżerze została zakończona zatwierdzeniem.
     */
    private boolean isLastTransactionCommitted;

    /**
     * Pole reprezentujące identyfikator trwającej transakcji.
     */
    private String lastTransactionId;

    @Override
    public String getCurrentUser() {
        return securityContext.getCallerPrincipal() != null ? securityContext.getCallerPrincipal().getName() : "UNAUTHENTICATED";
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public boolean isLastTransactionRollback() {
        return !isLastTransactionCommitted;
    }

    @Override
    public void setLastTransactionCommitted(boolean committed) {
        isLastTransactionCommitted = committed;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public String getLastTransactionId() {
        return lastTransactionId;
    }

    @Override
    public void setLastTransactionId(String id) {
        lastTransactionId = id;
    }

    @Override
    @RolesAllowed({"getAllAlleyDifficultyLevels", "getAlleyDetails"})
    public List<AlleyDifficultyLevelEntity> getAllAlleyDifficultyLevels() throws AppException {
        List<AlleyDifficultyLevelEntity> alleyDifficultyLevelEntityList = alleyDifficultyLevelFacadeReadCommitted.findAll();
        if (alleyDifficultyLevelEntityList.isEmpty()) {
            throw new NoAlleyDifficultyLevelsException();
        }
        return alleyDifficultyLevelEntityList;
    }

    @Override
    @RolesAllowed({"editAlley"})
    public AlleyDifficultyLevelEntity findByName(String name) throws AppException {
        return alleyDifficultyLevelFacadeReadCommitted.findByName(name).orElseThrow(AlleyDifficultyLevelDoesNotExistException::new);
    }
}
