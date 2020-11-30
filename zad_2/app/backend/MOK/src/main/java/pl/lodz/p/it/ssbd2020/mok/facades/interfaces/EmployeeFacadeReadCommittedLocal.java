package pl.lodz.p.it.ssbd2020.mok.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.EmployeeEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje dla klasy encyjnej {@link EmployeeEntity}.
 * Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface EmployeeFacadeReadCommittedLocal extends BasicFacadeOperations<EmployeeEntity> {

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy encyjnej {@link EmployeeEntity} z podanym numerzem telefonu
     *
     * @param workPhoneNumber numer telefonu przypisany do pracownika
     * @return pracownik o podanym numerze telefonu lub
     * pusty Optional, jeżeli taki pracownik nie istnieje
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<EmployeeEntity> findByWorkPhoneNumber(String workPhoneNumber) throws AppException;
}
