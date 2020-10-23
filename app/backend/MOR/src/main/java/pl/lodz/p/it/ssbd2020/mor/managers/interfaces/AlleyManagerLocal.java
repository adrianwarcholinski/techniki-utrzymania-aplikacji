package pl.lodz.p.it.ssbd2020.mor.managers.interfaces;

import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.utils.manager.Manager;

import javax.ejb.Local;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Interfejs przeznaczony do zaimplementowania przez menadżerów umożliwiających operacje na torach.
 */
@Local
public interface AlleyManagerLocal extends Manager {
    /**
     * Implementacja tej metody powinna zwracać wszystkie aktywne tory.
     *
     * @return wszystkie znalezione aktywne tory
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<AlleyEntity> getAllActiveAlleys() throws AppException;

    /**
     * Implementacja tej metody powinna zwracać tor o podanej nazwie.
     *
     * @param name nazwa przypisana do toru
     * @return znaleziony tor
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    AlleyEntity getAlleyDetails(@NotBlank @Size(max = 50) String name) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać tor o podanej nazwie.
     *
     * @param entity encja reprezentująca edytowane dane toru.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void editAlleyDetails(AlleyEntity entity) throws AppException;

    /**
     * Implementacja tej metody powinna dodawać nowy tor.
     *
     * @param entity                   encja {@link AlleyEntity} reprezentująca tor, który dodajemy.
     * @param alleyDifficultyLevelName nazwa poziomu trudności toru.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void addAlley(AlleyEntity entity, String alleyDifficultyLevelName) throws AppException;

    /**
     * Implementacja tej metody powinna deaktywować tor.
     *
     * @param name Nazwa toru do deaktywacji.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void removeAlley(@NotBlank String name) throws AppException;
}
