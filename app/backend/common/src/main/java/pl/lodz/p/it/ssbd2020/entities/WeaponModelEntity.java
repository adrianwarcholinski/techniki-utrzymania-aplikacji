package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "weapon_model")
@TableGenerator(name = "WeaponModelIdGen", table = "generator", pkColumnName = "class_name", valueColumnName = "id_range", pkColumnValue = "WeaponModelEntity")
@NamedQueries({
        @NamedQuery(name = "WeaponModelEntity.findAll", query = "SELECT wm FROM WeaponModelEntity wm"),
        @NamedQuery(name = "WeaponModelEntity.findById", query = "SELECT wm FROM WeaponModelEntity wm WHERE wm.id = :id"),
        @NamedQuery(name = "WeaponModelEntity.findByName", query = "SELECT wm FROM WeaponModelEntity wm WHERE wm.name = :name"),
        @NamedQuery(name = "WeaponModelEntity.findByActive", query = "SELECT wm FROM WeaponModelEntity wm WHERE wm.active = :active ORDER BY wm.name")
})
@SecondaryTable(name = "average_rate", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "weapon_model_id")})
public class WeaponModelEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "WeaponModelIdGen")
    @Column(updatable = false)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false, length = 20)
    @NotBlank
    @Size(max = 20, message = "Name maximum length is 20")
    @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME, message = "Weapon model name is not valid")
    private String name;

    @Column(length = 400)
    @NotBlank
    @Size(max = 400, message = "Description maximum length is 400")
    @Pattern(regexp = RegexPatterns.WEAPON_MODEL_DESCRIPTION, message = "Weapon model description is not valid")
    private String description;

    @Column(name = "caliber_mm", nullable = false)
    @Positive
    private double caliberMm;

    @Column(name = "magazine_capacity", nullable = false)
    @Positive
    private int magazineCapacity;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_weapon_category", referencedColumnName = "id")
    @NotNull
    @Valid
    private WeaponCategoryEntity weaponCategory;

    @OneToMany(mappedBy = "weaponModel")
    @NotNull
    private List<WeaponEntity> weapons = new ArrayList<>();

    @OneToMany(mappedBy = "weaponModel")
    @NotNull
    private List<OpinionEntity> opinions = new ArrayList<>();

    @Column(nullable = false)
    private boolean active;

    @Column(table = "average_rate", name = "value")
    @Max(5)
    @Min(1)
    private Double averageRate;

    @AssertTrue
    public boolean getAverageRateCheck() {
        if (averageRate != null) {
            return averageRate >= 1.0d && averageRate <= 5.0d;
        }
        return true;
    }

    public WeaponModelEntity() {
    }

    public WeaponModelEntity(String name) {
        this.name = name;
    }

    public WeaponModelEntity(String name, String description, double caliberMm, int magazineCapacity, WeaponCategoryEntity weaponCategory) {
        this.name = name;
        this.description = description;
        this.caliberMm = caliberMm;
        this.magazineCapacity = magazineCapacity;
        this.weaponCategory = weaponCategory;
        this.active = true;
        this.averageRate = null;
    }

    public WeaponModelEntity(long id, String name, String description, double caliberMm, int magazineCapacity, WeaponCategoryEntity weaponCategory, long version) {
        super(version);
        this.id = id;
        this.name = name;
        this.description = description;
        this.caliberMm = caliberMm;
        this.magazineCapacity = magazineCapacity;
        this.weaponCategory = weaponCategory;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getCaliberMm() {
        return caliberMm;
    }

    public int getMagazineCapacity() {
        return magazineCapacity;
    }

    public WeaponCategoryEntity getWeaponCategory() {
        return weaponCategory;
    }

    public List<WeaponEntity> getWeapons() {
        return weapons;
    }

    public List<OpinionEntity> getOpinions() {
        return opinions;
    }

    public boolean isActive() {
        return active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCaliberMm(double caliberMm) {
        this.caliberMm = caliberMm;
    }

    public void setMagazineCapacity(int magazineCapacity) {
        this.magazineCapacity = magazineCapacity;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Double getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(Double averageRate) {
        this.averageRate = averageRate;
    }

    public void setWeaponCategory(WeaponCategoryEntity weaponCategory) {
        this.weaponCategory = weaponCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeaponModelEntity)) return false;
        WeaponModelEntity that = (WeaponModelEntity) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("%s = [With id: %s, business key: %s and version: %s.]",
                this.getClass().getName(), id, name, this.getVersion());
    }


    /**
     * Metoda przepisująca dane, które nie zmieniają się w przypadku zmiany szczegółów modelu bronii, z jednej encji do drugiej.
     *
     * @param source encja stanowiąca źródło informacji.
     */
    public void copyNotChangedData(WeaponModelEntity source) {
        this.active = source.isActive();
        this.averageRate = source.getAverageRate();
        this.weapons = source.getWeapons();
        this.opinions = source.getOpinions();
    }
}
