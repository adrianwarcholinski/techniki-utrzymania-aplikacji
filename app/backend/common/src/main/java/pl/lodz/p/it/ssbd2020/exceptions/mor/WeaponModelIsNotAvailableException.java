package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class WeaponModelIsNotAvailableException extends AppException {
    private static final String MESSAGE_WEAPON_MODEL_IS_NOT_AVAILABLE = "Weapon model is not available";
    private static final String RESPONSE_WEAPON_MODEL_IS_NOT_AVAILABLE = "error.weaponModelIsNotAvailable";

    public WeaponModelIsNotAvailableException() {
        super(MESSAGE_WEAPON_MODEL_IS_NOT_AVAILABLE);
    }

    public WeaponModelIsNotAvailableException(Throwable e) {
        super(MESSAGE_WEAPON_MODEL_IS_NOT_AVAILABLE, e);
    }


    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_WEAPON_MODEL_IS_NOT_AVAILABLE);
    }
}
