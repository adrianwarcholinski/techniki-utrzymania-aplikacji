package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "admin_data", uniqueConstraints = {
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_CARD_NUMBER, columnNames = "card_number")
})
@NamedQueries({
        @NamedQuery(name = "AdminEntity.findByActive", query = "SELECT a FROM AdminEntity a WHERE a.active = :active"),
        @NamedQuery(name = "AdminEntity.countActive", query = "SELECT COUNT(a) FROM AdminEntity a WHERE a.active = :active"),
        @NamedQuery(name = "AdminEntity.findByCardNumber", query = "SELECT a FROM AdminEntity a WHERE a.cardNumber = :cardNumber"),
})
@DiscriminatorValue("ROLE_ADMIN")
public class AdminEntity extends AccessLevelEntity {

    @Column(name = "card_number", nullable = false, length = 12)
    @Pattern(regexp = RegexPatterns.CARD_NUMBER, message = "CardNumber is not valid")
    @NotBlank
    @Size(min = 12, max = 12, message = "CardNumber length has to be 12")
    private String cardNumber;

    public AdminEntity() {
        super();
    }

    public AdminEntity(AccountEntity account, String cardNumber) {
        super(account, "ROLE_ADMIN");
        this.cardNumber = cardNumber;
    }

    public AdminEntity(Long id, AccountEntity account, String cardNumber, long version) {
        super(id, account, "ROLE_ADMIN", version);
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
