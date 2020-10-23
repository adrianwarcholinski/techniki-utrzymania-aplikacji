package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane modelu broni przeznaczone do wyświetlenia jako rekord listy.
 */
public class ListWeaponModelDto implements Serializable {

    /**
     * Pole reprezentujące nazwę modelu broni.
     */
    @NotBlank
    @Size(max = 20, message = "Name maximum length is 20")
    private String name;

    /**
     * Pole reprezentujące kaliber modelu broni.
     */
    @Positive
    private double caliberMm;

    /**
     * Pole reprezentujące nazwę kategori modelu broni.
     */
    @NotBlank
    @Size(max = 50, message = "Name maximum length is 50")
    private String weaponCategoryName;

    /**
     * Pole reprezentujące ocenę modelu broni.
     */
    @NotBlank
    @Max(5)
    @Min(1)
    private Double averageRate;

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link WeaponModelEntity}, na obiekt klasy {@link ListWeaponModelDto}.
     *
     * @param weaponModelEntity obiekt klasy {@link WeaponModelEntity} z danymi toru.
     * @return obiekt klasy {@link ListWeaponModelDto} z danymi toru.
     */
    public static ListWeaponModelDto fromWeaponModelEntity(WeaponModelEntity weaponModelEntity) {
        ListWeaponModelDto dto = new ListWeaponModelDto();
        dto.name = weaponModelEntity.getName();
        dto.weaponCategoryName = weaponModelEntity.getWeaponCategory().getName();
        dto.caliberMm = weaponModelEntity.getCaliberMm();
        dto.averageRate = weaponModelEntity.getAverageRate();
        return dto;
    }

    public String getName() {
        return name;
    }

    public double getCaliberMm() {
        return caliberMm;
    }

    public String getWeaponCategoryName() {
        return weaponCategoryName;
    }

    public Double getAverageRate() {
        return averageRate;
    }
}
