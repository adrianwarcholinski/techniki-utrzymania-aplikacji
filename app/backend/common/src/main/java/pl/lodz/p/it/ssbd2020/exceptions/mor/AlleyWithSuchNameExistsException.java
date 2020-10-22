package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AlleyWithSuchNameExistsException extends AppException {

    private static final String MESSAGE_ALLEY_WITH_SUCH_NAME_EXISTS = "Alley with such name exists";
    private static final String RESPONSE_ALLEY_WITH_SUCH_NAME_EXISTS = "error.alleyWithSuchNameExists";

    public AlleyWithSuchNameExistsException() {
        super(MESSAGE_ALLEY_WITH_SUCH_NAME_EXISTS);
    }

    public AlleyWithSuchNameExistsException(Throwable e) {
        super(MESSAGE_ALLEY_WITH_SUCH_NAME_EXISTS, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ALLEY_WITH_SUCH_NAME_EXISTS);
    }
}
