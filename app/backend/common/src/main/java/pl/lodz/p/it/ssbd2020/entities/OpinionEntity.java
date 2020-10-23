package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Objects;

@Entity
@Table(name = "opinion", uniqueConstraints = {
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_OPINION_NUMBER, columnNames = "opinion_number"),
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_CUSTOMER_AND_MODEL, columnNames = {"id_weapon_model", "id_customer_data"})
})
@NamedQueries({
        @NamedQuery(name = "OpinionEntity.findAll", query = "SELECT o FROM OpinionEntity o"),
        @NamedQuery(name = "OpinionEntity.findByOpinionNumber", query= "SELECT o from OpinionEntity o where o.opinionNumber = :opinionNumber"),
        @NamedQuery(name = "OpinionEntity.findByWeaponModelName", query = "SELECT o FROM OpinionEntity o WHERE o.weaponModel.name = :weaponModelName ORDER BY o.opinionNumber DESC"),
        @NamedQuery(name = "OpinionEntity.findByWeaponModelNameAndCustomer",
                query = "SELECT o FROM OpinionEntity o WHERE o.weaponModel.name = :weaponModelName AND o.customer.account.login = :login")
})
@TableGenerator(name = "OpinionIdGen", table = "generator", pkColumnName = "class_name", valueColumnName = "id_range", pkColumnValue = "OpinionEntity")
public class OpinionEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "OpinionIdGen")
    @Column(updatable = false)
    private Long id;

    @Column(name = "opinion_number", unique = true, nullable = false, updatable = false)
    private long opinionNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_weapon_model", referencedColumnName = "id", updatable = false)
    @NotNull
    @Valid
    private WeaponModelEntity weaponModel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_customer_data", referencedColumnName = "id", updatable = false)
    @NotNull
    @Valid
    private CustomerEntity customer;

    @Column(nullable = false, length = 200)
    @NotBlank
    @Pattern(regexp = RegexPatterns.OPINION_CONTENT, message = "Invalid opinion content")
    private String content;

    @Column(nullable = false)
    @Min(value = 1, message = "Rate has to be between 1 and 5")
    @Max(value = 5, message = "Rate has to be between 1 and 5")
    private int rate;

    public OpinionEntity() {
    }

    public OpinionEntity(long id, long opinionNumber, long version) {
        super(version);
        this.id = id;
        this.opinionNumber = opinionNumber;
    }

    public OpinionEntity(long opinionNumber, WeaponModelEntity weaponModel,
                         CustomerEntity customer, String content, int rate) {
        this.opinionNumber = opinionNumber;
        this.weaponModel = weaponModel;
        this.customer = customer;
        this.content = content;
        this.rate = rate;
    }

    public OpinionEntity(long opinionNumber, WeaponModelEntity model, CustomerEntity customer, String content,
                         int rate, long version) {
        super(version);
        this.opinionNumber = opinionNumber;
        this.weaponModel = model;
        this.customer = customer;
        this.content = content;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public long getOpinionNumber() {
        return opinionNumber;
    }

    public WeaponModelEntity getWeaponModel() {
        return weaponModel;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public String getContent() {
        return content;
    }

    public int getRate() {
        return rate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setWeaponModel(WeaponModelEntity weaponModel) {
        this.weaponModel = weaponModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpinionEntity)) return false;
        OpinionEntity that = (OpinionEntity) o;
        return opinionNumber == that.opinionNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(opinionNumber);
    }

    @Override
    public String toString() {
        return String.format("%s = [With id: %s, business key: %s and version: %s.]",
                this.getClass().getName(), id, opinionNumber, this.getVersion());
    }

    /**
     * Metoda przepisująca dane, które nie zmieniają się w przypadku zmiany szczegółów opinii, z jednej encji do drugiej.
     *
     * @param source encja stanowiąca źródło informacji.
     */
    public void copyNotChangedData(OpinionEntity source) {
        this.weaponModel = source.weaponModel;
        this.customer = source.customer;
    }
}
