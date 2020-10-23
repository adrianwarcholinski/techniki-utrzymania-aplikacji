package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;


import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Klasa reprezentująca dane rezerwacji.
 */
public class ReservationDto implements Serializable {

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
     * Pole reprezentujące klienta przypisanego do rezerwacji.
     */
    @NotNull
    @Valid
    private CustomerDto customer;

    /**
     * Pole reprezentujące tor przypisany do rezerwacji.
     */
    @NotNull
    @Valid
    private ListAlleyDto alley;

    /**
     * Pole reprezentujące broń przypisaną do rezerwacji.
     */
    @NotNull
    @Valid
    private ListWeaponDto weapon;

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
     * Pole reprezentujące czy rezerwacja jest aktywna.
     */
    private boolean active;

    /**
     * Pole reprezentujące czy wersję rezerwacji.
     */
    @NotBlank
    private String version;
    /**
     * Metoda statyczna koonwertująca obiekt klasy {@link ReservationEntity}, na obiekt klasy {@link ReservationDto}.
     *
     * @param entity obiekt klasy {@link ReservationEntity} z danymi rezerwacji.
     * @return obiekt klasy {@link ReservationDto} z danymi rezerwacji.
     */
    public static ReservationDto map(ReservationEntity entity) {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setActive(entity.isActive());
        reservationDto.setAlley(ListAlleyDto.map(entity.getAlley()));
        reservationDto.setWeapon(ListWeaponDto.map(entity.getWeapon()));
        reservationDto.setCustomer(CustomerDto.map(entity.getCustomer()));
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

    public CustomerDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDto customer) {
        this.customer = customer;
    }

    public ListAlleyDto getAlley() {
        return alley;
    }

    public void setAlley(ListAlleyDto alley) {
        this.alley = alley;
    }

    public ListWeaponDto getWeapon() {
        return weapon;
    }

    public void setWeapon(ListWeaponDto weapon) {
        this.weapon = weapon;
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
