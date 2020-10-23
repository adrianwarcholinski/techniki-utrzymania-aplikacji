package pl.lodz.p.it.ssbd2020.mok.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.AccountEntity;
import pl.lodz.p.it.ssbd2020.entities.EmployeeEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Klasa reprezentująca dane firmowego numeru telefonu przeznaczone do edycji.
 */
public class WorkPhoneNumberDto {
    /**
     * Pole reprezentujące id firmowego numeru telefonu.
     */
    @NotBlank
    private String id;

    /**
     * Pole reprezentujące numer wersji firmowego numeru telefonu.
     */
    @NotBlank
    private String version;

    /**
     * Pole reprezentujące firmowy numer telefonu użytkownika.
     */
    @Size(min = 9, max = 9, message = "WorkPhoneNumber length has to be 9")
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = "WorkPhoneNumber is not valid")
    private String workPhoneNumber;


    public WorkPhoneNumberDto() {

    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link WorkPhoneNumberDto}, na obiekt klasy {@link EmployeeEntity}.
     *
     * @param workPhoneNumber obiekt klasy {@link WorkPhoneNumberDto} z danymi firmowego numeru telefonu użytkownika.
     * @param accountEntity   obiekt klasy {@link AccountEntity} z danymi użytkownika.
     * @return obiekt klasy {@link EmployeeEntity} z danymi firmowego numeru telefonu użytkownika.
     */
    public static EmployeeEntity convertToEmployeeEntity(WorkPhoneNumberDto workPhoneNumber, AccountEntity accountEntity) {
        return new EmployeeEntity(
                Long.parseLong(workPhoneNumber.getId()),
                accountEntity,
                workPhoneNumber.getWorkPhoneNumber(),
                Long.parseLong(workPhoneNumber.getVersion())
        );
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link EmployeeEntity}, na obiekt klasy {@link WorkPhoneNumberDto}.
     *
     * @param entity obiekt klasy {@link EmployeeEntity} z danymi użytkownika firmowego numeru telefonu użytkownika.
     * @return obiekt klasy {@link WorkPhoneNumberDto} z danymi firmowego numeru telefonu użytkownika.
     */
    public static WorkPhoneNumberDto fromEmployeeEntity(EmployeeEntity entity) {
        WorkPhoneNumberDto workPhoneNumber = new WorkPhoneNumberDto();
        workPhoneNumber.id = entity.getId().toString();
        workPhoneNumber.workPhoneNumber = entity.getWorkPhoneNumber();
        workPhoneNumber.version = Long.toString(entity.getVersion());
        return workPhoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWorkPhoneNumber() {
        return workPhoneNumber;
    }

    public void setWorkPhoneNumber(String workPhoneNumber) {
        this.workPhoneNumber = workPhoneNumber;
    }
}
