package pl.lodz.p.it.ssbd2020.mok.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.*;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane użytkownika przeznaczone do edycji.
 */
public class EditAccountDto implements Serializable {

    /**
     * Pole reprezentujące identyfikator użytkownika.
     */
    @NotBlank
    private String id;

    /**
     * Pole reprezentujące login użytkownika.
     */
    @NotBlank
    @Size(max = 20, message = "Login maximum length is 20")
    @Pattern(regexp = RegexPatterns.LOGIN, message = "Login is not valid")
    private String login;

    /**
     * Pole reprezentujące email użytkownika.
     */
    @Email(regexp = RegexPatterns.EMAIL, message = "Email is not valid")
    @NotBlank
    @Size(max = 50, message = "Email maximum length is 50")
    private String email;

    /**
     * Pole reprezentujące imię użytkownika.
     */
    @NotBlank
    @Size(max = 20, message = "Name maximum length is 20")
    @Pattern(regexp = RegexPatterns.FIRST_NAME, message = "Name is not valid")
    private String name;

    /**
     * Pole reprezentujące nazwisko użytkownika.
     */
    @NotBlank
    @Size(max = 50, message = "Surname maximum length is 50")
    @Pattern(regexp = RegexPatterns.LAST_NAME, message = "Surname is not valid")
    private String surname;

    /**
     * Pole reprezentujące numer karty użytkownika.
     */
    private CardNumberDto cardNumberDto;

    /**
     * Pole reprezentujące numer telefonu użytkownika.
     */
    private PhoneNumberDto phoneNumberDto;

    /**
     * Pole reprezentujące firmowy numer telefonu użytkownika.
     */
    private WorkPhoneNumberDto workPhoneNumberDto;

    /**
     * Pole reprezentujące numer wersji użytkownika.
     */
    @NotBlank
    private String version;

    public EditAccountDto() {
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link EditAccountDto}, na obiekt klasy {@link AccountEntity}.
     *
     * @param accountDto obiekt klasy {@link EditAccountDto} z danymi użytkownika.
     * @return obiekt klasy {@link AccountEntity} z danymi użytkownika.
     */
    public static AccountEntity convertToAccountEntity(EditAccountDto accountDto) {
        return new AccountEntity(
                Long.parseLong(accountDto.getId()),
                accountDto.getLogin(),
                accountDto.getEmail(),
                accountDto.getName(),
                accountDto.getSurname(),
                Long.parseLong(accountDto.version)
        );
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link AccountEntity}, na obiekt klasy {@link EditAccountDto}.
     *
     * @param entity obiekt klasy {@link AccountEntity} z danymi użytkownika.
     * @return obiekt klasy {@link EditAccountDto} z danymi użytkownika.
     */
    public static EditAccountDto fromAccountEntity(AccountEntity entity) {
        EditAccountDto accountDto = new EditAccountDto();
        accountDto.id = entity.getId().toString();
        accountDto.login = entity.getLogin();
        accountDto.email = entity.getEmail();
        accountDto.name = entity.getName();
        accountDto.surname = entity.getSurname();
        accountDto.version = Long.toString(entity.getVersion());

        for (AccessLevelEntity ent : entity.getAccessLevels()) {
            if (ent.isActive()) {
                if (ent instanceof AdminEntity)
                    accountDto.cardNumberDto = CardNumberDto.fromAdminEntity((AdminEntity) ent);
                if (ent instanceof CustomerEntity)
                    accountDto.phoneNumberDto = PhoneNumberDto.fromCustomerEntity((CustomerEntity) ent);
                if (ent instanceof EmployeeEntity)
                    accountDto.workPhoneNumberDto = WorkPhoneNumberDto.fromEmployeeEntity((EmployeeEntity) ent);
            }
        }

        return accountDto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public CardNumberDto getCardNumberDto() {
        return cardNumberDto;
    }

    public void setCardNumberDto(CardNumberDto cardNumberDto) {
        this.cardNumberDto = cardNumberDto;
    }

    public PhoneNumberDto getPhoneNumberDto() {
        return phoneNumberDto;
    }

    public void setPhoneNumberDto(PhoneNumberDto phoneNumberDto) {
        this.phoneNumberDto = phoneNumberDto;
    }

    public WorkPhoneNumberDto getWorkPhoneNumberDto() {
        return workPhoneNumberDto;
    }

    public void setWorkPhoneNumberDto(WorkPhoneNumberDto workPhoneNumberDto) {
        this.workPhoneNumberDto = workPhoneNumberDto;
    }
}
