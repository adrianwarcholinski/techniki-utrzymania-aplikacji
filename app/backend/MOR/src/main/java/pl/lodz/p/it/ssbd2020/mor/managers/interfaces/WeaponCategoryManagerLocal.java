package pl.lodz.p.it.ssbd2020.mor.managers.interfaces;

import pl.lodz.p.it.ssbd2020.entities.WeaponCategoryEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.utils.manager.Manager;

import javax.ejb.Local;
import java.util.List;

/**
 * Interfejs przeznaczony do zaimplementowania przez menadżerów umożliwiający operacje na kategoriach broni.
 */
@Local
public interface WeaponCategoryManagerLocal extends Manager{

    /**
     * Implementacja tej metody powinna zwracać wszystkie aktywne kategorie broni.
     *
     * @return wszystkie znalezione aktywne kategorie broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<WeaponCategoryEntity> getAllWeaponCategories() throws AppException;
}
