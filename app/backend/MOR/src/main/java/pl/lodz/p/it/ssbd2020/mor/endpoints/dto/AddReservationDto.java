package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * Klasa reprezentująca dane do utworzenia rezeracji.
 */
public class AddReservationDto {

    /**
     * Pole reprezentujące początek rezerwacji.
     */
    @NotNull
    //@Future
    private String startDate;

    /**
     * Pole reprezentujące koniec prezentacji.
     */
    @NotNull
    //@Future
    private String endDate;

    /**
     * Pole reprezentujące nazwe modlu borni.
     */
    @NotBlank
    @Pattern(regexp = RegexPatterns.NAME)
    private String weaponModelName;

    /**
     * Pole reprezentujące nazwę toru.
     */
    @NotBlank
    @Pattern(regexp = RegexPatterns.NAME)
    private String alleyName;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getWeaponModelName() {
        return weaponModelName;
    }

    public void setWeaponModelName(String weaponModelName) {
        this.weaponModelName = weaponModelName;
    }

    public String getAlleyName() {
        return alleyName;
    }

    public void setAlleyName(String alleyName) {
        this.alleyName = alleyName;
    }
}
