package pl.lodz.p.it.ssbd2020.mok.endpoints.dto;

import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Klasa reprezentująca dane zwracane po pomyslnym zalogowaniu
 */
public class LoginResponseDto implements Serializable {
    /**
     * Pole reprezentujące role zalogowanego użytkownika
     */
    @NotNull
    private Set<String> roles;

    /**
     * Pole reprezentujące login zalogowanego użytkownika
     */
    @NotBlank
    @Size(max = 20, message = "Login maximum length is 20")
    @Pattern(regexp = RegexPatterns.LOGIN, message = "Login is not valid")
    private String user;

    /**
     * Pole reprezentujące czas ostatniego niepomyślnego uwierzytelnienia
     */
    @PastOrPresent
    private LocalDateTime failedAuthenticationTime;

    /**
     * Pole reprezentujące czas ostatniego niepomyślnego uwierzytelnienia
     */
    @PastOrPresent
    private LocalDateTime successfulAuthenticationTime;

    public LoginResponseDto() {
    }

    public LoginResponseDto(Set<String> roles, String user, LocalDateTime failedAuthenticationTime, LocalDateTime successfulAuthenticationTime) {
        this.roles = roles;
        this.user = user;
        this.failedAuthenticationTime = failedAuthenticationTime;
        this.successfulAuthenticationTime = successfulAuthenticationTime;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getUser() {
        return user;
    }

    public LocalDateTime getFailedAuthenticationTime() {
        return failedAuthenticationTime;
    }

    public LocalDateTime getSuccessfulAuthenticationTime() {
        return successfulAuthenticationTime;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setFailedAuthenticationTime(LocalDateTime failedAuthenticationTime) {
        this.failedAuthenticationTime = failedAuthenticationTime;
    }

    public void setSuccessfulAuthenticationTime(LocalDateTime successfulAuthenticationTime) {
        this.successfulAuthenticationTime = successfulAuthenticationTime;
    }
}
