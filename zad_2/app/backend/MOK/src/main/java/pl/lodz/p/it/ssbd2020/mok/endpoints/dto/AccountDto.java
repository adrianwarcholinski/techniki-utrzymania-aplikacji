package pl.lodz.p.it.ssbd2020.mok.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.*;
import pl.lodz.p.it.ssbd2020.exceptions.common.InvalidInputException;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane użytkownika.
 */
public class AccountDto implements Serializable {

    /**
     * Pole reprezentujące login użytkownika.
     */
    @NotBlank
    @Size(max = 20, message = "Login maximum length is 20")
    @Pattern(regexp = RegexPatterns.LOGIN, message = "Login is not valid")
    private String login;

    /**
     * Pole reprezentujące hasło użytkownika.
     */
    @Size(min = 8, message = "Password minimum length is 8")
    @Pattern(regexp = RegexPatterns.PASSWORD, message = "Password is not valid")
    private String password;

    /**
     * Pole reprezentujące email użytkownika.
     */
    @Email(regexp = RegexPatterns.EMAIL, message = "Email is not valid")
    @NotBlank
    @Size(max = 50, message = "Email maximum length is 50")
    private String email;

    /**
     * Pole reprezentujące czy użytkownik jest zweryfikowany.
     */
    private boolean verified;

    /**
     * Pole reprezentujące czy użytkownik jest aktywny.
     */
    private boolean active;

    /**
     * Pole reprezentujące imię użytkownika.
     */
    @NotBlank
    @Size(max = 20, message = "Name maximum length is 20")
    @Pattern(regexp = RegexPatterns.FIRST_NAME, message = "First name is not valid")
    private String name;

    /**
     * Pole reprezentujące nazwisko użytkownika.
     */
    @NotBlank
    @Size(max = 50, message = "Surname maximum length is 50")
    @Pattern(regexp = RegexPatterns.LAST_NAME, message = "Last name is not valid")
    private String surname;

    /**
     * Pole reprezentujące numer karty użytkownika.
     */
    @Pattern(regexp = RegexPatterns.CARD_NUMBER, message = "CardNumber is not valid")
    private String cardNumber;

    /**
     * Pole reprezentujące numer telefonu użytkownika.
     */
    @Size(min = 9, max = 9, message = "PhoneNumber length has to be 9")
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = "PhoneNumber is not valid")
    private String phoneNumber;

    /**
     * Pole reprezentujące firmowy numer telefonu użytkownika.
     */
    @Size(min = 9, max = 9, message = "WorkPhoneNumber length has to be 9")
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = "WorkPhoneNumber is not valid")
    private String workPhoneNumber;

    public AccountDto() {
    }

    /**
     * Metoda sprawdzająca czy podano odpowiednie dane, odpowiednie dla przekazanego parametru.
     *
     * @param accessLevel nazwa poziomu dostępu.
     *                    pola są uzupłenione.
     * @throws InvalidInputException jeśli dla danego poziomu dostępu nie posiada on swojego atrybutu.
     */
    public void check(String accessLevel) throws InvalidInputException {
        switch (accessLevel) {
            case "ROLE_ADMIN":
                if (cardNumber == null || phoneNumber != null || workPhoneNumber != null) {
                    throw new InvalidInputException();
                }
                break;
            case "ROLE_EMPLOYEE":
                if (workPhoneNumber == null || cardNumber != null || phoneNumber != null) {
                    throw new InvalidInputException();
                }
                break;
            case "ROLE_CUSTOMER":
                if (phoneNumber == null || cardNumber != null || workPhoneNumber != null) {
                    throw new InvalidInputException();
                }
                break;
            default:
                throw new InvalidInputException();
        }
    }

    /**
     * Metoda statyczna, która tworzy odpowiedni obiekt klasy dziedziczącej po {@link AccessLevelEntity} w zależności
     * od przekazanego parametru.
     *
     * @param accessLevel   nazwa poziomu dostępu.
     * @param accountEntity obiekt klasy encyjnej {@link AccountEntity} reprezentującej użytkownika.
     * @param accountDto    obiekt klasy {@link AccountDto} reprezentującej użytkownika.
     *                      pola są uzupłenione.
     * @return Obiekt klasy {@link AccessLevelEntity}.
     * @throws InvalidInputException jeśli podano inny poziom dostępu niż te znajdujące się w bazie danych.
     */
    public static AccessLevelEntity createAccountAccessLevelEntity(String accessLevel, AccountEntity accountEntity, AccountDto accountDto) throws InvalidInputException {
        switch (accessLevel) {
            case "ROLE_ADMIN":
                return new AdminEntity(accountEntity, accountDto.getCardNumber());
            case "ROLE_EMPLOYEE":
                return new EmployeeEntity(accountEntity, accountDto.getWorkPhoneNumber());
            case "ROLE_CUSTOMER":
                return new CustomerEntity(accountEntity, accountDto.getPhoneNumber());
            default:
                throw new InvalidInputException();
        }
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link AccountDto}, na obiekt klasy {@link AccountEntity}.
     *
     * @param accountDto obiekt klasy {@link AccountDto} z danymi użytkownika.
     * @return obiekt klasy {@link AccountEntity} z danymi użytkownika.
     */
    public static AccountEntity convertToAccountEntity(AccountDto accountDto) {
        return new AccountEntity(
                accountDto.getLogin(),
                accountDto.getPassword(),
                accountDto.getEmail(),
                accountDto.getName(),
                accountDto.getSurname()
        );
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link AccountEntity}, na obiekt klasy {@link AccountDto}.
     *
     * @param entity obiekt klasy {@link AccountEntity} z danymi użytkownika.
     * @return obiekt klasy {@link AccountDto} z danymi użytkownika.
     */
    public static AccountDto fromAccountEntity(AccountEntity entity) {
        AccountDto accountDto = new AccountDto();
        accountDto.login = entity.getLogin();
        accountDto.email = entity.getEmail();
        accountDto.verified = entity.isVerified();
        accountDto.active = entity.isActive();
        accountDto.name = entity.getName();
        accountDto.surname = entity.getSurname();

        for (AccessLevelEntity ent : entity.getAccessLevels()) {
            if (ent instanceof AdminEntity) {
                accountDto.cardNumber = ((AdminEntity) ent).getCardNumber();
            }
            if (ent instanceof CustomerEntity) {
                accountDto.phoneNumber = ((CustomerEntity) ent).getPhoneNumber();
            }
            if (ent instanceof EmployeeEntity) {
                accountDto.workPhoneNumber = ((EmployeeEntity) ent).getWorkPhoneNumber();
            }
        }

        return accountDto;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWorkPhoneNumber() {
        return workPhoneNumber;
    }

    public void setWorkPhoneNumber(String workPhoneNumber) {
        this.workPhoneNumber = workPhoneNumber;
    }
}
