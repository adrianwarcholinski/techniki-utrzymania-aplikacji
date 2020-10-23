package pl.lodz.p.it.ssbd2020.utils.jwt;

import java.util.Set;

/***
 * Klasa JWTCredential jest odpowiedzalna za reprezentowanie danych poświadczających: login i rola.
 */
public class JWTCredential {

    /**
     * Pole reprezentujące login użytkownika
     */
    private final String principal;

    /**
     * Pole reprezentujące role użytkownika
     */
    private final Set<String> authorities;

    public JWTCredential(final String principal, final Set<String> authorities) {
        this.principal = principal;
        this.authorities = authorities;
    }

    public String getPrincipal() {
        return principal;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }
}
