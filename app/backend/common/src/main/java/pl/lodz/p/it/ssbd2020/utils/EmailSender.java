package pl.lodz.p.it.ssbd2020.utils;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Klasa jest odpowiedzialna za wysyłanie maili. Wykoszystuje {@link javax.mail.Session}, do którego
 * przekazujemy właściwości odczytane ze zasobu email za pomocą adnotacji {@link javax.annotation.Resource}.
 */
@Named
@RequestScoped
public class EmailSender {
    /**
     * Obiekt sesji związanej z serwerem pocztowym
     */
    @Resource(lookup = "java:app/SSBDJavaMail")
    private Session mailSession;

    /**
     * Metoda służąca wysyłania maili.
     *
     * @param email Obiekt klasy {@link Email} reprezentujący wysyłaną wiadomość e-mail.
     * @throws MessagingException jeśli wystąpi błąd w konstruktorze klasy {@link InternetAddress}
     * lub w metodach klasy {@link MimeMessage}.
     */
    public void sendEmail(Email email) throws MessagingException {
        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
        InternetAddress[] address = {new InternetAddress(email.getReceiverAddress())};
        message.setRecipients(Message.RecipientType.TO, address);
        message.setSubject(email.getSubject(), String.valueOf(StandardCharsets.UTF_8));
        message.setSentDate(new Date());
        message.setText(email.getBody(), String.valueOf(StandardCharsets.UTF_8), "html");
        Transport.send(message);
    }
}
