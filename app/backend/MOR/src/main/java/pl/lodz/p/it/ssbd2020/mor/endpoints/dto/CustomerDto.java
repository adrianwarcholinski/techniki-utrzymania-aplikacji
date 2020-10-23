package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.CustomerEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Klasa reprezentująca dane klienta.
 */
public class CustomerDto implements Serializable {

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
     * Pole reprezentujące login klienta.
     */
    @NotBlank
    @Size(max = 20, message = "Login maximum length is 20")
    @Pattern(regexp = RegexPatterns.LOGIN, message = "Login is not valid")
    private String login;

    /**
     * Pole reprezentujące email klienta.
     */
    @Email(regexp = RegexPatterns.EMAIL, message = "Email is not valid")
    @NotBlank
    @Size(max = 50, message = "Email maximum length is 50")
    private String email;

    /**
     * Pole reprezentujące numer telefonu klienta.
     */
    @Size(min = 9, max = 9, message = "PhoneNumber length has to be 9")
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = "PhoneNumber is not valid")
    private String phoneNumber;

    private CustomerDto(String name, String surname, String login, String email,String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public CustomerDto() {
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link CustomerEntity}, na obiekt klasy {@link CustomerDto}.
     *
     * @param customerEntity obiekt klasy {@link CustomerEntity} z danymi klienta.
     * @return obiekt klasy {@link CustomerDto} z danymi klienta.
     */
    public static CustomerDto map(CustomerEntity customerEntity) {
        return new CustomerDto(
                customerEntity.getAccount().getName(),
                customerEntity.getAccount().getSurname(),
                customerEntity.getAccount().getLogin(),
                customerEntity.getAccount().getEmail(),
                customerEntity.getPhoneNumber());
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
