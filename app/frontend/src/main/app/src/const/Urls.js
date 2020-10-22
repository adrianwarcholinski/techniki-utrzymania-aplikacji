export const urls = {
    login: process.env.PUBLIC_URL + '/login',
    register: process.env.PUBLIC_URL + '/register',
    dashboard: process.env.PUBLIC_URL + '/dashboard',
    adminReport: process.env.PUBLIC_URL + '/dashboard/report',
    allAccounts: process.env.PUBLIC_URL + '/dashboard/accounts',
    createNewAccount: process.env.PUBLIC_URL + '/dashboard/create-account',
    accountDetails: process.env.PUBLIC_URL + '/dashboard/accounts/details/:login',
    alleyDetails: process.env.PUBLIC_URL + '/dashboard/alleys/details/:name',
    ownAccount: process.env.PUBLIC_URL + '/dashboard/own-account',
    example: process.env.PUBLIC_URL + '/dashboard/example',
    addNewAlley: process.env.PUBLIC_URL + '/dashboard/alleys/add-alley',
    newAccountVerify: process.env.PUBLIC_URL + "/verify/:toVerify",
    changeEmailVerify: process.env.PUBLIC_URL + '/change-email/:toVerify',
    resetPasswordInit: process.env.PUBLIC_URL + '/init-reset',
    resetPasswordVerify: process.env.PUBLIC_URL + '/reset-password/:toVerify',
    makeReservation: process.env.PUBLIC_URL + '/dashboard/make-reservation',
    allAlleys: process.env.PUBLIC_URL + '/dashboard/alleys',
    allReservation: process.env.PUBLIC_URL + '/dashboard/reservations',
    reservationDetails: process.env.PUBLIC_URL + '/dashboard/reservations/details/:reservationNumber',
    allWeaponModels: process.env.PUBLIC_URL + '/dashboard/weaponModels',
    addNewWeaponModel: process.env.PUBLIC_URL + '/dashboard/weapon-models/add-weapon-model',
    allWeapons: process.env.PUBLIC_URL + '/dashboard/weapons',
    addWeapon: process.env.PUBLIC_URL + '/dashboard/weapons/add-weapon',
    weaponModelDetails: process.env.PUBLIC_URL + '/dashboard/weaponModels/details/:name'
};

export const breadcrumbsNameMap = {
    '/dashboard': 'breadcrumbs.dashboard',
    '/dashboard/accounts': 'breadcrumbs.accounts',
    '/dashboard/create-account': 'breadcrumbs.createAccount',
    '/dashboard/accounts/details': 'breadcrumbs.accountDetails',
    '/dashboard/reservations': 'breadcrumbs.reservations',
    '/dashboard/reservations/details': 'breadcrumbs.reservationDetails',
    '/dashboard/own-account': 'breadcrumbs.myAccount',
    '/dashboard/example': 'breadcrumbs.example',
    '/dashboard/report': 'breadcrumbs.report',
    '/dashboard/make-reservation': 'breadcrumbs.makeReservation',
    '/dashboard/weaponModels': 'breadcrumbs.weaponModels',
    '/dashboard/weapon-models/add-weapon-model': 'breadcrumbs.addNewWeaponModel',
    '/dashboard/alleys': 'breadcrumbs.alleys',
    '/dashboard/alleys/add-alley':'breadcrumbs.addAlley',
    '/dashboard/alleys/details':'breadcrumbs.alleyDetails',
    '/dashboard/weapons': 'breadcrumbs.weapons',
    '/dashboard/weapons/add-weapon': 'breadcrumbs.addWeapon',
    '/dashboard/weaponModels/details': 'breadcrumbs.weaponModelDetails'
};

export const notRepresentedUrls = [
    "account"
];

export const disabledUrls = [
    "/dashboard/accounts/details",
    "/dashboard/reservations/details",
    "/dashboard/weaponModels/details"
];
