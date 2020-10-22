package pl.lodz.p.it.ssbd2020.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.ws.rs.core.NewCookie;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Klasa jest odpowiedzialna za generowanie tokenów JWT.
 */
@RequestScoped
public class JWTTokenUtils {

    /**
     * Kontekst serwletu potrzebny do odczytania parametrów z deskryptora web.xml.
     */
    @Inject
    private ServletContext servletContext;

    /**
     * Pole reprezentujące stałą odpowiedzialna za długość życia żetonu.
     */
    private int TOKEN_LIFE_TIME_SECONDS;

    /**
     * Pole reprezentujące klucz, pod którym umieszczone są role użytkownika. Jest on pozyskiwany z deskryptora web.xml.
     */
    private String ROLE_KEY;

    /**
     * Pole reprezentujące generator służący do generowania skrótów na potrzeby żetonów JWT.
     */
    private String HASH_GENERATOR;

    /**
     * Pole reprezentujące stałą odpowiedzialna za nazwę ciasteczka z tokenem JWT.
     */
    private String COOKIE_NAME;
    /**
     * Pole reprezentujące stałą odpowiedzialna za ściezkę w ciasteczku z tokenem JWT.
     */
    private String COOKIE_PATH;
    /**
     * Pole reprezentujące stałą odpowiedzialna za domenę w ciasteczku z tokenem JWT.
     */
    private String COOKIE_DOMAIN;
    /**
     * Pole reprezentujące stałą odpowiedzialna za flagę "Secure" ciasteczka z tokenem JWT.
     */
    private boolean COOKIE_SECURE;
    /**
     * Pole reprezentujące stałą odpowiedzialna za flagę "HTTPOnly" ciasteczka z tokenem JWT.
     */
    private boolean COOKIE_HTTP_ONLY;
    /**
     * Pole reprezentujące stałą odpowiedzialna za komentarz w ciasteczku z tokenem JWT.
     */
    private String COOKIE_COMMENT;

    /**
     * Metoda wykonująca się zaraz po utworzeniu obiektu klasy, służąca do odczytywania
     * parametrów z deskryptora web.xml.
     */
    @PostConstruct
    public void init() {
        ROLE_KEY = servletContext.getInitParameter("ROLE_KEY");
        HASH_GENERATOR = servletContext.getInitParameter("HASH_GENERATOR");
        TOKEN_LIFE_TIME_SECONDS = Integer.parseInt(servletContext.getInitParameter("TOKEN_LIFE_TIME_SECONDS"));
        COOKIE_NAME = servletContext.getInitParameter("COOKIE_NAME");
        COOKIE_PATH = servletContext.getInitParameter("COOKIE_PATH");
        COOKIE_DOMAIN = servletContext.getInitParameter("COOKIE_DOMAIN");
        COOKIE_SECURE = Boolean.parseBoolean(servletContext.getInitParameter("COOKIE_SECURE"));
        COOKIE_HTTP_ONLY = Boolean.parseBoolean(servletContext.getInitParameter("COOKIE_HTTP_ONLY"));
        COOKIE_COMMENT = servletContext.getInitParameter("COOKIE_COMMENT");
    }

    /**
     * Metoda z wykorzystaniem podanego loginu i roli tworzy token JWT o czasie ważności zdefiniowanym w polu
     * {@link JWTTokenUtils#TOKEN_LIFE_TIME_SECONDS} używając funkcji skrótu SHA-512 i generatora zdefiniowanego w polu
     * {@link JWTTokenUtils#HASH_GENERATOR}.
     *
     * @param login unikalny login użytkownika
     * @param roles zbiór roli, które chcemy przechować w tokenie
     * @return token JWT zapisany w String
     */
    public String createToken(String login, Set<String> roles) {
        return Jwts.builder().setSubject(login).claim(ROLE_KEY, String.join(",", roles)).signWith(SignatureAlgorithm.HS512,
                HASH_GENERATOR).setExpiration(new Date(Instant.now().plusMillis(TimeUnit.SECONDS.toMillis(TOKEN_LIFE_TIME_SECONDS)).toEpochMilli())).compact();
    }

    /**
     * Metoda sprawdzająca czy token został utworzony przy uzyciu klucza podpisującego aplikacji,
     * czy nie jest obcym tokenem nie pozwalającym posiadającemu go użytkownikowi na dostęp do aplikacji.
     *
     * @param token JWT token w postaci string.
     * @return wartość true jeśli walidacja tokena sie powiodła, wartość false jeśli jeśli klucz nie był prawidłowy dla tokenu.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(HASH_GENERATOR).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            return false;
        }
    }


    /***
     * Metoda zwracająca login i rolę użytkownika na podstawie żetonu.
     * @param token żeton użytkownika z którego mają zostać pobrane dana poświadczającce
     * @return Zwraca obiekt klasy {@link JWTCredential}
     */
    public JWTCredential getCredential(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(HASH_GENERATOR)
                .parseClaimsJws(token)
                .getBody();

        Set<String> authorities = new HashSet<>(Arrays.asList(claims.get(ROLE_KEY).toString().split(",")));

        return new JWTCredential(claims.getSubject(), authorities);
    }

    /**
     * Metoda tworząca odświeżone ciasteczko z tokenem JWT
     *
     * @param principal login użtkownika
     * @param roles     role użytkownika
     * @return odświeżone ciasteczko zawierające token JWT
     */
    public Cookie refreshCookie(String principal, Set<String> roles) {
        Cookie newCookie = new Cookie(COOKIE_NAME, createToken(principal, roles));
        newCookie.setPath(COOKIE_PATH);
        if (!COOKIE_DOMAIN.isBlank())
            newCookie.setDomain(COOKIE_DOMAIN);
        newCookie.setMaxAge(TOKEN_LIFE_TIME_SECONDS);
        newCookie.setComment(COOKIE_COMMENT);
        newCookie.setSecure(COOKIE_SECURE);
        newCookie.setHttpOnly(COOKIE_HTTP_ONLY);
        newCookie.setVersion(1);
        return newCookie;
    }


    /**
     * Metoda tworząca nowe ciasteczko z tokenem JWT
     *
     * @param principal login użtkownika
     * @param roles     role użytkownika
     * @return nowe ciasteczko zawierające token JWT
     */
    public NewCookie newCookie(String principal, Set<String> roles) {
        return new NewCookie(COOKIE_NAME,
                this.createToken(principal, roles),
                COOKIE_PATH,
                COOKIE_DOMAIN,
                COOKIE_COMMENT,
                TOKEN_LIFE_TIME_SECONDS,
                COOKIE_SECURE,
                COOKIE_HTTP_ONLY
        );
    }
}
