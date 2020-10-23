package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;


import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;
import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Klasa reprezentująca dane przeznaczone do edycji rezerwacji.
 */
public class EditReservationDto implements Serializable {

    /**
     * Pole reprezentujące identyfikator rezerwacji.
     */
    @NotBlank
    private String id;

    /**
     * Pole reprezentujące tor przypisany do rezerwacji.
     */
    @NotBlank
    @Pattern(regexp = RegexPatterns.NAME)
    private String alleyName;

    /**
     * Pole reprezentujące broń przypisaną do rezerwacji.
     */
    @NotBlank
    @Pattern(regexp = RegexPatterns.NAME)
    private String weaponSerialNumber;

    /**
     * Pole reprezentujące początkową datę rezerwacji
     */
    @NotNull
    private LocalDateTime startDate;

    /**
     * Pole reprezentujące końcową datę rezerwacji
     */
    @NotNull
    @Future
    private LocalDateTime endDate;

    /**
     * Asercja sprawdzająca czy podana początkowa data jest wcześniejsza niż data końcowa.
     *
     * @return wartość logiczną informująca czy data początkowa jest przed datą końcową.
     */
    @AssertTrue
    public boolean isValidDates() {
        return startDate.isBefore(endDate);
    }


    /**
     * Pole reprezentujące czy wersję rezerwacji.
     */
    @NotBlank
    private String version;

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link EditReservationDto}, na obiekt klasy {@link ReservationEntity}.
     *
     * @param reservationDto obiekt klasy {@link EditReservationDto} z danymi rezerwacji.
     * @return obiekt klasy {@link ReservationEntity} z danymi rezerwacji.
     */
    public static ReservationEntity map(EditReservationDto reservationDto) {
        return new ReservationEntity(
                Long.parseLong(reservationDto.id),
                new AlleyEntity(reservationDto.alleyName),
                new WeaponEntity(reservationDto.weaponSerialNumber),
                reservationDto.startDate, reservationDto.endDate,
                Long.parseLong(reservationDto.version));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlleyName() {
        return alleyName;
    }

    public void setAlleyName(String alleyName) {
        this.alleyName = alleyName;
    }

    public String getWeaponSerialNumber() {
        return weaponSerialNumber;
    }

    public void setWeaponSerialNumber(String weaponSerialNumber) {
        this.weaponSerialNumber = weaponSerialNumber;
    }

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
