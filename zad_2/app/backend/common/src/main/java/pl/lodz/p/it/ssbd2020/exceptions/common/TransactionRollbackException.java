package pl.lodz.p.it.ssbd2020.exceptions.common;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class TransactionRollbackException extends AppException {

    private static final String MESSAGE_TRANSACTION_ROLLBACK = "Transaction has ended with rollback";
    private static final String RESPONSE_INTERNAL_PROBLEM = "error.internalProblem";

    public TransactionRollbackException() {
        super(MESSAGE_TRANSACTION_ROLLBACK);
    }

    public TransactionRollbackException(Throwable e) {
        super(MESSAGE_TRANSACTION_ROLLBACK, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_INTERNAL_PROBLEM);
    }
}
