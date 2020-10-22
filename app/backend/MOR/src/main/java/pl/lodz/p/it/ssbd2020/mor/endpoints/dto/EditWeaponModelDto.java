package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.WeaponCategoryEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane przeznaczone do edycji modelu broni.
 */
public class EditWeaponModelDto implements Serializable {

    /**
     * Pole reprezentujące identyfikator danego modelu broni.
     */
    @NotBlank
    private String id;

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
     * Pole reprezentujące ilość egzemplarzy danego modelu broni.
     */
    private int numberOfWeapons;

    /**
     * Pole reprezentujące ilość opinii wystawionych danemu modelowi broni.
     */
    private int numberOfOpinions;

    /**
     * Pole reprezentujące średnią ocenę danego modelu broni.
     */
    @Max(5)
    @Min(1)
    private Double averageRate;

    /**
     * Pole reprezentujące wersję danego modelu broni.
     */
    @NotBlank
    private String version;

    public EditWeaponModelDto() {

    }

    public EditWeaponModelDto(@NotBlank @Size(max = 20) @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME) String name,
                              @NotBlank @Size(max = 400) @Pattern(regexp = RegexPatterns.WEAPON_MODEL_DESCRIPTION) String description,
                              @Positive @Max(999999) double caliberMm,
                              @Positive @Max(9999) @Min(1) int magazineCapacity,
                              @NotBlank @Pattern(regexp = RegexPatterns.WEAPON_MODEL_CATEGORY) String weaponCategory,
                              int numberOfWeapons,
                              int numberOfOpinions,
                              Double averageRate,
                              String version,
                              String id) {
        this.name = name;
        this.description = description;
        this.caliberMm = caliberMm;
        this.magazineCapacity = magazineCapacity;
        this.weaponCategory = weaponCategory;
        this.numberOfWeapons = numberOfWeapons;
        this.numberOfOpinions = numberOfOpinions;
        this.averageRate = averageRate;
        this.version = version;
        this.id = id;
    }

    /**
     * Metoda konwertująca obiekt klasy {@link EditWeaponModelDto}, na obiekt klasy {@link WeaponModelEntity}.
     *
     * @param entity obiekt encyjny klasy {@link WeaponCategoryEntity} z modelu broni.
     * @return obiekt klasy {@link WeaponModelEntity} z modelu broni.
     */
    public WeaponModelEntity convertToEntity(WeaponCategoryEntity entity) {
        return new WeaponModelEntity(Long.parseLong(this.id), this.name, this.description, this.caliberMm, this.magazineCapacity, entity, Long.parseLong(this.version));
    }

    /**
     * Metoda konwertująca obiekt klasy {@link WeaponModelEntity}, na obiekt klasy {@link EditWeaponModelDto}.
     *
     * @param entity obiekt encyjny klasy {@link WeaponModelEntity} z modelu broni.
     * @return obiekt klasy {@link EditWeaponModelDto} z modelu broni.
     */
    public static EditWeaponModelDto convertToDto(WeaponModelEntity entity) {
        return new EditWeaponModelDto(
                entity.getName(),
                entity.getDescription(),
                entity.getCaliberMm(),
                entity.getMagazineCapacity(),
                entity.getWeaponCategory().getName(),
                entity.getWeapons().size(),
                entity.getOpinions().size(),
                entity.getAverageRate(),
                Long.toString(entity.getVersion()),
                Long.toString(entity.getId())
        );
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

    public int getNumberOfWeapons() {
        return numberOfWeapons;
    }

    public void setNumberOfWeapons(int numberOfWeapons) {
        this.numberOfWeapons = numberOfWeapons;
    }

    public int getNumberOfOpinions() {
        return numberOfOpinions;
    }

    public void setNumberOfOpinions(int numberOfOpinions) {
        this.numberOfOpinions = numberOfOpinions;
    }

    public Double getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(Double averageRate) {
        this.averageRate = averageRate;
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
