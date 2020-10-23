package pl.lodz.p.it.ssbd2020.utils.captcha;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa jest odpowiedzialna za walidację żetonu captcha.
 */
@RequestScoped
@Interceptors(LoggingInterceptor.class)
public class CaptchaUtils {
    /**
     * Kontekst serwletu, który pozwala odczytywać parametry z deskryptora web.xml.
     */
    @Inject
    private ServletContext servletContext;

    /**
     * Pole reprezentujące klucz API Google ReCaptcha.
     */
    private String CAPTCHA_API_KEY;

    /**
     * Metoda odczytująca z deskryptora klucz API Google reCAPTCHA i przypisująca go do pola CAPTCHA_API_KEY.
     */
    @PostConstruct
    public void init() {
        CAPTCHA_API_KEY = servletContext.getInitParameter("CAPTCHA_API_KEY");
    }

    /**
     * Metoda sprawdzająca przy użyciu API Google reCAPTCHA, czy przekazany token captcha jest poprawny.
     *
     * @param token captcha w postaci string.
     * @return wartość true jeśli walidacja tokena sie powiodła, wartość false jeśli się nie powiodła.
     */
    public boolean validateToken(String token) {
        HttpPost post = new HttpPost("https://www.google.com/recaptcha/api/siteverify");
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("secret", CAPTCHA_API_KEY));
        urlParameters.add(new BasicNameValuePair("response", token));

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String responseString = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = Json.createReader(new StringReader(responseString)).readObject();
            return jsonObject.getBoolean("success");
        } catch (IOException e) {
            return false;
        }
    }
}
