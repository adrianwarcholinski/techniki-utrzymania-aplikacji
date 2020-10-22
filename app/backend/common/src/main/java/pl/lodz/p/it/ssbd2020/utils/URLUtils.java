package pl.lodz.p.it.ssbd2020.utils;

import pl.lodz.p.it.ssbd2020.exceptions.mok.EncryptException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Klasa pomocnicza do generowania adresu url.
 */
@RequestScoped
public class URLUtils {

    /**
     * Kontekst bezpieczeństwa, który zwraca informacje na temat zalogowanego użytkownika
     */
    @Inject
    private ServletContext servletContext;

    /**
     * Ziarno, które pozwala na szyfrowanie i deszyfrowanie łańcuchów znaków
     */
    @Inject
    private Crypt crypt;

    /**
     * Metoda tworząca URL.
     *
     * @param path   ścieżka do zasobu.
     * @param params składniki parametru.
     * @return URL.
     * @throws EncryptException wyjątek zgłaszany, kiedy wystąpi błąd podczas szyfrowania.
     */
    public String createUrl(String path, String... params) throws EncryptException {
        String url = getApiUrl() + path + createUrlParam(params);
        return String.join("","<br/>","<a name='emailUrl' href='",url,"'>",url,"</a>");
    }

    /**
     * Metoda tworząca parametr adresu url.
     *
     * @param elements Składniki parametru.
     * @return Zaszyfrowany parametr url.
     * @throws EncryptException wyjątek zgłaszany, kiedy wystąpi błąd podczas szyfrowania.
     */
    private String createUrlParam(String... elements) throws EncryptException {
        String concatenated = crypt.encrypt(String.join(";", elements));
        return URLEncoder.encode(concatenated, StandardCharsets.UTF_8);
    }

    /**
     * Metoda poierająca wartość parametru API_URL z deskryptora web.xml.
     *
     * @return wartość parametru API_URL
     */
    private String getApiUrl() {
        return servletContext.getInitParameter("API_URL");
    }
}
