package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class CardNumberIsAlreadyTakenException extends AppException {

    private static final String MESSAGE_CARD_NUMBER_IS_ALREADY_TAKEN = "Card number is already taken";
    private static final String RESPONSE_CARD_NUMBER_IS_ALREADY_TAKEN = "error.cardNumberIsAlreadyTaken";

    public CardNumberIsAlreadyTakenException() {
        super(MESSAGE_CARD_NUMBER_IS_ALREADY_TAKEN);
    }

    public CardNumberIsAlreadyTakenException(Throwable e) {
        super(MESSAGE_CARD_NUMBER_IS_ALREADY_TAKEN, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_CARD_NUMBER_IS_ALREADY_TAKEN);
    }
}
