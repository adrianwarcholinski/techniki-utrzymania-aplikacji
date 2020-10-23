package pl.lodz.p.it.ssbd2020.exceptions.common;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class CustomerRevokedException extends AppException {
    private static final String MESSAGE_CUSTOMER_DOES_NOT_EXIST = "Account has not customer access level";
    private static final String RESPONSE_CUSTOMER_DOES_NOT_EXIST = "error.accountIsNotCustomer";

    public CustomerRevokedException(){
        super(MESSAGE_CUSTOMER_DOES_NOT_EXIST);
    }

    public CustomerRevokedException(Throwable e){
        super(MESSAGE_CUSTOMER_DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_CUSTOMER_DOES_NOT_EXIST);
    }
}
