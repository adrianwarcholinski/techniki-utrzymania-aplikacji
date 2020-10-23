package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class CustomerHasAlreadyAddedOpinionOnThisWeaponModelException extends AppException {

    private static final String MESSAGE_CUSTOMER_HAS_ALREADY_ADDED_OPINION_ON_THIS_WEAPON_MODEL
            = "Customer has already added opinion on this weapon model";
    private static final String RESPONSE_CUSTOMER_HAS_ALREADY_ADDED_OPINION_ON_THIS_WEAPON_MODEL =
            "error.customerHasAlreadyAddedOpinionOnThisWeaponModel";

    public CustomerHasAlreadyAddedOpinionOnThisWeaponModelException() {
        super(MESSAGE_CUSTOMER_HAS_ALREADY_ADDED_OPINION_ON_THIS_WEAPON_MODEL);
    }

    public CustomerHasAlreadyAddedOpinionOnThisWeaponModelException(String message, Throwable e) {
        super(MESSAGE_CUSTOMER_HAS_ALREADY_ADDED_OPINION_ON_THIS_WEAPON_MODEL, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_CUSTOMER_HAS_ALREADY_ADDED_OPINION_ON_THIS_WEAPON_MODEL);
    }
}
