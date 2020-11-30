package pl.lodz.p.it.ssbd2020.utils;

import pl.lodz.p.it.ssbd2020.exceptions.mok.DecryptException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.EncryptException;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Klasa zawierająca metody pozwalające na szyfrowanie i deszyfrowanie wiadomości tekstowych.
 */
@RequestScoped
public class Crypt {

    /**
     * Kontekst serwletu potrzebny do odczytania parametrów z deskryptora web.xml.
     */
    @Inject
    private ServletContext servletContext;

    /**
     * Pole reprezentujące klucz do szyfrowania i deszyfrowania.
     */
    private SecretKeySpec secretKey;

    /**
     * Pole reprezentujące nazwę transformacji.
     */
    private String transformation;

    /**
     * Pole reprezentujące nazwę algorytmu {@link MessageDigest}.
     */
    private String algorithmMD;

    /**
     * Pole reprezentujące długość klucza.
     */
    private int keyLength;

    /**
     * Pole reprezentujące nazwę algorytmu {@link javax.crypto.SecretKeyFactory}.
     */
    private String algorithmSKF;

    /**
     * Metoda wykonująca się zaraz po utworzeniu obiektu klasy, służąca do odczytywania
     * parametrów z deskryptora web.xml.
     */
    @PostConstruct
    public void init() {
        transformation = servletContext.getInitParameter("TRANSFORMATION");
        algorithmMD = servletContext.getInitParameter("ALGORITHM_MD");
        keyLength = Integer.parseInt(servletContext.getInitParameter("KEY_LENGTH"));
        algorithmSKF = servletContext.getInitParameter("ALGORITHM_SKF");
    }

    /**
     * Metoda służąca do ustawiania klucza do szyfrowania i deszyfrowania.
     *
     * @param myKey wartość klucza.
     * @throws NoSuchAlgorithmException jeśli algorytm kryptograficzny odczytany
     * z deskryptora aplikacji nie jest obsługiwany.
     */
    private void setKey(String myKey) throws NoSuchAlgorithmException {
        byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance(algorithmMD);
        key = sha.digest(key);
        key = Arrays.copyOf(key, keyLength);
        secretKey = new SecretKeySpec(key, algorithmSKF);
    }

    /**
     * Metoda zwracająca zaszyfrowany tekst podany w parametrze.
     *
     * @param strToEncrypt tekst przeznaczony do szyfrowania.
     * @return zaszyfrowany tekst - szyfrogram.
     *
     * @throws EncryptException jeśli wystąpi problem podczas deszyfrowania.
     */
    public String encrypt(String strToEncrypt) throws EncryptException {
        try {
            setKey(servletContext.getInitParameter("CRYPT_KEY"));
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new EncryptException();
        }
    }

    /**
     * Metoda zwracająca odszyfrowany teskt podany w parametrze.
     *
     * @param strToDecrypt szyfrogram - zaszyfrowany tekst.
     * @return odszyfrowany szyfrogram.
     *
     * @throws DecryptException jeśli wystąpi problem podczas szyfrowania.
     */
    public String decrypt(String strToDecrypt) throws DecryptException {
        try {
            setKey(servletContext.getInitParameter("CRYPT_KEY"));
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IllegalArgumentException e) {
            throw new DecryptException();
        }
    }
}