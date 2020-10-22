package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class DecryptException extends AppException {

    private static final String MESSAGE_DECRYPTION_FAILED = "Decryption failed";
    private static final String RESPONSE_DECRYPTION_FAILED = "error.internalProblem";

    public DecryptException() {
        super(MESSAGE_DECRYPTION_FAILED);
    }

    public DecryptException(Throwable e) {
        super(MESSAGE_DECRYPTION_FAILED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_DECRYPTION_FAILED);
    }
}
