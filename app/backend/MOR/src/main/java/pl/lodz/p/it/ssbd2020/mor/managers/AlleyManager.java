package pl.lodz.p.it.ssbd2020.mor.managers;

import pl.lodz.p.it.ssbd2020.entities.AlleyDifficultyLevelEntity;
import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.*;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.AlleyDifficultyLevelFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.AlleyFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces.AlleyFacadeSerializableLocal;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.AlleyManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.ReservationUtils;
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
 * Menadżer odpowiedzialny za operacje na torach.
 */
@Stateful
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AlleyManager implements AlleyManagerLocal {

    /**
     * Kontekst bezpieczeństwa, który zwraca informacje na temat zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na torach, z poziomem izolacji read committed
     **/
    @Inject
    private AlleyFacadeReadCommittedLocal alleyFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na torach, z poziomem izolacji read committed.
     */
    @Inject
    private AlleyFacadeSerializableLocal alleyFacadeSerializable;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na poziomach trudności torów, z poziomem izolacji  read committed..
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
    @RolesAllowed("getAllActiveAlleys")
    public List<AlleyEntity> getAllActiveAlleys() throws AppException {
        return alleyFacadeReadCommitted.findByActive(true);
    }

    @Override
    @RolesAllowed("getAlleyDetails")
    public AlleyEntity getAlleyDetails(String name) throws AppException {
        AlleyEntity alley = alleyFacadeReadCommitted.findByName(name).orElseThrow(AlleyDoesNotExistException::new);
        if(!alley.isActive()){
            throw new AlleyDoesNotExistException();
        }
        return alley;
    }

    @Override
    @RolesAllowed("editAlley")
    public void editAlleyDetails(AlleyEntity entity) throws AppException {
        AlleyEntity alley = alleyFacadeReadCommitted.find(entity.getId()).orElseThrow(AlleyDoesNotExistException::new);
        if(!alley.isActive()){
            throw new AlleyDoesNotExistException();
        }
        entity.copyNotChangedDataAlley(alley);
        alleyFacadeReadCommitted.edit(entity);
    }

    @Override
    @RolesAllowed("addAlley")
    public void addAlley(AlleyEntity alleyEntity, String alleyDifficultyLevelName) throws AppException {
        AlleyDifficultyLevelEntity alleyDifficultyLevelEntity =
                alleyDifficultyLevelFacadeReadCommitted.findByName(alleyDifficultyLevelName)
                        .orElseThrow(AlleyDifficultyLevelDoesNotExistException::new);
        alleyEntity.setDifficultyLevel(alleyDifficultyLevelEntity);
        if (alleyFacadeReadCommitted.findByName(alleyEntity.getName()).isPresent()) {
            throw new AlleyWithSuchNameExistsException();
        }
        alleyFacadeReadCommitted.create(alleyEntity);
    }

    @Override
    @RolesAllowed("removeAlley")
    public void removeAlley(String name) throws AppException {
        AlleyEntity alleyEntity = alleyFacadeSerializable.findByName(name).orElseThrow(AlleyDoesNotExistException::new);
        if (!alleyEntity.isActive()) {
            throw new AlleyIsAlreadyDeactivatedException();
        }
        if (alleyEntity.getReservations().stream().anyMatch(ReservationUtils::checkIsActiveReservation)) {
            throw new AttemptToRemoveAlleyWithActiveReservations();
        }
        alleyEntity.setActive(false);
        alleyFacadeSerializable.edit(alleyEntity);
    }
}
