package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;

/**
 * Klasa reprezentująca dane egzemplarzu broni przeznaczone do wyświetlenia jako rekord listy.
 */
public class ListWeaponDto {

    /**
     * Pole reprezentujące numer seryjny egzemplarza broni.
     */
    private String serialNumber;

    /**
     * Pole reprezentujące nazwę modelu broni, który reprezentuje model broni.
     */
    private String weaponModelName;

    public ListWeaponDto(String serialNumber, String weaponModelName) {
        this.serialNumber = serialNumber;
        this.weaponModelName = weaponModelName;
    }

    public ListWeaponDto() { }

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

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link WeaponEntity}, na obiekt klasy {@link ListWeaponDto}.
     *
     * @param weaponEntity obiekt klasy {@link WeaponEntity} z danymi egzemplarzu broni.
     * @return obiekt klasy {@link ListWeaponDto} z danymi egzemplarzu boni.
     */
    public static ListWeaponDto map(WeaponEntity weaponEntity) {
        return new ListWeaponDto(weaponEntity.getSerialNumber(),weaponEntity.getWeaponModel().getName());
    }
}
