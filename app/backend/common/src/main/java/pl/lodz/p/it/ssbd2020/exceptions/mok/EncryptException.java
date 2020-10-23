package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class EncryptException extends AppException {

    private static final String MESSAGE_ENCRYPTION_FAILED = "Encryption failed";
    private static final String RESPONSE_ENCRYPTION_FAILED = "error.internalProblem";

    public EncryptException() {
        super(MESSAGE_ENCRYPTION_FAILED);
    }

    public EncryptException(Throwable e) {
        super(MESSAGE_ENCRYPTION_FAILED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ENCRYPTION_FAILED);
    }
}
