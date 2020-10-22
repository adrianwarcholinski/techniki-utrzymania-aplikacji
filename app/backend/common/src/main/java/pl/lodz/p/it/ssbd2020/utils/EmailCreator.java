package pl.lodz.p.it.ssbd2020.utils;

import pl.lodz.p.it.ssbd2020.exceptions.mok.EncryptException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Klasa służąca do generowania wiadomości e-mail.
 */
@RequestScoped
public class EmailCreator {

    @Inject
    private URLUtils urlUtils;

    private static final String CHANGE_EMAIL_SUBJECT_KEY = "change_email";
    private static final String CHANGE_EMAIL_MESSAGE_KEY = "use_link_to_change_email";
    private static final String CHANGE_EMAIL_PATH = "/change-email/";
    private static final String CHANGE_EMAIL_BY_ADMIN_KEY = "admin_changed_email";

    private static final String VERIFY_SUBJECT_KEY = "verify_account";
    private static final String VERIFY_MESSAGE_KEY = "use_link_verify";
    private static final String VERIFY_PATH = "/verify/";

    private static final String VERIFY_CONFIRM_SUBJECT_KEY = "verify_account_confirm";
    private static final String VERIFY_CONFIRM_MESSAGE_KEY = "verify_account_confirm_info";

    private static final String UNLOCK_ACCOUNT_SUBJECT_KEY = "unlocked_topic";
    private static final String UNLOCK_ACCOUNT_MESSAGE_KEY = "account_unlocked";

    private static final String MAKE_RESERVATION_SUBJECT_KEY = "make_reservation_topic";
    private static final String MAKE_RESERVATION_MESSAGE_KEY = "reservation_make";

    private static final String CANCEL_RESERVATION_SUBJECT_KEY = "reservation_topic";
    private static final String CANCEL_RESERVATION_MESSAGE_KEY = "reservation_canceled";

    private static final String CANCEL_OWN_RESERVATION_SUBJECT_KEY = "own_reservation_topic";
    private static final String CANCEL_OWN_RESERVATION_MESSAGE_KEY = "own_reservation_canceled";

    private static final String UPDATE_OWN_RESERVATION_SUBJECT_KEY = "reservation_updated_topic";
    private static final String UPDATE_OWN_RESERVATION_MESSAGE_KEY = "own_reservation_updated";

    private static final String UPDATE_RESERVATION_SUBJECT_KEY = "reservation_updated_topic";
    private static final String UPDATE_RESERVATION_MESSAGE_KEY = "reservation_updated";

    private static final String RESERVATION_WEAPON_MODEL_KEY = "reservation_weapon_model";
    private static final String RESERVATION_NEW_DATA_KEY = "reservation_new_data";
    private static final String RESERVATION_DATA_KEY = "reservation_data";
    private static final String RESERVATION_ALLEY_KEY = "reservation_alley";
    private static final String RESERVATION_START_DATE_KEY = "reservation_start_date";
    private static final String RESERVATION_END_DATE_KEY = "reservation_end_date";

    private static final String LOCK_ACCOUNT_SUBJECT_KEY = "locked_topic";
    private static final String LOCK_ACCOUNT_MESSAGE_KEY = "account_locked";

    private static final String RESET_PASSWORD_SUBJECT_KEY = "reset_pass";
    private static final String RESET_PASSWORD_MESSAGE_KEY = "use_link_reset";
    private static final String RESET_PASSWORD_PATH = "/reset-password/";

    private static final String ADMIN_AUTHENTICATION_SUBJECT_KEY = "admin_authentication_subject";
    private static final String ADMIN_AUTHENTICATION_MESSAGE_KEY = "admin_authentication_message";

    private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd, HH:mm:ss";


