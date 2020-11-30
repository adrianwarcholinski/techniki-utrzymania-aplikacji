package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AccessToAnotherCustomersOpinionException extends AppException {

    private static final String MESSAGE_ATTEMPT_TO_EDIT_ANOTHER_CUSTOMERS_OPINION = "Customer cannot access another customer's opinion";
    private static final String RESPONSE_ATTEMPT_TO_EDIT_ANOTHER_CUSTOMERS_OPINION = "error.accessToAnotherCustomersOpinionException";

    public AccessToAnotherCustomersOpinionException() {
        super(MESSAGE_ATTEMPT_TO_EDIT_ANOTHER_CUSTOMERS_OPINION);
    }

    public AccessToAnotherCustomersOpinionException(String message, Throwable e) {
        super(MESSAGE_ATTEMPT_TO_EDIT_ANOTHER_CUSTOMERS_OPINION, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ATTEMPT_TO_EDIT_ANOTHER_CUSTOMERS_OPINION);
    }
}
