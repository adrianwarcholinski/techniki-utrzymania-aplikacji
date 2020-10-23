package pl.lodz.p.it.ssbd2020.utils;


import java.time.LocalDateTime;

/**
 * Klasa pomocnicza zawierająca czasy ostatniego pomyślnego i niepomyślnego uwierzytelnienia
 */
public class LastAuthenticationTimes {

    /**
     * Pole reprezentujące czas ostatniego niepomyślnego uwierzytelnienia
     */
    private LocalDateTime lastUnsuccessfulAuthenticationTime;
    /**
     * Pole reprezentujące czas ostatniego pomyślnego uwierzytenienia
     */
    private LocalDateTime lastSuccessfulAuthenticationTime;

    public LastAuthenticationTimes(LocalDateTime lastUnsuccessfulAuthenticationTime, LocalDateTime lastSuccessfulAuthenticationTime) {
        this.lastUnsuccessfulAuthenticationTime = lastUnsuccessfulAuthenticationTime;
        this.lastSuccessfulAuthenticationTime = lastSuccessfulAuthenticationTime;
    }

    public LocalDateTime getLastUnsuccessfulAuthenticationTime() {
        return lastUnsuccessfulAuthenticationTime;
    }

    public LocalDateTime getLastSuccessfulAuthenticationTime() {
        return lastSuccessfulAuthenticationTime;
    }
}
