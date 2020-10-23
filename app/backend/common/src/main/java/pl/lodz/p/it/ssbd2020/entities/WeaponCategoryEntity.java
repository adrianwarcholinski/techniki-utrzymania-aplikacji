package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "weapon_category")
@NamedQueries({
        @NamedQuery(name = "WeaponCategoryEntity.findAll", query = "SELECT wc FROM WeaponCategoryEntity wc"),
        @NamedQuery(name = "WeaponCategoryEntity.findById", query = "SELECT wc FROM WeaponCategoryEntity wc WHERE wc.id = :id"),
        @NamedQuery(name = "WeaponCategoryEntity.findByName", query = "SELECT wc FROM WeaponCategoryEntity wc WHERE wc.name = :name")
})
public class WeaponCategoryEntity extends AbstractEntity {

    @Id
    @Column(updatable = false)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false, length = 50)
    @NotBlank
    @Size(max = 50, message = "Name maximum length is 50")
    @Pattern(regexp = RegexPatterns.WEAPON_MODEL_CATEGORY, message = "Weapon model category is not valid")
    private String name;

    public WeaponCategoryEntity() {

    }

    public WeaponCategoryEntity(@NotBlank @Size(max = 50) String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeaponCategoryEntity)) return false;
        WeaponCategoryEntity that = (WeaponCategoryEntity) o;
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
}
