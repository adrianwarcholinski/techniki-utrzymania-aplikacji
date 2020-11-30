package pl.lodz.p.it.ssbd2020.mok.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.CustomerEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje dla klasy encyjnej {@link CustomerEntity}.
 * Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface CustomerFacadeReadCommittedLocal extends BasicFacadeOperations<CustomerEntity> {

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy encyjnej {@link CustomerEntity}
     * o podanym numerzem telefonu
     *
     * @param phoneNumber numer telefonu przypisany do klienta
     * @return klient o podanym numerze telefonu lub
     * pusty Optional, jeżeli taki klient nie istnieje
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<CustomerEntity> findByPhoneNumber(String phoneNumber) throws AppException;
}
