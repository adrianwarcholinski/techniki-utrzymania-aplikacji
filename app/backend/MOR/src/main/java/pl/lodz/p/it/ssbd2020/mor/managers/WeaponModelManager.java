package pl.lodz.p.it.ssbd2020.mor.managers;

import pl.lodz.p.it.ssbd2020.entities.WeaponCategoryEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.*;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.WeaponCategoryFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.WeaponModelFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces.WeaponModelFacadeSerializableLocal;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.WeaponModelManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.ReservationUtils;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.security.enterprise.SecurityContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Menadżer odpowiedzialny za operacje modelach broni.
 */
@Stateful
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WeaponModelManager implements WeaponModelManagerLocal {

    /**
     * Kontekst bezpieczeństwa, który zwraca informacje na temat zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na modelach broni, z poziomem izolacji read committed.
     **/
    @Inject
    private WeaponModelFacadeReadCommittedLocal weaponModelFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na modelach broni, z poziomem izolacji read committed.
     **/
    @Inject
    private WeaponModelFacadeSerializableLocal weaponModelFacadeSerializable;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na kategoriach modelu broni, z poziomem izolacji read committed.
     **/
    @Inject
    private WeaponCategoryFacadeReadCommittedLocal weaponCategoryFacadeReadCommitted;

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
    @PermitAll
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
    @RolesAllowed("getAllActiveWeaponModels")
    public List<WeaponModelEntity> getAllActiveWeaponModels() throws AppException {
        return weaponModelFacadeReadCommitted.findByActive(true);
    }

    @Override
    @RolesAllowed("removeWeaponModel")
    public void removeWeaponModel(String name) throws AppException {
        WeaponModelEntity weaponModelEntity = weaponModelFacadeSerializable.findByName(name).orElseThrow(WeaponModelDoesNotExistException::new);
        if (!weaponModelEntity.isActive())
            throw new WeaponModelIsAlreadyDeactivatedException();
        weaponModelEntity.setActive(false);
        for (WeaponEntity weaponEntity : weaponModelEntity.getWeapons())
            if (weaponEntity.isActive())
                removeWeapon(weaponEntity);
        weaponModelFacadeSerializable.edit(weaponModelEntity);
    }


    @RolesAllowed("getWeaponModel")
    public WeaponModelEntity getWeaponModel(String name) throws AppException {
        Optional<WeaponModelEntity> entity = weaponModelFacadeReadCommitted.findByName(name);
        if (entity.isEmpty() || !entity.get().isActive()) {
            throw new WeaponModelDoesNotExistException();
        }
        return entity.get();
    }

    @Override
    @RolesAllowed("addWeaponModel")
    public void addWeaponModel(WeaponModelEntity entity) throws AppException {
        if (weaponModelFacadeReadCommitted.findByName(entity.getName()).isPresent()) {
            throw new WeaponModelWithSuchNameExistsException();
        }
        Optional<WeaponCategoryEntity> weaponCategoryEntity = weaponCategoryFacadeReadCommitted.findByName(entity.getWeaponCategory().getName());
        if (weaponCategoryEntity.isEmpty()) {
            throw new WeaponCategoryDoesNotExistException();
        }
        entity.setWeaponCategory(weaponCategoryEntity.get());
        weaponModelFacadeReadCommitted.create(entity);
    }

    @Override
    @RolesAllowed("editWeaponModel")
    public void editWeaponModel(WeaponModelEntity entity) throws AppException {
        Optional<WeaponModelEntity> weaponModelEntity = weaponModelFacadeReadCommitted.find(entity.getId());
        if (weaponModelEntity.isEmpty() || !weaponModelEntity.get().isActive()) {
            throw new WeaponModelDoesNotExistException();
        }
        entity.copyNotChangedData(weaponModelEntity.get());
        Optional<WeaponCategoryEntity> weaponCategoryEntity = weaponCategoryFacadeReadCommitted.findByName(entity.getWeaponCategory().getName());
        if (weaponCategoryEntity.isEmpty()) {
            throw new WeaponCategoryDoesNotExistException();
        }
        entity.setWeaponCategory(weaponCategoryEntity.get());
        weaponModelFacadeReadCommitted.edit(entity);
    }

    @Override
    @RolesAllowed("getAllActiveWeaponModelsWithActiveWeapons")
    public List<WeaponModelEntity> getAllActiveWeaponModelsWithActiveWeapons() throws AppException {
        return weaponModelFacadeReadCommitted.findByActive(true).stream()
                .filter(weaponModelEntity -> weaponModelEntity.getWeapons().stream().anyMatch(WeaponEntity::isActive)).collect(Collectors.toList());
    }

    /**
     * Metoda deaktywująca egzemplarz broni, jeśli nie ma on aktywnych przyszłych lub trwających rezerwacji.
     *
     * @param weaponEntity obiekt egzemplarza broni, który chcemu usunąć(dezaktywować)
     * @throws AttemptToRemoveWeaponModelWithActiveReservationException jeśli próbujemy usunąć egzemplarz broni,
     *                                                                  który ma aktywne jakiekolwiek przyszłe
     *                                                                  lub trwające rezerwacje
     */

    private void removeWeapon(WeaponEntity weaponEntity) throws AppException {
        if (weaponEntity.getReservations().stream().anyMatch(ReservationUtils::checkIsActiveReservation))
            throw new AttemptToRemoveWeaponModelWithActiveReservationException();
        else
            weaponEntity.setActive(false);
    }
}
