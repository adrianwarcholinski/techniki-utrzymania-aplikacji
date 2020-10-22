package pl.lodz.p.it.ssbd2020.mok.endpoints.dto;

import pl.lodz.p.it.ssbd2020.entities.AccountEntity;

import java.time.LocalDateTime;

/**
 * Klasa reprezentująca dane użytkownika do raportu.
 */
public class AccountReportDto {

    /**
     * Pole reprezentujące login użytkownika.
     */
    private String login;

    /**
     * Pole reprezentujące ostatnio używany adres IP podczas uwierzytelnienia danego użytkownika.
     */
    private String ip;

    /**
     * Pole reprezentujące ostatni czas w jakim użytkownik próbował się uwierzytelnić.
     */
    private LocalDateTime lastAuthentication;


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getLastAuthentication() {
        return lastAuthentication;
    }

    public void setLastAuthentication(LocalDateTime lastAuthentication) {
        this.lastAuthentication = lastAuthentication;
    }

    /**
     * Metoda statyczna koonwertująca obiekt klasy {@link AccountEntity}, na obiekt klasy {@link AccountDto}.
     *
     * @param accountEntity obiekt klasy {@link AccountReportDto} z danymi użytkownika.
     * @return obiekt klasy {@link AccountReportDto} z danymi użytkownika.
     */
    public static AccountReportDto fromAccountEntity(AccountEntity accountEntity) {
        AccountReportDto accountReportDto = new AccountReportDto();
        accountReportDto.setLogin(accountEntity.getLogin());
        accountReportDto.setIp(accountEntity.getLastUsedIpAddress());
        accountReportDto.setLastAuthentication(accountEntity.getLastSuccessfulAuthentication());
        return accountReportDto;
    }
}
