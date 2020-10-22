package pl.lodz.p.it.ssbd2020.entities;

import pl.lodz.p.it.ssbd2020.facades.AbstractFacade;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "employee_data", uniqueConstraints = {
        @UniqueConstraint(name = AbstractFacade.CONSTRAINT_UNIQUE_WORK_PHONE_NUMBER, columnNames = "work_phone_number")
})
@NamedQueries({
        @NamedQuery(name = "EmployeeEntity.findByWorkPhoneNumber", query = "SELECT a FROM EmployeeEntity a WHERE a.workPhoneNumber = :workPhoneNumber"),
})
@DiscriminatorValue("ROLE_EMPLOYEE")
public class EmployeeEntity extends AccessLevelEntity {

    @Column(name = "work_phone_number", nullable = false, length = 9)
    @NotBlank
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = "WorkPhoneNumber is not valid")
    @Size(min = 9, max = 9, message = "WorkPhoneNumber length has to be 9")
    private String workPhoneNumber;

    public EmployeeEntity() {
    }

    public EmployeeEntity(AccountEntity account, String workPhoneNumber) {
        super(account, "ROLE_EMPLOYEE");
        this.workPhoneNumber = workPhoneNumber;
    }

    public EmployeeEntity(Long id, AccountEntity account, String workPhoneNumber, long version) {
        super(id, account, "ROLE_CUSTOMER", version);
        this.workPhoneNumber = workPhoneNumber;
    }

    public String getWorkPhoneNumber() {
        return workPhoneNumber;
    }

    public void setWorkPhoneNumber(String workPhoneNumber) {
        this.workPhoneNumber = workPhoneNumber;
    }
}
