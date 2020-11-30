package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.converters.LocalDateTimeConverter;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservation", uniqueConstraints = {
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_RESERVATION_NUMBER, columnNames = "reservation_number")
})
@TableGenerator(name = "ReservationIdGen", table = "generator", pkColumnName = "class_name", valueColumnName = "id_range", pkColumnValue = "ReservationEntity")
@NamedQueries({
        @NamedQuery(name = "ReservationEntity.findById", query = "SELECT r FROM ReservationEntity r WHERE r.id = :id"),
        @NamedQuery(name = "ReservationEntity.findByReservationNumber",
                query = "SELECT r FROM ReservationEntity r WHERE r.reservationNumber = :reservationNumber"),
        @NamedQuery(name = "ReservationEntity.findByActive", query = "SELECT r FROM ReservationEntity r WHERE r.active = :active"),
        @NamedQuery(name = "ReservationEntity.findByCustomer", query = "SELECT r FROM ReservationEntity r WHERE r.customer.account.login = :login"),
        @NamedQuery(name = "ReservationEntity.findConflictReservationsByWeaponModel", query = "SELECT r FROM " + "ReservationEntity r" +
                " WHERE ((r.startDate <= :startDate AND r.endDate > :startDate) " +
                "OR (r.startDate < :endDate AND r.endDate >= :endDate)" +
                "OR (r.startDate >= :startDate AND r.endDate <= :endDate)) " +
                "AND (r.weapon.weaponModel.name = :weaponModelName " +
                "OR r.alley.name = :alleyName)" +
                "AND r.active = true"),

        @NamedQuery(name = "ReservationEntity.findConflictReservationsByWeapon", query = "SELECT r FROM ReservationEntity r" +
                " WHERE ((r.startDate <= :startDate AND r.endDate > :startDate) " +
                "OR (r.startDate < :endDate AND r.endDate >= :endDate)" +
                "OR (r.startDate >= :startDate AND r.endDate <= :endDate)) " +
                "AND (r.weapon.serialNumber = :weaponSerialNumber " +
                "OR r.alley.name = :alleyName)" +
                "AND r.active = true"),
        @NamedQuery(name = "ReservationEntity.findByAlleyNameAndDate", query = "SELECT r FROM ReservationEntity r WHERE r.alley.name = :name AND  r.startDate >= :startDate AND r.endDate <= :endDate")
})
public class ReservationEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ReservationIdGen")
    @Column(updatable = false)
    private Long id;

    @Column(name = "reservation_number", unique = true, nullable = false, updatable = false)
    @Positive
    private long reservationNumber;

    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "id_customer", referencedColumnName = "id", updatable = false)
    @NotNull
    @Valid
    private CustomerEntity customer;

    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "id_alley", referencedColumnName = "id")
    @NotNull
    @Valid
    private AlleyEntity alley;

    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "id_weapon", referencedColumnName = "id")
    @NotNull
    @Valid
    private WeaponEntity weapon;

    @Column(name = "start_date", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    @Convert(converter = LocalDateTimeConverter.class)
    @NotNull
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    @Convert(converter = LocalDateTimeConverter.class)
    @NotNull
    private LocalDateTime endDate;

    @AssertTrue
    public boolean getDatesCheck() {
        return startDate.isBefore(endDate);
    }

    @Column(name = "active", nullable = false)
    private boolean active;

    public ReservationEntity() {
    }

    public ReservationEntity(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ReservationEntity(long reservationNumber, LocalDateTime startDate, LocalDateTime endDate) {
        this(startDate, endDate);
        this.reservationNumber = reservationNumber;
    }

    public ReservationEntity(Long id, AlleyEntity alley,
                             WeaponEntity weapon, LocalDateTime startDate, LocalDateTime endDate, long version) {
        super(version);
        this.id = id;
        this.alley = alley;
        this.weapon = weapon;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ReservationEntity(long reservationNumber, CustomerEntity customer, AlleyEntity alley,
                             WeaponEntity weapon, LocalDateTime startDate, LocalDateTime endDate) {
        this.reservationNumber = reservationNumber;
        this.customer = customer;
        this.alley = alley;
        this.weapon = weapon;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public long getReservationNumber() {
        return reservationNumber;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public AlleyEntity getAlley() {
        return alley;
    }

    public WeaponEntity getWeapon() {
        return weapon;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setAlley(AlleyEntity alley) {
        this.alley = alley;
    }

    public void setWeapon(WeaponEntity weapon) {
        this.weapon = weapon;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationEntity)) return false;
        ReservationEntity that = (ReservationEntity) o;
        return reservationNumber == that.reservationNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationNumber);
    }

    @Override
    public String toString() {
        return String.format("%s = [With id: %s, business key: %s and version: %s.]",
                this.getClass().getName(), id, reservationNumber, this.getVersion());
    }

    /**
     * Metoda przepisująca dane, które nie zmieniają się w przypadku zmiany szczegółów rezerwacji, z jednej encji do drugiej.
     *
     * @param source encja stanowiąca źródło informacji.
     */
    public void copyNotChangedReservationData(ReservationEntity source) {
        reservationNumber = source.getReservationNumber();
        active = source.isActive();
        customer = source.getCustomer();
    }
}
