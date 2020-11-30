package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane do utworzenia egzemplarza broni.
 */
public class AddWeaponDto implements Serializable {

    /**
     * Pole reprezentujące numer seryjny.
     */
    @NotBlank
    @Size(max = 25, message = "SerialNumber maximum length is 25")
    @Pattern(regexp = RegexPatterns.WEAPON_SERIAL_NUMBER, message = "Weapon Serial number is not valid")
    private String serialNumber;

    /**
     * Pole reprezentujące nazwę modelu broni.
     */
    @NotBlank
    @Size(max = 20, message = "Name maximum length is 20")
    @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME, message = "Weapon model name is not valid")
    private String weaponModelName;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getWeaponModelName() {
        return weaponModelName;
    }

    public void setWeaponModelName(String weaponModelName) {
        this.weaponModelName = weaponModelName;
    }
}
