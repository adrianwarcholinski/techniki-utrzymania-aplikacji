package pl.lodz.p.it.ssbd2020.exceptions.common;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class CustomerDoesNotExistException extends AppException {
    private static final String MESSAGE_CUSTOMER_DOES_NOT_EXIST = "Customer does not exist";
    private static final String RESPONSE_CUSTOMER_DOES_NOT_EXIST = "error.customerDoesNotExist";

    public CustomerDoesNotExistException(){
        super(MESSAGE_CUSTOMER_DOES_NOT_EXIST);
    }

    public CustomerDoesNotExistException(Throwable e){
        super(MESSAGE_CUSTOMER_DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_CUSTOMER_DOES_NOT_EXIST);
    }
}