    /**
     * Metoda generująca wiodmość e-mail z informacją o aktualizacji rezerwacji.
     *
     * @param language                   Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress            Adres odbiorcy.
     * @param byEmployee                 wskazuje czy rezerwacja została edytowana przez pracownika.
     * @param reservationNumber          numer rezerwacji.
     * @param reservationWeaponModelName nazwa modelu broni
     * @param reservationAlley           tor.
     * @param reservationStartDate       czas początkowy rezerwacji.
     * @param reservationEndDate         czas końcowy rezerwacji.
     * @return Obiekt klasy {@link Email}.
     */
    public Email getEmailForReservationEdit(String language, String receiverAddress, boolean byEmployee,
                                            String reservationNumber, String reservationWeaponModelName,
                                            String reservationAlley, LocalDateTime reservationStartDate,
                                            LocalDateTime reservationEndDate) {
        ResourceBundle resourceBundle = getResourceBundle(language);
        Email email;
        if (byEmployee) {
            email = getEmailWithMessage(language, receiverAddress, UPDATE_RESERVATION_SUBJECT_KEY, UPDATE_RESERVATION_MESSAGE_KEY,
                    reservationNumber);
        } else {
            email = getEmailWithMessage(language, receiverAddress, UPDATE_OWN_RESERVATION_SUBJECT_KEY,
                    UPDATE_OWN_RESERVATION_MESSAGE_KEY,
                    reservationNumber);
        }
        String newLineSeparator = "<br/>";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        StringBuilder reservationDetails = new StringBuilder();
        reservationDetails.append(newLineSeparator).append(resourceBundle.getString(RESERVATION_NEW_DATA_KEY))
                .append(newLineSeparator);
        reservationDetails.append(resourceBundle.getString(RESERVATION_WEAPON_MODEL_KEY)).append(" ")
                .append(reservationWeaponModelName).append(newLineSeparator);
        reservationDetails.append(resourceBundle.getString(RESERVATION_ALLEY_KEY)).append(" ")
                .append(reservationAlley).append(newLineSeparator);
        reservationDetails.append(resourceBundle.getString(RESERVATION_START_DATE_KEY)).append(" ")
                .append(reservationStartDate.format(dateTimeFormatter)).append(newLineSeparator);
        reservationDetails.append(resourceBundle.getString(RESERVATION_END_DATE_KEY)).append(" ")
                .append(reservationEndDate.format(dateTimeFormatter)).append(newLineSeparator);
        return new Email(email.getReceiverAddress(), email.getSubject(), email.getBody() + reservationDetails.toString());
    }

    /**
     * Metoda generująca wiodmość e-mail z informacją o zmianie adresu e-mail i z linkiem potwierdzającym.
     *
     * @param language        Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress Adres odbiorcy.
     * @param byAdmin         Określa czy e-mail jest zmieniany przez użytkownika czy przez administratora.
     * @param params          Elementy parametru URL.
     * @return Obiekt klasy {@link Email}.
     * @throws EncryptException jeśli wystąpi błąd podczas szyfrowania parametrów.
     */
    public Email getEmailForChangeEmail(String language, String receiverAddress, boolean byAdmin, String... params) throws EncryptException {
        ResourceBundle resourceBundle = getResourceBundle(language);
        Email email = getEmailWithUrlAndMessage(language, receiverAddress, CHANGE_EMAIL_SUBJECT_KEY, CHANGE_EMAIL_MESSAGE_KEY, CHANGE_EMAIL_PATH, params);
        if (byAdmin) {
            String adminMessage = resourceBundle.getString(CHANGE_EMAIL_BY_ADMIN_KEY);
            return new Email(email.getReceiverAddress(), email.getSubject(), adminMessage + email.getBody());
        } else {
            return new Email(email.getReceiverAddress(), email.getSubject(), email.getBody());
        }
    }

    /**
     * Metoda generująca wiodmość e-mail z informacją o rozpoczęciu sesji dla konta z administracyjnym poziomem dostępu.
     *
     * @param language        Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress Adres odbiorcy.
     * @param ipAddress       Adres ip z jakiego się uwierzytelniono.
     * @return Obiekt klasy {@link Email}.
     */
    public Email getAdminAuthenticationEmail(String language, String receiverAddress, String ipAddress) {
        return getEmailWithMessage(language, receiverAddress, ADMIN_AUTHENTICATION_SUBJECT_KEY, ADMIN_AUTHENTICATION_MESSAGE_KEY, ipAddress);
    }

    /**
     * Metoda generująca wiodmość e-mail z linkiem aktywacyjnym.
     *
     * @param language        Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress Adres odbiorcy.
     * @param params          Elementy parametru URL.
     * @return Obiekt klasy {@link Email}.
     * @throws EncryptException jeśli wystąpi błąd podczas szyfrowania parametrów.
     */
    public Email getVerificationEmail(String language, String receiverAddress, String... params) throws EncryptException {
        return getEmail(language, receiverAddress, VERIFY_SUBJECT_KEY, VERIFY_MESSAGE_KEY, VERIFY_PATH, params);
    }

    /**
     * Metoda generująca wiodmość e-mail z informacją o zweryfikowaniu konta.
     *
     * @param language        Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress Adres odbiorcy.
     * @param param           Element parametru URL.
     * @return Obiekt klasy {@link Email}.
     */
    public Email getVerificationConfirmEmail(String language, String receiverAddress, String param) {
        return getEmailWithMessage(language, receiverAddress, VERIFY_CONFIRM_SUBJECT_KEY, VERIFY_CONFIRM_MESSAGE_KEY, param);
    }

