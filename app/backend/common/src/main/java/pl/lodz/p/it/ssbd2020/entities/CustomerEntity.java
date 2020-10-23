package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Entity
@Table(name = "customer_data", uniqueConstraints = {
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_PHONE_NUMBER, columnNames = "phone_number")
})
@NamedQueries({
        @NamedQuery(name = "CustomerEntity.findByPhoneNumber", query = "SELECT a FROM CustomerEntity a WHERE a.phoneNumber = :phoneNumber"),
        @NamedQuery(name = "CustomerEntity.findByLogin", query = "SELECT a FROM CustomerEntity a WHERE a.account.login = :login")
})
@DiscriminatorValue("ROLE_CUSTOMER")
public class CustomerEntity extends AccessLevelEntity {

    @Column(name = "phone_number", nullable = false, length = 9)
    @NotBlank
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = "PhoneNumber is not valid")
    @Size(min = 9, max = 9, message = "PhoneNumber length has to be 9")
    private String phoneNumber;


    public CustomerEntity() {
        super();
    }

    public CustomerEntity(AccountEntity account, String phoneNumber) {
        super(account, "ROLE_CUSTOMER");
        this.phoneNumber = phoneNumber;
    }

    public CustomerEntity(Long id, AccountEntity account, String phoneNumber, long version) {
        super(id, account, "ROLE_CUSTOMER", version);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
