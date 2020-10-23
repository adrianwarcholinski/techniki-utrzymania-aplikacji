package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.WeaponCategoryEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane do utworzenia modelu broni.
 */
public class AddWeaponModelDto implements Serializable {

    /**
     * Pole reprezentujące nazwę modelu broni.
     */
    @NotBlank
    @Size(max = 20, message = "Name maximum length is 20")
    @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME, message = "Weapon model name is not valid")
    private String name;

    /**
     * Pole reprezentujące opis modelu broni.
     */
    @NotBlank
    @Size(max = 400, message = "Description maximum length is 400")
    @Pattern(regexp = RegexPatterns.WEAPON_MODEL_DESCRIPTION, message = "Weapon model description is not valid")
    private String description;

    /**
     * Pole reprezentujące kaliber modelu broni.
     */
    @Positive
    private double caliberMm;

    /**
     * Pole reprezentujące pojemność magazynku modelu broni.
     */
    @Positive
    private int magazineCapacity;

    /**
     * Pole reprezentujące nazwę kategorii modelu broni.
     */
    @NotBlank
    @Pattern(regexp = RegexPatterns.WEAPON_MODEL_CATEGORY, message = "Weapon model category is not valid")
    private String weaponCategory;


    /**
     * Metoda konwertująca obiekt klasy {@link AddWeaponModelDto}, na obiekt klasy {@link WeaponModelEntity}.
     *
     * @param entity obiekt klasy {@link WeaponCategoryEntity} z danymi kategori broni.
     * @return obiekt klasy {@link WeaponModelEntity} z danymi modelu broni.
     */
    public WeaponModelEntity convertToEntity(WeaponCategoryEntity entity) {
        return new WeaponModelEntity(this.name, this.description, this.caliberMm, this.magazineCapacity, entity);
    }

    public AddWeaponModelDto() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCaliberMm() {
        return caliberMm;
    }

    public void setCaliberMm(double caliberMm) {
        this.caliberMm = caliberMm;
    }

    public int getMagazineCapacity() {
        return magazineCapacity;
    }

    public void setMagazineCapacity(int magazineCapacity) {
        this.magazineCapacity = magazineCapacity;
    }

    public String getWeaponCategory() {
        return weaponCategory;
    }

    public void setWeaponCategory(String weaponCategory) {
        this.weaponCategory = weaponCategory;
    }
}
