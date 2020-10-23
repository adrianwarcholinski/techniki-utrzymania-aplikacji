package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Klasa reprezentująca dane własnej rezerwacji.
 */
public class OwnReservationDto implements Serializable {

    /**
     * Pole reprezentujące identyfikator rezerwacji.
     */
    @NotBlank
    private String id;

    /**
     * Pole reprezentujące numer rezerwacji.
     */
    @NotNull
    private long reservationNumber;

    /**
     * Pole reprezentujące tor przypisany do rezerwacji.
     */
    @NotNull
    @Valid
    private ListAlleyDto alley;

    /**
     * Pole reprezentujące broń przypisaną do rezerwacji.
     */
    @NotEmpty
    private String weaponModelName;

    /**
     * Pole reprezentujące początkową datę rezerwacji
     */
    @NotNull
    private LocalDateTime startDate;

    /**
     * Pole reprezentujące końcową datę rezerwacji
     */
    @NotNull
    private LocalDateTime endDate;

    /**
     * Asercja sprawdzająca czy podana początkowa data jest wcześniejsza niż data końcowa.
     *
     * @return wartość logiczną informująca czy data początkowa jest przed datą końcową.
     */
    @AssertTrue
    public boolean datesCheck() {
        return startDate.isBefore(endDate);
    }
    
    /**
     * Pole reprezentujące czy rezerwacja jest aktywna.
     */
    private boolean active;

    /**
     * Pole reprezentujące czy wersję rezerwacji.
     */
    @NotBlank
    private String version;
    /**
     * Metoda statyczna koonwertująca obiekt klasy {@link ReservationEntity}, na obiekt klasy {@link OwnReservationDto}.
     *
     * @param entity obiekt klasy {@link ReservationEntity} z danymi rezerwacji.
     * @return obiekt klasy {@link OwnReservationDto} z danymi rezerwacji.
     */
    public static OwnReservationDto map(ReservationEntity entity) {
        OwnReservationDto reservationDto = new OwnReservationDto();
        reservationDto.setActive(entity.isActive());
        reservationDto.setAlley(ListAlleyDto.map(entity.getAlley()));
        reservationDto.setWeaponModelName(entity.getWeapon().getWeaponModel().getName());
        reservationDto.setReservationNumber(entity.getReservationNumber());
        reservationDto.setStartDate(entity.getStartDate());
        reservationDto.setEndDate(entity.getEndDate());
        reservationDto.setVersion(String.valueOf(entity.getVersion()));
        reservationDto.setId(String.valueOf(entity.getId()));
        return reservationDto;
    }

    public long getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(long reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public ListAlleyDto getAlley() {
        return alley;
    }

    public void setAlley(ListAlleyDto alley) {
        this.alley = alley;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
