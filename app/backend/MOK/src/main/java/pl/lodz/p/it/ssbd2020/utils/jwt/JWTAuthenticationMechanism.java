package pl.lodz.p.it.ssbd2020.utils.jwt;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;


/**
 * Klasa odpowiedzialna za uwierzytelnienie użytkownika.
 */
@RequestScoped
public class JWTAuthenticationMechanism implements HttpAuthenticationMechanism {


    /**
     * Kontekst serwletu potrzebny do odczytania parametrów z deskryptora web.xml.
     */
    @Inject
    private ServletContext servletContext;

    /**
     * Ziarno pozwalające na odczytywanie danych z żetonu JWT oraz zapisu danych w postaci żetonu JWT.
     */
    @Inject
    private JWTTokenUtils jwtTokenUtils;

    /**
     * Pole reprezentujące stałą odpowiedzialną za nazwę ciasteczka z tokenem JWT.
     */
    private String COOKIE_NAME;

    /**
     * Metoda wykonująca się zaraz po utworzeniu obiektu klasy, służąca do odczytywania
     * parametrów z deskryptora web.xml.
     */
    @PostConstruct
    public void init() {
        COOKIE_NAME = servletContext.getInitParameter("COOKIE_NAME");
    }

    /**
     * Metoda służąca do walidacji żądania Http, jest wywoływana w odpowiedzi na żądanie klienta do zasobu.
     *
     * @param httpServletRequest  żądanie utworzone przez klienta
     * @param httpServletResponse odpowiedź jaka będzie przekazana klientowi
     * @param httpMessageContext  kontekst do interakcji z kontenerem
     * @return zwraca wynik procesu uwierzytelnienia
     */ 
    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpMessageContext httpMessageContext){
        if(httpServletRequest.getCookies() != null){
            Optional<Cookie> cookieOptional = Arrays.stream(httpServletRequest.getCookies()).filter(c -> c.getName().equals(COOKIE_NAME)).findFirst();
            if(cookieOptional.isPresent()){
                Cookie cookie = cookieOptional.get();
                String token = cookie.getValue();
                try {
                    if( jwtTokenUtils.validateToken(cookie.getValue())) {
                        JWTCredential jwtCredential = jwtTokenUtils.getCredential(token);
                        httpServletResponse.addCookie(jwtTokenUtils.refreshCookie(jwtCredential.getPrincipal(), jwtCredential.getAuthorities()));
                        return httpMessageContext.notifyContainerAboutLogin(jwtCredential.getPrincipal(), jwtCredential.getAuthorities());
                    } else {
                        return httpMessageContext.responseUnauthorized();
                    }
                } catch (Exception e) {
                    return httpMessageContext.doNothing();
                }
            } else{
                if (httpMessageContext.isProtected()) {
                    return httpMessageContext.responseUnauthorized();
                }
            }
        }

        return httpMessageContext.doNothing();
    }

}
