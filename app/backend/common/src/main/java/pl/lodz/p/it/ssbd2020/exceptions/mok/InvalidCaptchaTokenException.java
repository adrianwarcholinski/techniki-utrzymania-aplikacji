package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class InvalidCaptchaTokenException extends AppException {

    private static final String MESSAGE_INVALID_CAPTCHA_TOKEN = "Invalid captcha token";
    private static final String RESPONSE_INVALID_CAPTCHA_TOKEN = "error.invalidCaptchaToken";

    public InvalidCaptchaTokenException() {
        super(MESSAGE_INVALID_CAPTCHA_TOKEN);
    }

    public InvalidCaptchaTokenException(Throwable e) {
        super(MESSAGE_INVALID_CAPTCHA_TOKEN, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_INVALID_CAPTCHA_TOKEN);
    }
}
