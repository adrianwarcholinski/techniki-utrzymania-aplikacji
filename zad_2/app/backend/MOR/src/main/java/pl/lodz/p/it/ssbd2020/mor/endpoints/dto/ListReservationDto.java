package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.converters.LocalDateTimeAdapter;
import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Klasa reprezentująca dane rezerwacji przeznaczone do wyświetlenia jako rekord listy.
 */
public class ListReservationDto implements Serializable {
    /**
     * Pole reprezentujące numer rezerwacji.
     */
    private long reservationNumber;
    /**
     * Pole reprezentujące login klienta, który dokonał rezerwacji.
     */
    private String login;
    /**
     * Pole reprezentujące nazwę toru, który został zarezerwowany w ramach rezerwacji.
     */
    private String alleyName;
    /**
     * Pole reprezentujące nazwe modelu broni, która została zarezerwowana w ramach rezerwacji.
     */
    private String weaponModelName;
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
    /**
     * Pole informujące o tym czy rezerwacji nie została anulowana
     */
    private boolean active;

    public long getReservationNumber() {
        return reservationNumber;
    }

    public String getLogin() {
        return login;
    }

    public String getAlleyName() {
        return alleyName;
    }

    public String getWeaponModelName() {
        return weaponModelName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link ReservationEntity}, na obiekt klasy {@link ListReservationDto}.
     *
     * @param entity obiekt klasy {@link ReservationEntity} z danymi rezerwacji.
     * @return obiekt klasy {@link ListReservationDto} z danymi rezerwacji.
     */
    public static ListReservationDto fromReservationEntity(ReservationEntity entity) {
        ListReservationDto listReservationDto = new ListReservationDto();
        listReservationDto.active = entity.isActive();
        listReservationDto.alleyName = entity.getAlley().getName();
        listReservationDto.endDate = entity.getEndDate();
        listReservationDto.login = entity.getCustomer().getAccount().getLogin();
        listReservationDto.reservationNumber = entity.getReservationNumber();
        listReservationDto.startDate = entity.getStartDate();
        listReservationDto.weaponModelName = entity.getWeapon().getWeaponModel().getName();
        return listReservationDto;
    }
}