    /**
     * Metoda generująca wiodmość e-mail z informacją o odblokowaniu konta.
     *
     * @param language        Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress Adres odbiorcy.
     * @return Obiekt klasy {@link Email}.
     */
    public Email getUnlockAccountEmail(String language, String receiverAddress) {
        return getEmailWithoutUrl(language, receiverAddress, UNLOCK_ACCOUNT_SUBJECT_KEY, UNLOCK_ACCOUNT_MESSAGE_KEY);
    }

    /**
     * Metoda generująca wiodmość e-mail z informacją o zablokowaniu konta.
     *
     * @param language        Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress Adres odbiorcy.
     * @return Obiekt klasy {@link Email}.
     */
    public Email getLockAccountEmail(String language, String receiverAddress) {
        return getEmailWithoutUrl(language, receiverAddress, LOCK_ACCOUNT_SUBJECT_KEY, LOCK_ACCOUNT_MESSAGE_KEY);
    }

    /**
     * Metoda generująca wiodmość e-mail z informacją o zresetowaniu hasła i linkiem do wprowadzenia nowego hasła.
     *
     * @param language        Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress Adres odbiorcy.
     * @param params          Elementy parametru URL.
     * @return Obiekt klasy {@link Email}.
     * @throws EncryptException jeśli wystąpi błąd podczas szyfrowania parametrów.
     */
    public Email getResetPasswordEmail(String language, String receiverAddress, String... params) throws EncryptException {
        return getEmail(language, receiverAddress, RESET_PASSWORD_SUBJECT_KEY, RESET_PASSWORD_MESSAGE_KEY, RESET_PASSWORD_PATH, params);
    }

    /**
     * Metoda generująca wiodmość e-mail z informacją o odwołaniu rezerwacji użytkownika.
     *
     * @param language        Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress Adres odbiorcy.
     * @param reservationNumber Numer odwoływanej rezerwacji.
     * @return Obiekt klasy {@link Email}.
     */
    public Email getCancelReservationEmail(String language, String receiverAddress, String reservationNumber) {
        return getEmailCancel(language, receiverAddress, CANCEL_RESERVATION_SUBJECT_KEY, CANCEL_RESERVATION_MESSAGE_KEY, reservationNumber);
    }

    /**
     * Metoda generująca wiodmość e-mail z informacją o odwołaniu własnej rezerwacji.
     *
     * @param language          Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress   Adres odbiorcy.
     * @param reservationNumber Numer odwoływanej rezerwacji.
     * @return Obiekt klasy {@link Email}.
     */
    public Email getCancelOwnReservationEmail(String language, String receiverAddress, String reservationNumber) {
        return getEmailCancel(language, receiverAddress, CANCEL_OWN_RESERVATION_SUBJECT_KEY, CANCEL_OWN_RESERVATION_MESSAGE_KEY, reservationNumber);
    }


    /**
     * Metoda generująca wiadomośc e-mail z informacją o dokonaniu rezerwacji.
     *
     * @param language          Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress   Adres odbiorcy.
     * @param reservationNumber Numer rezerwacji.
     * @param weaponModelName   Nazwa modeu broni.
     * @param alleyName         Nazwa toru.
     * @param startDate         Data rozpoczęcia.
     * @param endDate           Data zakońćzenia.
     * @return Obiekt klasy {@link Email}.
     */
    public Email getMakeReservationEmail(String language, String receiverAddress,
                                         String reservationNumber, String weaponModelName,
                                         String alleyName, LocalDateTime startDate,
                                         LocalDateTime endDate) {
        ResourceBundle resourceBundle = getResourceBundle(language);
        Email email = getEmailWithMessage(language, receiverAddress, MAKE_RESERVATION_SUBJECT_KEY, MAKE_RESERVATION_MESSAGE_KEY, reservationNumber);
        String newLineSeparator = "<br/>";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        String reservationDetails = newLineSeparator + resourceBundle.getString(RESERVATION_DATA_KEY) +
                newLineSeparator +
                resourceBundle.getString(RESERVATION_WEAPON_MODEL_KEY) + " " +
                weaponModelName + newLineSeparator +
                resourceBundle.getString(RESERVATION_ALLEY_KEY) + " " +
                alleyName + newLineSeparator +
                resourceBundle.getString(RESERVATION_START_DATE_KEY) + " " +
                startDate.format(dateTimeFormatter) + newLineSeparator +
                resourceBundle.getString(RESERVATION_END_DATE_KEY) + " " +
                endDate.format(dateTimeFormatter) + newLineSeparator;
        return new Email(email.getReceiverAddress(), email.getSubject(), email.getBody() + reservationDetails);
    }

