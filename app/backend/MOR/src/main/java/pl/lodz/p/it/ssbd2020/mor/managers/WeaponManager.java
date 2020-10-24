package pl.lodz.p.it.ssbd2020.mor.managers;

import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.*;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.WeaponFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces.WeaponFacadeSerializableLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces.WeaponModelFacadeSerializableLocal;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.WeaponManagerLocal;
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

/**
 * Menadżer odpowiedzialny za operacje na egzemplarzach broni.
 */
@Stateful
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WeaponManager implements WeaponManagerLocal {

    /**
     * Kontekst bezpieczeństwa, który zwraca informacje na temat zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Komponent EJB (Fasada) udostępniająca operacje na encjach egzemplarzy broni, z poziomem izolacji read committed.
     */
    @Inject
    private WeaponFacadeReadCommittedLocal weaponFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) udostępniająca operacje na encjach egzemplarzy broni, z poziomem izolacji read committed.
     */
    @Inject
    private WeaponFacadeSerializableLocal weaponFacadeSerializable;

    /**
     * Komponent EJB (Fasada) udostępniająca operacje na encjach modelów broni, z poziomem izolacji read committed.
     */
    @Inject
    private WeaponModelFacadeSerializableLocal weaponModelFacadeSerializable;

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
    @RolesAllowed("ROLE_EMPLOYEE")
    public List<WeaponEntity> getAllActiveWeapons() throws AppException {
        return weaponFacadeReadCommitted.findByActive(true);
    }

    @Override
    @RolesAllowed("ROLE_EMPLOYEE")
    public void removeWeapon(String serialNumber) throws AppException {
        WeaponEntity weaponToDeactivate = weaponFacadeSerializable.findBySerialNumber(serialNumber).orElseThrow(WeaponDoesNotExistException::new);
        if (!weaponToDeactivate.isActive()) {
            throw new WeaponIsAlreadyDeactivatedException();
        }
        if (weaponToDeactivate.getReservations().stream().anyMatch(ReservationUtils::checkIsActiveReservation)) {
            throw new AttemptToRemoveWeaponWithActiveReservationException();
        }
        weaponToDeactivate.setActive(false);
        weaponFacadeSerializable.edit(weaponToDeactivate);
    }

    @Override
    @RolesAllowed("ROLE_EMPLOYEE")
    public void createWeapon(WeaponEntity weaponEntity, String weaponModelName) throws AppException {
        Optional<WeaponEntity> entityWithTheSameSerialNumber = weaponFacadeSerializable.findBySerialNumber(weaponEntity.getSerialNumber());
        if (entityWithTheSameSerialNumber.isPresent()) {
            throw new WeaponWithSuchSerialNumberAlreadyExistsException();
        }
        WeaponModelEntity weaponModelEntity = weaponModelFacadeSerializable.findByName(weaponModelName).orElseThrow(WeaponModelDoesNotExistException::new);
        if (!weaponModelEntity.isActive()) {
            throw new WeaponModelIsAlreadyDeactivatedException();
        }
        weaponEntity.setWeaponModel(weaponModelEntity);
        weaponFacadeSerializable.create(weaponEntity);
    }

    @Override
    @RolesAllowed("ROLE_EMPLOYEE")
    public List<WeaponEntity> getAllActiveWeaponsByModelName(String modelName) throws AppException {
        return weaponFacadeReadCommitted.findByActiveAndWeaponModelName(true, modelName);
    }
}
