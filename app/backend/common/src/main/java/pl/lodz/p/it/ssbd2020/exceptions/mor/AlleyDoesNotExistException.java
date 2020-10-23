package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AlleyDoesNotExistException extends AppException {

    private static final String MESSAGE_ALLEY_DOES_NOT_EXIST = "Alley does not exist";
    private static final String RESPONSE_ALLEY_DOES_NOT_EXIST = "error.alleyDoesNotExist";

    public AlleyDoesNotExistException() {
        super(MESSAGE_ALLEY_DOES_NOT_EXIST);
    }

    public AlleyDoesNotExistException(Throwable e) {
        super(MESSAGE_ALLEY_DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ALLEY_DOES_NOT_EXIST);
    }

}


