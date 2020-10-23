package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class WeaponCategoryDoesNotExistException extends AppException {
    private static final String MESSAGE_WEAPON_CATEGORY_DOES_NOT_EXIST = "Weapon category does not exists";
    private static final String RESPONSE_WEAPON_CATEGORY_DOES_NOT_EXIST = "error.weaponCategoryDoesNotExists";

    public WeaponCategoryDoesNotExistException() {
        super(MESSAGE_WEAPON_CATEGORY_DOES_NOT_EXIST);
    }

    public WeaponCategoryDoesNotExistException(Throwable e) {
        super(MESSAGE_WEAPON_CATEGORY_DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_WEAPON_CATEGORY_DOES_NOT_EXIST);
    }
}
