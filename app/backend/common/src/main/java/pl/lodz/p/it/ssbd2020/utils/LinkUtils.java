package pl.lodz.p.it.ssbd2020.utils;

import pl.lodz.p.it.ssbd2020.exceptions.mok.DecryptException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.LinkCorruptedException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.LinkExpiredException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;


/**
 * Klasa pomocnicza do walidacji tokenów wysyłanych w wiadomościach e-mail.
 */
@Named
@RequestScoped
public class LinkUtils {

    /**
     * Ziarno umożliwiające odszyfrowanie danych znajdujących się w linku.
     */
    @Inject
    private Crypt crypt;

    /**
     * Metoda odczytująca dane z tokenu będącego częścią linku.
     *
     * @param token token uzyskany z linku
     * @return dane odszyfrowane z tokenu
     * @throws LinkCorruptedException jeśli podany link był nieprawidłowy.
     */
    public String[] extractDataFromTimedToken(String token) throws LinkCorruptedException {
        String[] decrypted;
        try {
            decrypted = crypt.decrypt(token).split(";");
        } catch (DecryptException e) {
            throw new LinkCorruptedException(e);
        }
        return decrypted;
    }

    /**
     * Metoda walidująca dane zapisane w tokenie, którego ważność jest ograniczona czasowo.
     *
     * @param data dane pobrane z tokenu
     * @throws LinkExpiredException   jeśli token wygasł.
     * @throws LinkCorruptedException jeśli podany link był nieprawidłowy.
     */
    public void validateTimedToken(String[] data) throws LinkExpiredException, LinkCorruptedException {
        final int correctAmountOfDataInToken = 2;
        if (data.length != correctAmountOfDataInToken) {
            throw new LinkCorruptedException();
        }

        if (LocalDateTime.now().isAfter(LocalDateTime.parse(data[1]))) {
            throw new LinkExpiredException();
        }
    }
}
