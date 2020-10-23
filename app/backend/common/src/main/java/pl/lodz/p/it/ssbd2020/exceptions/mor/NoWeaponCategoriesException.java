package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class NoWeaponCategoriesException extends AppException {

    private static final String MESSAGE_NO_WEAPON_CATEGORIES = "No weapon categories";
    private static final String RESPONSE_NO_WEAPON_CATEGORIES = "error.noWeaponCategories";

    public NoWeaponCategoriesException() {
        super(MESSAGE_NO_WEAPON_CATEGORIES);
    }

    public NoWeaponCategoriesException(Throwable e) {
        super(MESSAGE_NO_WEAPON_CATEGORIES, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_NO_WEAPON_CATEGORIES);
    }
}
