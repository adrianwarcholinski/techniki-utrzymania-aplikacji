package pl.lodz.p.it.ssbd2020.utils.beanvalidation;

/**
 * Klasa narzędziowa przechowująca wyrażenia regularne stosowane w aplikacji.
 */
public class RegexPatterns {
    public static final String PASSWORD = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
    public static final String EMAIL = "^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$";
    public static final String CARD_NUMBER = "^([\\w]{3})+(-[\\w]{3})+(-[\\w]{4})";
    public static final String PHONE_NUMBER = "^\\d+$";
    public static final String FIRST_NAME = "^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż,.'-]{1,20}$";
    public static final String LAST_NAME = "^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż,.'-]{1,50}$";
    public static final String NAME_AND_SURNAME = "^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.'-]{1,71}$";
    public static final String LOGIN = "^[a-zA-Z0-9]{1,20}$";
    public static final String NAME = "^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż0-9- ,.']{1,50}$";
    public static final String OPINION_CONTENT = "^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.()|\\n\\s$'-]{1,200}$";
    public static final String ALLEY_NAME = "^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż0-9- ,.']{1,50}$";
    public static final String ALLEY_DESCRIPTION = "^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.|\\n\\s$'-]{1,400}$";
    public static final String ALLEY_DIFFICULTY_LEVEL_NAME = "^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż-]{1,20}$";
    public static final String WEAPON_MODEL_DESCRIPTION = "^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.:|\\n\\s$'-]{1,400}$";
    public static final String WEAPON_MODEL_NAME = "^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.'-]{1,20}$";
    public static final String WEAPON_MODEL_CATEGORY = "^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,._'-]{1,50}$";
    public static final String WEAPON_SERIAL_NUMBER = "^^[A-Z0-9]{2}-[A-Z0-9]-[A-Z0-9]{5}-[A-Z0-9]{2}-[A-Z0-9]{2}-[A-Z0-9]{8}$";
}

