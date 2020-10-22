package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.converters.LocalDateTimeConverter;
import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "account", uniqueConstraints = {
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_LOGIN, columnNames = "login"),
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_EMAIL, columnNames = "email")
})
@TableGenerator(name = "AccountIdGen", table = "generator", pkColumnName = "class_name", valueColumnName = "id_range", pkColumnValue = "AccountEntity")
@NamedQueries({
        @NamedQuery(name = "Account.findByLogin", query = "SELECT a FROM AccountEntity a WHERE a.login = :login"),
        @NamedQuery(name = "Account.findByEmail", query = "SELECT a FROM AccountEntity a WHERE a.email = :email"),
        @NamedQuery(name = "Account.findByPhraseInFullName", query = "SELECT a FROM AccountEntity a WHERE LOWER(CONCAT(a.name, a.surname)) LIKE :phrase ORDER BY a.id"),
        @NamedQuery(name = "Account.findAuthenticated", query = "SELECT a FROM AccountEntity a WHERE a.lastSuccessfulAuthentication IS NOT NULL"),
        @NamedQuery(name = "Account.findAllSortedById", query = "SELECT a FROM AccountEntity a ORDER BY a.id"),
})
@SecondaryTable(name = "personal_data", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "id")})
public class AccountEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AccountIdGen")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false, length = 20)
    @NotBlank
    @Pattern(regexp = RegexPatterns.LOGIN, message = "Login is not valid")
    @Size(max = 20, message = "Login maximum length is 20")
    private String login;

    @Column(nullable = false, length = 115)
    @NotBlank
    @Size(max = 115, message = "Password maximum length is 115")
    private String password;

    @Column(nullable = false, length = 50)
    @Email(regexp = RegexPatterns.EMAIL, message = "Email is not valid")
    @NotBlank
    @Size(max = 50, message = "Email maximum length is 50")
    private String email;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private boolean active;

    @Column(table = "personal_data", name = "name", nullable = false, length = 20)
    @NotBlank
    @Pattern(regexp = RegexPatterns.FIRST_NAME, message = "First name is not valid")
    @Size(max = 20, message = "Name maximum length is 20")
    private String name;

    @Column(table = "personal_data", name = "surname", nullable = false, length = 50)
    @NotBlank
    @Pattern(regexp = RegexPatterns.LAST_NAME, message = "Last name is not valid")
    @Size(max = 50, message = "Surname maximum length is 50")
    private String surname;

    @OneToMany(mappedBy = "account", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @NotEmpty
    private List<AccessLevelEntity> accessLevels = new ArrayList<>();

    @Column(name = "last_successful_authentication")
    @Temporal(value = TemporalType.TIMESTAMP)
    @Convert(converter = LocalDateTimeConverter.class)
    @PastOrPresent
    private LocalDateTime lastSuccessfulAuthentication;

    @Column(name = "last_unsuccessful_authentication")
    @Temporal(value = TemporalType.TIMESTAMP)
    @Convert(converter = LocalDateTimeConverter.class)
    @PastOrPresent
    private LocalDateTime lastUnsuccessfulAuthentication;

    @Column(name = "unsuccessful_authentication_count")
    private long unsuccessfulAuthenticationCount;

    @Column(name = "last_used_ip_address", length = 40)
    @Size(min = 7, max = 40)
    private String lastUsedIpAddress;

    public AccountEntity() {
    }

    public AccountEntity(Long id, String login, String email, String name, String surname, long version) {
        super(version);
        this.id = id;
        this.login = login;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.verified = false;
        this.active = false;
    }

    public AccountEntity(String login, String password, String email, String name, String surname) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.verified = false;
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<AccessLevelEntity> getAccessLevels() {
        return accessLevels;
    }

    public void setAccessLevels(List<AccessLevelEntity> accessLevels) {
        this.accessLevels = accessLevels;
    }

    public LocalDateTime getLastSuccessfulAuthentication() {
        return lastSuccessfulAuthentication;
    }

    public void setLastSuccessfulAuthentication(LocalDateTime lastSuccessfulAuthentication) {
        this.lastSuccessfulAuthentication = lastSuccessfulAuthentication;
    }

    public LocalDateTime getLastUnsuccessfulAuthentication() {
        return lastUnsuccessfulAuthentication;
    }

    public void setLastUnsuccessfulAuthentication(LocalDateTime lastUnsuccessfulAuthentication) {
        this.lastUnsuccessfulAuthentication = lastUnsuccessfulAuthentication;
    }

    public long getUnsuccessfulAuthenticationCount() {
        return unsuccessfulAuthenticationCount;
    }

    public void setUnsuccessfulAuthenticationCount(long unsuccessfulAuthenticationCount) {
        this.unsuccessfulAuthenticationCount = unsuccessfulAuthenticationCount;
    }

    public String getLastUsedIpAddress() {
        return lastUsedIpAddress;
    }

    public void setLastUsedIpAddress(String lasUsedIpAddress) {
        this.lastUsedIpAddress = lasUsedIpAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountEntity)) return false;
        AccountEntity that = (AccountEntity) o;
        return Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogin());
    }

    @Override
    public String toString() {
        return String.format("%s = [With id: %s, business key: %s and version: %s.]",
                this.getClass().getName(), id, login, this.getVersion());
    }

    /**
     * Metoda przepisująca dane, które nie zmieniają się w przypadku zmiany szczegółów konta, z jednej encji do drugiej.
     *
     * @param source encja stanowiąca źródło informacji.
     */
    public void copyNotChangedDataAccount(AccountEntity source) {
        this.login = source.getLogin();
        this.accessLevels = source.getAccessLevels();
        this.password = source.getPassword();
        this.lastSuccessfulAuthentication = source.getLastSuccessfulAuthentication();
        this.lastUnsuccessfulAuthentication = source.getLastUnsuccessfulAuthentication();
        this.unsuccessfulAuthenticationCount = source.getUnsuccessfulAuthenticationCount();
        this.active = source.isActive();
        this.verified = source.isVerified();
        this.lastUsedIpAddress = source.getLastUsedIpAddress();
        this.email = source.getEmail();
    }
}
