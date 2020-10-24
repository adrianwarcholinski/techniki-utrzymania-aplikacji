package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;


import pl.lodz.p.it.ssbd2020.converters.LocalDateTimeAdapter;
import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;
import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Klasa reprezentująca dane przeznaczone do edycji własnej rezerwacji.
 */
public class EditOwnReservationDto implements Serializable {

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
    private String weaponModelName;

    /**
     * Pole reprezentujące początkową datę rezerwacji
     */
    @NotNull
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime startDate;

    /**
     * Pole reprezentujące końcową datę rezerwacji
     */
    @NotNull
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
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
     * Metoda statyczna konwertująca obiekt klasy {@link EditOwnReservationDto}, na obiekt klasy {@link ReservationEntity}.
     *
     * @param reservationDto obiekt klasy {@link EditOwnReservationDto} z danymi rezerwacji.
     * @return obiekt klasy {@link ReservationEntity} z danymi rezerwacji.
     */
    public static ReservationEntity map(EditOwnReservationDto reservationDto) {
        return new ReservationEntity(
                Long.parseLong(reservationDto.id),
                new AlleyEntity(reservationDto.alleyName),
                new WeaponEntity(new WeaponModelEntity(reservationDto.weaponModelName)),
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

    public String getWeaponModelName() {
        return weaponModelName;
    }

    public void setWeaponModelName(String weaponModelName) {
        this.weaponModelName = weaponModelName;
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
