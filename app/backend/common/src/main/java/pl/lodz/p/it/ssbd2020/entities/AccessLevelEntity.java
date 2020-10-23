package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@TableGenerator(name = "AccessLevelIdGen", table = "generator", pkColumnName = "class_name", valueColumnName = "id_range", pkColumnValue = "AccessLevelEntity")
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "level")
@NamedQueries({
        @NamedQuery(name = "AccessLevelEntity.findByLevel", query = "SELECT a FROM AccessLevelEntity a WHERE a.level = :level"),
        @NamedQuery(name = "AccessLevelEntity.findByAccessLevelAndLogin", query = "SELECT a FROM AccessLevelEntity a WHERE a.level = :level AND a.account.login = :login"),
        @NamedQuery(name = "AccessLevelEntity.countAccountRoles", query = "SELECT COUNT(a) FROM AccessLevelEntity a WHERE a.account.login = :login AND a.active = true")
})
@Table(name = "access_level", uniqueConstraints = {
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_LOGIN_ACCESS_LEVEL, columnNames = {"id_account", "level"})
})
public class AccessLevelEntity extends AbstractEntity {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AccessLevelIdGen")
    @NotNull
    private Long id;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_account", referencedColumnName = "id", nullable = false, updatable = false)
    @NotNull
    @Valid
    private AccountEntity account;

    @Column(nullable = false, updatable = false, length = 20)
    @NotBlank
    @Size(max = 20, message = "Level maximum length is 20")
    private String level;

    @Column(nullable = false)
    private boolean active;

    public AccessLevelEntity() {
    }

    protected AccessLevelEntity(AccountEntity account, String level) {
        this.account = account;
        this.level = level;
        this.active = false;
    }


    public AccessLevelEntity(Long id, AccountEntity account, String level, long version) {
        super(version);
        this.id = id;
        this.account = account;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public String getLevel() {
        return level;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessLevelEntity)) return false;
        AccessLevelEntity that = (AccessLevelEntity) o;
        return account.equals(that.account) &&
                level.equals(that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, level);
    }

    @Override
    public String toString() {
        return String.format("%s = [With id: %s, business key: %s + %s and version: %s.]",
                this.getClass().getName(), id, account.getLogin(), level, this.getVersion());
    }

    /**
     * Metoda przepisująca dane, które nie zmieniają się w przypadku zmiany szczegółów encji dziedziczących po klsie {@link AccessLevelEntity}, z jednej encji do drugiej.
     *
     * @param source encja stanowiąca źródło informacji.
     */
    public void copyNotChangedDataAccessLevel(AccessLevelEntity source) {
        setActive(source.isActive());
    }

}
