package pl.lodz.p.it.ssbd2020.mok.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.AccountEntity;
import pl.lodz.p.it.ssbd2020.entities.AdminEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Klasa reprezentująca dane numeru karty przeznaczone do edycji.
 */
public class CardNumberDto {

    /**
     * Pole reprezentujące id  numeru karty.
     */
    @NotBlank
    private String id;

    /**
     * Pole reprezentujące numer wersji numeru karty.
     */
    @NotBlank
    private String version;

    /**
     * Pole reprezentujące numer karty użytkownika.
     */
    @Pattern(regexp = RegexPatterns.CARD_NUMBER, message = "CardNumber is not valid")
    private String cardNumber;

    public CardNumberDto() {
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link CardNumberDto}, na obiekt klasy {@link AdminEntity}.
     *
     * @param cardNumberDto obiekt klasy {@link CardNumberDto} z danymi numeru karty użytkownika.
     * @param accountEntity obiekt klasy {@link AccountEntity} z danymi użytkownika.
     * @return obiekt klasy {@link AccountEntity} z danymi numeru karty użytkownika.
     */
    public static AdminEntity convertToAdminEntity(CardNumberDto cardNumberDto, AccountEntity accountEntity) {
        AdminEntity adminEntity = new AdminEntity(
                Long.parseLong(cardNumberDto.getId()),
                accountEntity,
                cardNumberDto.getCardNumber(),
                Long.parseLong(cardNumberDto.getVersion()));
        return adminEntity;
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link AdminEntity}, na obiekt klasy {@link CardNumberDto}.
     *
     * @param entity obiekt klasy {@link AdminEntity} z danymi użytkownika numeru karty użytkownika.
     * @return obiekt klasy {@link CardNumberDto} z danymi numeru karty użytkownika.
     */
    public static CardNumberDto fromAdminEntity(AdminEntity entity) {
        CardNumberDto cardNumberDto = new CardNumberDto();
        cardNumberDto.id = entity.getId().toString();
        cardNumberDto.cardNumber = entity.getCardNumber();
        cardNumberDto.version = Long.toString(entity.getVersion());
        return cardNumberDto;
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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
