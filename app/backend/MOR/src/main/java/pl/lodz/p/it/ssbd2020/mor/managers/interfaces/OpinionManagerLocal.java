package pl.lodz.p.it.ssbd2020.mor.managers.interfaces;

import pl.lodz.p.it.ssbd2020.entities.OpinionEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.utils.manager.Manager;

import javax.ejb.Local;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Interfejs przeznaczony do zaimplementowania przez menadżerów umożliwiających operacje
 * na opiniach klientów na temat modeli broni.
 */
@Local
public interface OpinionManagerLocal extends Manager {

    /**
     * Implementacja tej metody powinna zwracać wszystkie opinie dla podanego modelu broni.
     *
     * @param name Nazwa modelu broni dla której chcemy pobrać opinie.
     * @return lista z wszystkim opinia podanego modelu broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<OpinionEntity> getAllOpinionsForWeaponModel(@NotBlank String name) throws AppException;

    /**
     * Implementacja tej metody powinna dodawać opinię na temat modelu broni.
     *
     * @param opinion opinia klienta na temat modelu broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void addOpinion(OpinionEntity opinion) throws AppException;

    /**
     * Implementacja tej metody powinna aktualizować opinię na temat modelu broni.
     *
     * @param opinion zaktualizowana opinia klienta na temat modelu broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void editOpinion(OpinionEntity opinion) throws AppException;

    /**
     * Implementacja tej metody powinna usuwać opinię o podanym numerze identyfikacyjnym
     *
     * @param opinionNumber numer identyfikacyjny opinii, którą chcemy usunąć
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void removeOpinion(Long opinionNumber) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać opinię dodaną przez wywołującego użytkownika na temat modelu broni.
     *
     * @param name nazwa modelu broni
     * @return opinia dodana przez wywołującego na temat modelu broni, lub {@code null},
     * jeżeli użytkownik ten nie ocenił tego modelu broni
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    OpinionEntity getOwnOpinionForWeaponModel(String name) throws AppException;
}
