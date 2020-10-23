package pl.lodz.p.it.ssbd2020.utils.manager;

import javax.ejb.EJBException;
import javax.ejb.SessionSynchronization;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interfejs, który powinien być implementowany przez klasy menadżera,
 * które udostępniają informację na temat wyniku ostatniej transakcji.
 */
public interface Manager extends SessionSynchronization {

    /**
     * Implementacja tej metody powinna zwracać wartość określającą, czy ostatnia transakcja wykonana w menadżerze zakończyła się wycofaniem.
     *
     * @return {@code true}, jeżeli ostatnia transakcja zakończyła się wycofaniem.
     * {@code false}, jeżeli ostatnia transakcja zakończyła się zatwierdzeniem.
     */
    boolean isLastTransactionRollback();

    /**
     * Implementacja tej metody powinna ustawiać wartość określającą, czy ostatnia transakcja wykonana w menadżerze zakończyła się zatwierdzeniem.
     *
     * @param committed wartość logiczna, która określa, czy ostatnia transakcja wykonana w menadżerze zakończyła się
     *                  zatwierdzeniem.
     */
    void setLastTransactionCommitted(boolean committed);

    /**
     * Implementacja tej metody powinna zwracać identyfikator ostatniej transakcji wykonanej w menadżerze.
     *
     * @return identyfikator ostatniej transakcji wykonanej w menadżerze.
     */
    String getLastTransactionId();

    /**
     * Implementacja tej metody powinna ustawić identyfikator ostatniej transakcji wykonanej w menadżerze.
     *
     * @param id identyfikator ostatniej transakcji wykonanej w menadżerze.
     */
    void setLastTransactionId(String id);

    /**
     * Implementacja tej metody powinna zwracać login aktualnie zalogowanego użytkownika
     *
     * @return nazwa zalogowanego użytkownika lub
     * UNAUTHORIZED, gdy użytkownik nie jest zalogowany
     */
    String getCurrentUser();

    /**
     * Implementacja tej metody powinna odnotować w dzienniku wpis o nowo rozpoczętej transakcji.
     * Wywoływana przez rozpoczęciem transakcji.
     *
     * @throws EJBException jeśli zostanie zgłoszony wyjątek {@link EJBException}.
     */
    @Override
    default void afterBegin() throws EJBException {
        setLastTransactionId(Long.toString(System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)));
        Logger.getGlobal().log(Level.INFO, "Transaction with ID " + getLastTransactionId()
                + " has started for user " + getCurrentUser());
    }

    /**
     * Implementacja tej metody powinna odnotować w dzienniku wpis o kończącej się transakcji.
     * Wywoływana przed zakończeniem transakcji.
     *
     * @throws EJBException jeśli zostanie zgłoszony wyjątek {@link EJBException}.
     */
    @Override
    default void beforeCompletion() throws EJBException {
        Logger.getGlobal().log(Level.INFO, "Transaction with ID " + getLastTransactionId()
                + " started for user " + getCurrentUser() + " is finishing");
    }

    /**
     * Implementacja tej metody powinna odnotować w dzienniku fakt zakończenia transakcji wraz z jej wynikiem.\
     * Wywoływana po zakończeniu transakcji.
     *
     * @param committed wartość logiczna określająca, czy transakcja zakończyła się zatwierdzeniem.
     * @throws EJBException jeśli zostanie zgłoszony wyjątek {@link EJBException}.
     */
    @Override
    default void afterCompletion(boolean committed) throws EJBException {
        setLastTransactionCommitted(committed);
        String result = committed ? "commit" : "rollback";
        Logger.getGlobal().log(Level.INFO, "Transaction with ID " + getLastTransactionId() + " started for user "
                + getCurrentUser() + " has finished with " + result);
    }
}
