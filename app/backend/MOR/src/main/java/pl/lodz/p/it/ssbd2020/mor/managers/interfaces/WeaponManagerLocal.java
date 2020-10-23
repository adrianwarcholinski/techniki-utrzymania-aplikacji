package pl.lodz.p.it.ssbd2020.mor.managers.interfaces;

import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.utils.manager.Manager;

import javax.ejb.Local;
import java.util.List;

/**
 * Interfejs przeznaczony do zaimplementowania przez menadżerów umożliwiający operacje na egzemplarzach broni.
 */
@Local
public interface WeaponManagerLocal extends Manager {
    /**
     * Implementacja tej metody powinna zwracać wszystkie aktywne egzemplarze broni.
     *
     * @return wszystkie znalezione egzemplarze broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<WeaponEntity> getAllActiveWeapons() throws AppException;

    /**
     * Implementacja tej metody powinna usuwać egzemplarz broni - ustawia wartość pola active encji {@link WeaponEntity} na false
     *
     * @param serialNumber numer seryjny broni
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void removeWeapon(String serialNumber) throws AppException;

    /**
     * Implementacja tej metody powinna tworzyć nowy egzemplarz broni
     *
     * @param weaponEntity encja reprezentująca egzemplarz broni, który dodajemy
     * @param weaponModelName nazwa modelu broni
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void createWeapon(WeaponEntity weaponEntity, String weaponModelName) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać wszystkie aktywne egzemplarze broni danego modelu broni.
     *
     * @param modelName nazwa modelu broni
     * @return wszystkie znalezione egzemplarze broni danego modelu broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<WeaponEntity> getAllActiveWeaponsByModelName(String modelName) throws AppException;

}
