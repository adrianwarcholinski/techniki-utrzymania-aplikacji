package pl.lodz.p.it.ssbd2020.mor.managers.interfaces;

import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.utils.manager.Manager;

import javax.ejb.Local;
import javax.validation.Valid;
import java.util.List;

/**
 * Interfejs przeznaczony do zaimplementowania przez menadżerów umożliwiający operacje na modelach broni.
 */
@Local
public interface WeaponModelManagerLocal extends Manager {

    /**
     * Implementacja tej metody powinna zwracać wszystkie aktywne modele broni.
     *
     * @return wszystkie znalezione aktywne modele broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<WeaponModelEntity> getAllActiveWeaponModels() throws AppException;

    /**
     * Implementacja tej metody powinna deaktywować model broni o podanej nazwie
     * oraz wszystkie egzemplarze tego modelu.
     * Jeśli żaden z egzemplarzy tego modelu nie jest zarezerwowany w przyszłej
     * lub obecnie trwającej rezerwacji
     *
     * @param name nazwa modelu broni, który chcemu usunąć
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void removeWeaponModel(String name) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać model broni o podanej nazwie.
     *
     * @param name nazwa modelu broni.
     * @return model broni o podanej nazwie.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    WeaponModelEntity getWeaponModel(String name) throws AppException;

    /**
     * Implementacja tej metody powinna dodawać nowy model broni.
     *
     * @param entity encja reprezentująca model broni który dodajemy
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void addWeaponModel(WeaponModelEntity entity) throws AppException;

    /**
     * Implementacja tej metody powinna edytować model broni.
     *
     * @param entity encja reprezentująca model broni który edytujemy
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void editWeaponModel(WeaponModelEntity entity) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać wszystkie aktywne modele broni posiadające aktywne egzemplarze broni.
     *
     * @return wszystkie znalezione aktywne modele broni posiadające aktywne egzemplarze broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<WeaponModelEntity> getAllActiveWeaponModelsWithActiveWeapons() throws AppException;
}
