package pl.lodz.p.it.ssbd2020.mor.endpoints.dto;


import pl.lodz.p.it.ssbd2020.entities.OpinionEntity;
import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.*;

/**
 * Klasa reprezentująca dane opinii przeznaczone do wyświetlenia jako rekord listy.
 */
public class ListOpinionDto {

    /**
     * Pole reprezentujące numer opini.
     */
    @Positive
    private long opinionNumber;

    /**
     * Pole reprezentujące login użytkownika, który wyraził opinie.
     */
    @NotBlank
    @Pattern(regexp = RegexPatterns.LOGIN)
    private String customerLogin;

    /**
     * Pole reprezentujące treść opini.
     */
    @NotBlank
    @Pattern(regexp = RegexPatterns.OPINION_CONTENT)
    private String content;

    /**
     * Pole reprezentujące ocenę wystawioną przez użytkownika.
     */
    @Min(value = 1, message = "Rate has to be between 1 and 5")
    @Max(value = 5, message = "Rate has to be between 1 and 5")
    private int rate;

    public long getOpinionNumber() {
        return opinionNumber;
    }

    public void setOpinionNumber(long opinionNumber) {
        this.opinionNumber = opinionNumber;
    }

    public String getCustomerLogin() {
        return customerLogin;
    }

    public void setCustomerLogin(String customerLogin) {
        this.customerLogin = customerLogin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * Metoda statyczna konwertująca obiekt klasy {@link OpinionEntity}, na obiekt klasy {@link ListOpinionDto}.
     *
     * @param entity obiekt klasy {@link OpinionEntity} z danymy opinii.
     * @return obiekt klasy {@link ListOpinionDto} z danymi opinii.
     */
    public static ListOpinionDto fromOpinionEntity(OpinionEntity entity){
        ListOpinionDto listOpinionDto = new ListOpinionDto();
        listOpinionDto.setContent(entity.getContent());
        listOpinionDto.setCustomerLogin(entity.getCustomer().getAccount().getLogin());
        listOpinionDto.setOpinionNumber(entity.getOpinionNumber());
        listOpinionDto.setRate(entity.getRate());
        return listOpinionDto;
    }
}
