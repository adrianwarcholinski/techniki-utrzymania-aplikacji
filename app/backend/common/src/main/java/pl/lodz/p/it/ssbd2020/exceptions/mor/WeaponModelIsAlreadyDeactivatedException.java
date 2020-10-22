package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class WeaponModelIsAlreadyDeactivatedException extends AppException {

    private static final String MESSAGE_WEAPON_MODEL_IS_ALREADY_DEACTIVATED = "Weapon model has been already deactivated";
    private static final String RESPONSE_WEAPON_MODEL_IS_ALREADY_DEACTIVATED = "error.weaponModelIsAlreadyDeactivated";

    public WeaponModelIsAlreadyDeactivatedException() {
        super(MESSAGE_WEAPON_MODEL_IS_ALREADY_DEACTIVATED);
    }

    public WeaponModelIsAlreadyDeactivatedException(Throwable e) {
        super(MESSAGE_WEAPON_MODEL_IS_ALREADY_DEACTIVATED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_WEAPON_MODEL_IS_ALREADY_DEACTIVATED);
    }
}

