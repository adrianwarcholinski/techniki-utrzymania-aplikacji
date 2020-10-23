package pl.lodz.p.it.ssbd2020.utils;

/**
 * Klasa reprezentująca wiadomość e-mail.
 */
public class Email {

    /**
     * Pole reprezentujące adres e-mail odbiorcy.
     */
    private String receiverAddress;

    /**
     * Pole reprezentujące temat wiadomości e-mail.
     */
    private String subject;

    /**
     * Pole reprezentujące treść wiadomości e-mail.
     */
    private String body;

    public Email(String receiverAddress, String subject, String body) {
        this.receiverAddress = receiverAddress;
        this.subject = subject;
        this.body = body;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
