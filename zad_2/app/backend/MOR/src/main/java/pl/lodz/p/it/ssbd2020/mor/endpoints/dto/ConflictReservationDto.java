package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.converters.LocalDateTimeAdapter;
import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Klasa reprezentująca dane konfliktowej rezeracji.
 */
public class ConflictReservationDto implements Serializable {
    /**
     * Pole reprezentujące czas rozpoczecia rezerwacji
     */
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime startDate;

    /**
     * Pole reprezentujące czas zakończenia rezerwacji
     */
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime endDate;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link ReservationEntity}, na obiekt klasy {@link ConflictReservationDto}.
     *
     * @param entity obiekt klasy {@link ReservationEntity} z danymi rezerwacji.
     * @return obiekt klasy {@link ConflictReservationDto} z danymi konfliktowej rezerwacji.
     */
    public static ConflictReservationDto map(ReservationEntity entity) {
        ConflictReservationDto reservationDto = new ConflictReservationDto();
        reservationDto.startDate=entity.getStartDate();
        reservationDto.endDate=entity.getEndDate();
        return reservationDto;
    }
}
