export function validatePasswordIsSafe(password) {
    return /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$/i.test(password) || password === "";
}

export function validateEmailAddress(email) {
    return /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(email) || email === "";
}

export function validateCardNumber(cardNumber) {
    return /^([\w]{3})+(-[\w]{3})+(-[\w]{4})/i.test(cardNumber) || cardNumber === "" || cardNumber === undefined;
}

export function validatePhoneNumber(phoneNumber) {
    return /\d{9}/i.test(phoneNumber) || phoneNumber === "" || phoneNumber === undefined;
}

export function validateOnlyDigits(string) {
    return /^\d+$/i.test(string);
}

export function validateFirstName(arg) {
    return /^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż,.'-]{1,20}$/i.test(arg)
        || arg === "";
}

export function validateLastName(arg) {
    return /^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż,.'-]{1,50}$/i.test(arg)
        || arg === "";
}

export function validateNameAndSurname(arg) {
    return /^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.'-]{1,71}$/i.test(arg)
        || arg === "";
}

export function validateLogin(arg) {
    return /^[a-zA-Z0-9]{1,20}$/i.test(arg) || arg === "";
}

export function validateDescription(arg) {
    return /^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.|\n\s$'-]{1,400}$/i.test(arg);
}

export function validateAlleyName(arg) {
    return /^[a-zA-ZĄąĆćĘęŁłŃńÓóŚśŹźŻż0-9- ,.']{1,50}$/i.test(arg) || arg === "";
}

export function validateAlleyDescription(arg) {
    return /^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.()|\n\s$'-]{1,400}$/i.test(arg) || arg === "";
}

export function validateWeaponModelDescription(arg) {
    return /^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.:|\n\s$'-]{1,400}$/i.test(arg) || arg === "";
}

export function validateWeaponModelName(arg) {
    return /^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.'-]{1,20}$/i.test(arg) || arg === "";
}

export function validateCaliber(arg) {
    return /^[0-9]+\.?[0-9]*$/i.test(arg) || arg === "";
}

export function validateMagazineCapacity(arg) {
    return /^\d+$/i.test(arg) || arg === "";
}

export function validateOpinionContent(arg) {
    return /^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ,.()|\n\s$'-]{1,200}$/i.test(arg) || arg === "";
}
