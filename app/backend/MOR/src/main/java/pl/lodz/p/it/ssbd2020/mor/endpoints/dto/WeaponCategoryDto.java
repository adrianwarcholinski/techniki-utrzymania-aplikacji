package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.WeaponCategoryEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;

import java.io.Serializable;

/**
 * Klasa reprezentująca dane kategorii broni.
 */
public class WeaponCategoryDto implements Serializable {

    /**
     * Pole reprezentujące nazwę kategorii broni.
     */
    private String name;

    public WeaponCategoryDto() {

    }

    public WeaponCategoryDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Metoda statyczna koonwertująca obiekt klasy {@link WeaponCategoryEntity}, na obiekt klasy {@link WeaponCategoryDto}.
     *
     * @param weaponCategoryEntity obiekt klasy {@link WeaponCategoryEntity} z danymi kategorii broni.
     * @return obiekt klasy {@link WeaponCategoryDto} z danymi kategorii broni.
     */
    public static WeaponCategoryDto map(WeaponCategoryEntity weaponCategoryEntity) {
        return new WeaponCategoryDto(weaponCategoryEntity.getName());
    }
}
