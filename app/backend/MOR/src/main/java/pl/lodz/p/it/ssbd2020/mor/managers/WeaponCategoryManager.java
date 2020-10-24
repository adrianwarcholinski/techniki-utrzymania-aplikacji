package pl.lodz.p.it.ssbd2020.mor.managers;

import pl.lodz.p.it.ssbd2020.entities.WeaponCategoryEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.NoWeaponCategoriesException;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.WeaponCategoryFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.WeaponCategoryManagerLocal;
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

/**
 * Menadżer odpowiedzialny za operacje na kategoriach broni.
 */
@Stateful
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WeaponCategoryManager implements WeaponCategoryManagerLocal {

    /**
     * Kontekst bezpieczeństwa, który zwraca informacje na temat zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Komponent EJB (Fasada) udostępniają operacje na kategoriach broni, z poziomem izolacji read committed.
     */
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
    @RolesAllowed("getAllWeaponCategories")
    public List<WeaponCategoryEntity> getAllWeaponCategories() throws AppException {
        List<WeaponCategoryEntity> list = weaponCategoryFacadeReadCommitted.findAll();
        if (list.size() == 0) {
            throw new NoWeaponCategoriesException();
        }
        return list;
    }
}