    /**
     * Metoda generująca wiadomość e-mail z komunikatem oraz linkiem.
     *
     * @param language        Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress Adres odbiorcy.
     * @param subjectKey      Klucz tematu wiadomości.
     * @param messageKey      Klucz komunikatu.
     * @param path            Klucz do ścieżki.
     * @param params          Elementy parametru URL.
     * @return Obiekt klasy {@link Email}.
     * @throws EncryptException jeśli wystąpi błąd podczas szyfrowania parametrów.
     */
    private Email getEmail(String language, String receiverAddress, String subjectKey, String messageKey, String path, String... params) throws EncryptException {
        ResourceBundle resourceBundle = getResourceBundle(language);
        String subject = resourceBundle.getString(subjectKey);
        String message = resourceBundle.getString(messageKey);
        String url = urlUtils.createUrl(path, params);
        return new Email(receiverAddress, subject, String.join(" ", message, url));
    }


    /**
     * Metoda generująca wiadomość e-mail z komunikatem oraz linkiem.
     *
     * @param language        Język w jakim ma zostać wygenerowana wiadomość.
     * @param receiverAddress Adres odbiorcy.
     * @param subjectKey      Klucz tematu wiadomości.
     * @param messageKey      Klucz komunikatu.
     * @param path            Klucz do ścieżki.
     * @param params          Elementy parametru URL.
     * @return Obiekt klasy {@link Email}.
     * @throws EncryptException jeśli wystąpi błąd podczas szyfrowania parametrów.
     */
    private Email getEmailWithUrlAndMessage(String language, String receiverAddress, String subjectKey, String messageKey, String path, String... params) throws EncryptException {
        ResourceBundle resourceBundle = getResourceBundle(language);
        String subject = resourceBundle.getString(subjectKey);
        String message = String.format(resourceBundle.getString(messageKey), params[0]);
        String url = urlUtils.createUrl(path, params);
        return new Email(receiverAddress, subject, String.join(" ", message, url));
    }


    /**
     * Metoda generująca wiadomośc e-mail z komunikatem.
     *
     * @param language        Adres odbiorcy.
     * @param receiverAddress Elementy parametru URL.
     * @param subjectKey      Klucz tematu wiadomości
     * @param messageKey      Klucz komunikatu.
     * @return Obiekt klasy {@link Email}.
     */
    private Email getEmailWithoutUrl(String language, String receiverAddress, String subjectKey, String messageKey) {
        ResourceBundle resourceBundle = getResourceBundle(language);
        String subject = resourceBundle.getString(subjectKey);
        String message = resourceBundle.getString(messageKey);
        return new Email(receiverAddress, subject, message);
    }

    /**
     * Metoda generująca wiadomośc e-mail z komunikatem.
     *
     * @param language          Adres odbiorcy.
     * @param receiverAddress   Elementy parametru URL.
     * @param subjectKey        Klucz tematu wiadomości
     * @param messageKey        Klucz komunikatu.
     * @param additionalMessage Dodatkowa wiadomość do maila.
     * @return Obiekt klasy {@link Email}.
     */
    private Email getEmailWithMessage(String language, String receiverAddress, String subjectKey, String messageKey, String additionalMessage) {
        ResourceBundle resourceBundle = getResourceBundle(language);
        String subject = resourceBundle.getString(subjectKey);
        String message = resourceBundle.getString(messageKey);
        message += " - " + additionalMessage;
        return new Email(receiverAddress, subject, message);
    }

    /**
     * Metoda generująca wiadomośc e-mail z komunikatem.
     *
     * @param language          Adres odbiorcy.
     * @param receiverAddress   Elementy parametru URL.
     * @param subjectKey        Klucz tematu wiadomości
     * @param messageKey        Klucz komunikatu.
     * @param additionalMessage Dodatkowa wiadomość do maila.
     * @return Obiekt klasy {@link Email}.
     */
    private Email getEmailCancel(String language, String receiverAddress, String subjectKey, String messageKey, String additionalMessage) {
        ResourceBundle resourceBundle = getResourceBundle(language);
        String subject = resourceBundle.getString(subjectKey);
        String message = String.format(resourceBundle.getString(messageKey), additionalMessage);
        return new Email(receiverAddress, subject, message);
    }

    /**
     * Metoda zwracająca pakiet zsobów słownikowych dla wybranego języka
     *
     * @param language język
     * @return {@link ResourceBundle} pakiet zasobów słownikowych
     */
    private ResourceBundle getResourceBundle(String language) {
        Locale loc = language != null && language.toLowerCase().equals("pl") ? new Locale("pl") : new Locale("en");
        return ResourceBundle.getBundle("Labels", loc);
    }
}
