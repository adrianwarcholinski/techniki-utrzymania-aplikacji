package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class WeaponModelWithSuchNameExistsException extends AppException {

    private static final String MESSAGE_WEAPON_MODEL_WITH_SUCH_NAME_EXISTS = "Weapon model with such name already exists";
    private static final String RESPONSE_WEAPON_MODEL_WITH_SUCH_NAME_EXISTS = "error.weaponModelWithSuchNameAlreadyExists";

    public WeaponModelWithSuchNameExistsException() {
        super(MESSAGE_WEAPON_MODEL_WITH_SUCH_NAME_EXISTS);
    }

    public WeaponModelWithSuchNameExistsException(Throwable e) {
        super(MESSAGE_WEAPON_MODEL_WITH_SUCH_NAME_EXISTS, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_WEAPON_MODEL_WITH_SUCH_NAME_EXISTS);
    }
}
