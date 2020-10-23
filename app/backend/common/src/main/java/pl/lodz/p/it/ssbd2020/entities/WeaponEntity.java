package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "weapon", uniqueConstraints = @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_WEAPON_SERIAL_NUMBER, columnNames = "serial_number"))
@TableGenerator(name = "WeaponIdGen", table = "generator", pkColumnName = "class_name", valueColumnName = "id_range", pkColumnValue = "WeaponEntity")
@NamedQueries({
        @NamedQuery(name = "WeaponEntity.findAll", query = "SELECT w FROM WeaponEntity w"),
        @NamedQuery(name = "WeaponEntity.findById", query = "SELECT w FROM WeaponEntity w WHERE w.id = :id"),
        @NamedQuery(name = "WeaponEntity.findBySerial", query = "SELECT w FROM WeaponEntity w WHERE w.serialNumber = :serialNumber"),
        @NamedQuery(name = "WeaponEntity.findByActive", query = "SELECT w FROM WeaponEntity w WHERE w.active = " +
                ":active ORDER BY w.serialNumber, w.weaponModel.name"),
        @NamedQuery(name = "WeaponEntity.findByActiveAndModelName", query = "SELECT w FROM WeaponEntity w WHERE w" +
                ".active = :active AND w.weaponModel.name = :weaponModelName ORDER BY w.serialNumber, w.weaponModel" +
                ".name")
})
public class WeaponEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "WeaponIdGen")
    @Column(updatable = false)
    private Long id;

    @Column(name = "serial_number", unique = true, nullable = false, updatable = false, length = 25)
    @NotBlank
    @Size(max = 25, message = "SerialNumber maximum length is 25")
    private String serialNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_weapon_model", updatable = false, referencedColumnName = "id")
    @NotNull
    @Valid
    private WeaponModelEntity weaponModel;

    @OneToMany(mappedBy = "weapon", cascade = CascadeType.REMOVE)
    @NotNull
    private List<ReservationEntity> reservations = new ArrayList<>();

    @Column(nullable = false)
    private boolean active;

    public WeaponEntity() {
    }

    public WeaponEntity(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public WeaponEntity(WeaponModelEntity weaponModel) {
        this.weaponModel = weaponModel;
    }

    public WeaponEntity(String serialNumber, WeaponModelEntity weaponModel) {
        this.serialNumber = serialNumber;
        this.weaponModel = weaponModel;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public WeaponModelEntity getWeaponModel() {
        return weaponModel;
    }

    public void setWeaponModel(WeaponModelEntity weaponModel) { this.weaponModel = weaponModel; }

    public List<ReservationEntity> getReservations() {
        return reservations;
    }

    public boolean isActive() {
        return active;
    }

    public void setReservations(List<ReservationEntity> reservations) {
        this.reservations = reservations;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeaponEntity that = (WeaponEntity) o;
        return serialNumber.equals(that.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }

    @Override
    public String toString() {
        return String.format("%s = [With id: %s, business key: %s and version: %s.]",
                this.getClass().getName(), id, serialNumber, this.getVersion());
    }

}
