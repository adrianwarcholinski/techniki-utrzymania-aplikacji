package pl.lodz.p.it.ssbd2020.mok.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.AccountEntity;
import pl.lodz.p.it.ssbd2020.entities.CustomerEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Klasa reprezentująca dane numeru telefonu przeznaczone do edycji.
 */
public class PhoneNumberDto {
    /**
     * Pole reprezentujące id  numeru karty.
     */
    @NotBlank
    private String id;

    /**
     * Pole reprezentujące numer wersji  numeru karty.
     */
    @NotBlank
    private String version;

    /**
     * Pole reprezentujące numer telefonu użytkownika.
     */
    @Size(min = 9, max = 9, message = "PhoneNumber length has to be 9")
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = "PhoneNumber is not valid")
    private String phoneNumber;


    public PhoneNumberDto() {

    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link PhoneNumberDto}, na obiekt klasy {@link CustomerEntity}.
     *
     * @param phoneNumberDto obiekt klasy {@link PhoneNumberDto} z danymi numeru telefonu użytkownika.
     * @param accountEntity  obiekt klasy {@link AccountEntity} z danymi użytkownika.
     * @return obiekt klasy {@link CustomerEntity} z danymi numeru telefonu użytkownika.
     */
    public static CustomerEntity convertToCustomerEntity(PhoneNumberDto phoneNumberDto, AccountEntity accountEntity) {
        return new CustomerEntity(
                Long.parseLong(phoneNumberDto.getId()),
                accountEntity,
                phoneNumberDto.getPhoneNumber(),
                Long.parseLong(phoneNumberDto.getVersion())
        );
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link CustomerEntity}, na obiekt klasy {@link PhoneNumberDto}.
     *
     * @param entity obiekt klasy {@link CustomerEntity} z danymi użytkownika numeru telefonu użytkownika.
     * @return obiekt klasy {@link PhoneNumberDto} z danymi numeru telefonu użytkownika.
     */
    public static PhoneNumberDto fromCustomerEntity(CustomerEntity entity) {
        PhoneNumberDto phoneNumberDto = new PhoneNumberDto();
        phoneNumberDto.id = entity.getId().toString();
        phoneNumberDto.phoneNumber = entity.getPhoneNumber();
        phoneNumberDto.version = Long.toString(entity.getVersion());
        return phoneNumberDto;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
