package pl.lodz.p.it.ssbd2020.utils;

import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;

import java.time.LocalDateTime;


/**
 * Klasa pomocnicza do działania na rezerwacji
 */
public class ReservationUtils {

    /**
     * Metoda statyczna sprawdzająca czy rezerwacja jest przyszłą lub trwającą aktwną rezerwacją
     *
     * @param reservationEntity enacja rezeracji, którą chcemy sprawdzić
     * @return wartość logiczna wskazująca czy rezerwacja jest przyszłą lub trwającą aktwną rezerwacją
     */
    public static boolean checkIsActiveReservation(ReservationEntity reservationEntity) {
        LocalDateTime actualDateTime = LocalDateTime.now();
        return reservationEntity.isActive() &&
                (reservationEntity.getStartDate().isAfter(actualDateTime) ||
                        (reservationEntity.getStartDate().isBefore(actualDateTime)
                                && reservationEntity.getEndDate().isAfter(actualDateTime)));
    }

}
