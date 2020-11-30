package pl.lodz.p.it.ssbd2020.utils.endpoint;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InternalProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.TransactionRollbackException;
import pl.lodz.p.it.ssbd2020.utils.manager.Manager;

import javax.ejb.*;
import javax.ws.rs.Produces;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa abstrakcyjna, która powinna być dziedziczona przez klasy punktów końcowych,
 * które korzystają z mechanizmu powtarzania transakcji.
 */

public abstract class Endpoint {

    /**
     * Maksymalna liczba podejmowanych prób powtarzania transakcji.
     */
    protected int maxTransactions;

    /**
     * Metoda wykonująca metodę menadżera, która zwraca obiekt dowolnego typu.
     * Jeżeli transakcja nie powiedzie się z powodu wyjątku EJBTransactionRolledbackException
     * bądź jej status będzie wskazywał, że została wycofana, będzie ona powtarzana
     * maksymalnie {@link #maxTransactions} razy.
     *
     * @param manager  obiekt menadżera, którego metoda jest wywoływana
     * @param supplier metoda wywoływana maksymalnie {@link #maxTransactions} razy
     * @return obiekt zwracany przez metodę przekazaną w parametrze {@code supplier}
     * @throws AppException jeśli metoda {@link AppSupplier} zgłosi wyjątek {@link AppException}.
     */
    protected Object performTransaction(Manager manager, AppSupplier supplier) throws AppException {
        int retriesCount = 0;
        Object result = null;
        boolean rollback;

        do {
            try {
                result = supplier.execute();
                rollback = manager.isLastTransactionRollback();
            } catch (EJBTransactionRolledbackException e) {
                Logger.getGlobal().log(Level.WARNING, "EJBTransactionRolledbackException " + e.getCause().getMessage());
                rollback = true;
            } catch (TransactionRolledbackLocalException e) {
                Logger.getGlobal().log(Level.WARNING, "TransactionRolledbackLocalException " + e.getCause().getMessage());
                rollback = true;
            } catch (EJBAccessException e) {
                Logger.getGlobal().log(Level.WARNING, "EJBAccessException " + e.getCause().getMessage());
                throw new InternalProblemException();
            } catch (EJBTransactionRequiredException e) {
                Logger.getGlobal().log(Level.WARNING, "EJBTransactionRequiredException " + e.getCause().getMessage());
                throw new InternalProblemException();
            } catch (NoSuchEJBException e) {
                Logger.getGlobal().log(Level.WARNING, "NoSuchEJBException " + e.getCause().getMessage());
                throw new InternalProblemException();
            } catch (EJBException e) {
                Logger.getGlobal().log(Level.WARNING, "EJBException " + e.getCause().getMessage());
                throw new InternalProblemException();
            } finally {
                retriesCount++;
            }
        } while (rollback && retriesCount < maxTransactions);

        if (retriesCount == maxTransactions) {
            throw new TransactionRollbackException();
        }

        return result;
    }

    /**
     * Metoda wykonująca metodę menadżera, która nie zwraca wartości.
     *
     * @param manager   obiekt menadżera, którego metoda jest wywoływana
     * @param procedure metoda wywoływana maksymalnie {@link #maxTransactions} razy
     * @throws AppException jeśli metoda {@link AppProcedure} zgłosi wyjątek {@link AppException}.
     */
    protected void performTransaction(Manager manager, AppProcedure procedure) throws AppException {
        int retriesCount = 0;
        boolean rollback;

        do {
            try {
                procedure.execute();
                rollback = manager.isLastTransactionRollback();
            } catch (EJBTransactionRolledbackException e) {
                Logger.getGlobal().log(Level.WARNING, "EJBTransactionRolledbackException " + e.getCause().getMessage());
                rollback = true;
            } catch (TransactionRolledbackLocalException e) {
                Logger.getGlobal().log(Level.WARNING, "TransactionRolledbackLocalException " + e.getCause().getMessage());
                rollback = true;
            } catch (EJBAccessException e) {
                Logger.getGlobal().log(Level.WARNING, "EJBAccessException " + e.getCause().getMessage());
                throw new InternalProblemException();
            } catch (EJBTransactionRequiredException e) {
                Logger.getGlobal().log(Level.WARNING, "EJBTransactionRequiredException " + e.getCause().getMessage());
                throw new InternalProblemException();
            } catch (NoSuchEJBException e) {
                Logger.getGlobal().log(Level.WARNING, "NoSuchEJBException " + e.getCause().getMessage());
                throw new InternalProblemException();
            } catch (EJBException e) {
                Logger.getGlobal().log(Level.WARNING, "EJBException " + e.getCause().getMessage());
                throw new InternalProblemException();
            } finally {
                retriesCount++;
            }
        } while (rollback && retriesCount < maxTransactions);

        if (retriesCount == maxTransactions) {
            throw new TransactionRollbackException();
        }
    }

    @FunctionalInterface
    public interface AppSupplier {
        Object execute() throws AppException;
    }

    @FunctionalInterface
    public interface AppProcedure {
        void execute() throws AppException;
    }
}
