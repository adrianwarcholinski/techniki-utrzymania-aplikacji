package pl.lodz.p.it.ssbd2020.utils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.identitystore.PasswordHash;

/**
 * Klasa służąca do generowania hasha orazz weryfikacji hasła.
 */
@RequestScoped
public class HashGenerator {

    /**
     * Domyślna implementacja to Pbkdf2PasswordHash.
     * Obiekt klasy odpowiedzialnej za generowanie i weryfikację skrótów haseł.
     **/
    @Inject
    private PasswordHash passwordHash;

    /**
     * Metoda generująca hash hasła
     *
     * @param password hosło z którego zostanie wygenerowany hash.
     * @return hash hasła.
     */
    public String generatePasswordHash(String password) {
        return passwordHash.generate(password.toCharArray());
    }

    /**
     * Metoda weryfikująca hasło.
     *
     * @param passwordToVerify Hasło do zweryfikowania.
     * @param correctPasswordHash Aktualny skrót hasła.
     * @return True - jeśli hasła sie zgadzają, false - jeśli hasła się nie zgadzają.
     */
    public boolean verifyPassword(String passwordToVerify, String correctPasswordHash) {

        return passwordHash.verify(passwordToVerify.toCharArray(), correctPasswordHash);
    }
}
