package pl.lodz.p.it.ssbd2020.mok.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.ExpiredTokenEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje dla klasy encyjnej {@link ExpiredTokenEntity}.
 * Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface ExpiredTokenFacadeReadCommittedLocal extends BasicFacadeOperations<ExpiredTokenEntity> {

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy encyjnej {@link ExpiredTokenEntity} z podanym tokenem
     *
     * @param token poszukiwany tokenwyc
     * @return ExpiredTokenEntity jeśli podany token był już użyty
     * pusty Optional, jeżeli taki token jeszcze nie był używany
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<ExpiredTokenEntity> findByToken(String token) throws AppException;

}
