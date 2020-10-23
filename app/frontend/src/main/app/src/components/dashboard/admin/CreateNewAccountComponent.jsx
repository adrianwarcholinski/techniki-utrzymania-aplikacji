import React, {Component} from "react";

import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import FormControl from '@material-ui/core/FormControl';
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import CircularProgress from "@material-ui/core/CircularProgress";
import {withTranslation} from "react-i18next";
import {postFetch} from "../../../utils/fetchUtility";
import ConfirmDialog from "../templates/ConfirmDialog";
import {roles} from "../../../const/Roles";
import {localStorageKeys} from "../../../const/LocalStorageKeys";
import {SnackBarType} from "../templates/SnackBar";
import {Card} from "@material-ui/core";
import CardContent from "@material-ui/core/CardContent";
import CardHeader from "@material-ui/core/CardHeader";
import {
    validateCardNumber,
    validateEmailAddress,
    validateFirstName,
    validateLastName,
    validateLogin,
    validatePasswordIsSafe,
    validatePhoneNumber
} from "../../../utils/regexpUtils";
import TextFieldComponent from "../templates/TextFieldComponent";
import Box from "@material-ui/core/Box";
import Captcha from "../templates/Captcha";
import serverConfig from "../../../properties";

import '../../../resources/styles/CreateNewAccountPage.scss'

class CreateNewAccountComponent extends Component {

    constructor(props) {
        super(props);

        this.captchaRef = React.createRef();

        this.state = {
            login: "",
            firstName: "",
            lastName: "",
            phoneNumber: "",
            workPhoneNumber: "",
            cardNumber: "",
            emailAddress: "",
            password: "",
            repeatedPassword: "",
            loading: false,
            openDialog: false,
            response: null,
            activeRole: roles.customer,
            roles: JSON.parse(localStorage.getItem(localStorageKeys.roles)),
            captchaToken: null,
            filledCaptcha: false
        };
    }

    sendRequest = () => {
        const {t} = this.props;
        let body = {};

        if (this.state.activeRole === roles.admin) {
            body = {
                "login": this.state.login,
                "password": this.state.password,
                "email": this.state.emailAddress,
                "name": this.state.firstName,
                "surname": this.state.lastName,
                "cardNumber": this.state.cardNumber
            };
        } else if (this.state.activeRole === roles.employee) {
            body = {
                "login": this.state.login,
                "password": this.state.password,
                "email": this.state.emailAddress,
                "name": this.state.firstName,
                "surname": this.state.lastName,
                "workPhoneNumber": this.state.workPhoneNumber
            };
        } else {
            body = {
                "login": this.state.login,
                "password": this.state.password,
                "email": this.state.emailAddress,
                "name": this.state.firstName,
                "surname": this.state.lastName,
                "phoneNumber": this.state.phoneNumber
            };
        }

        const header = {
            headers: {
                "accessLevel": this.state.activeRole,
                "Content-Type": "application/json; charset=utf-8",
                "language": navigator.language,
                "captchaToken": this.state.captchaToken
            }
        };

        postFetch(
            '/app/account/add', body, header)
            .then((res) => {
                    if (res.status === 200) {
                        this.props.displaySnackbar(SnackBarType.success, t("addNewAccount.SUCCESS"));
                    } else if(res.status===400){
                        res.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                    }
                }
            ).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("addNewAccount.ERROR"));
        }).finally(() => {
            this.setState({loading: false});
            if (serverConfig.ENABLE_CAPTCHA) {
                this.captchaRef.current.reset();
            }
        });
    }

    handleSubmit = (event) => {
        event.preventDefault();
        if (this.validateInputs()) {
            this.handleOpenDialog();
        }
    }

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({loading: true});
            this.sendRequest();
        }
    }

    handleCloseDialog = () => {
        this.setState({
            openDialog: false
        })
    }

    handleOpenDialog = () => {
        this.setState({
            openDialog: true
        })
    }


    handleChangeRole = (event) => {
        this.setState({
            activeRole: event.target.value
        });

        if (event.target.value === roles.admin) {
            this.setState({phoneNumber: ""});
            this.setState({workPhoneNumber: ""});
        } else if (event.target.value === roles.customer) {
            this.setState({phoneNumber: ""});
            this.setState({cardNumber: ""});
        } else if (event.target.value === roles.customer) {
            this.setState({workPhoneNumber: ""});
            this.setState({cardNumber: ""});
        } else {
            this.setState({workPhoneNumber: ""});
            this.setState({phoneNumber: ""});
            this.setState({cardNumber: ""});
        }
    };

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    validateInputs = () => {
        if (!validatePasswordIsSafe(this.state.password)
            || !validateEmailAddress(this.state.emailAddress)
            || !this.validateNotEmptyInputs()
            || !this.validatePasswords()
            || !validateFirstName(this.state.firstName)
            || !validateLastName(this.state.lastName)
            || !validateLogin(this.state.login)) {
            return false;
        }

        if (this.state.activeRole === roles.admin) {
            return validateCardNumber(this.state.cardNumber);
        } else if (this.state.activeRole === roles.employee) {
            return validatePhoneNumber(this.state.workPhoneNumber);
        } else {
            return validatePhoneNumber(this.state.phoneNumber);
        }
    }

    validateNineCharsPhoneNumberLimit = (event) => {
        if (!this.validateOnlyDigits(event)) {
            const lastCharIndex = event.target.value.length - 1;
            event.target.value = event.target.value.slice(0, lastCharIndex);
        }

        if (event.target.value.length > 9) {
            event.target.value = event.target.value.slice(0, 9);
        }
    }

    validateElevenCharsCardNumberLimit = (event) => {
        if (!this.validateOnlyLetterAndDigits(event)) {
            const lastCharIndex = event.target.value.length - 1;
            event.target.value = event.target.value.slice(0, lastCharIndex);
        }

        if (event.target.value.length > 12) {
            event.target.value = event.target.value.slice(0, 12);
        }
    }

    validateOnlyDigits = (event) => {
        return /^\d+$/i.test(event.target.value);
    }

    validateOnlyLetterAndDigits = (event) => {
        return /^(\w+-?)+$/i.test(event.target.value);
    }

    validatePasswords = () => {
        return this.state.password === this.state.repeatedPassword;
    }

    validateNotEmptyInputs = () => {
        if (this.state.activeRole === roles.admin) {
            return this.state.login !== ''
                && this.state.firstName !== ''
                && this.state.lastName !== ''
                && this.state.emailAddress !== ''
                && this.state.password !== ''
                && this.state.repeatedPassword !== ''
                && this.state.cardNumber !== '';
        } else if (this.state.activeRole === roles.employee) {
            return this.state.login !== ''
                && this.state.firstName !== ''
                && this.state.lastName !== ''
                && this.state.emailAddress !== ''
                && this.state.password !== ''
                && this.state.repeatedPassword !== ''
                && this.state.workPhoneNumber !== '';
        } else {
            return this.state.login !== ''
                && this.state.firstName !== ''
                && this.state.lastName !== ''
                && this.state.emailAddress !== ''
                && this.state.password !== ''
                && this.state.repeatedPassword !== ''
                && this.state.phoneNumber !== '';
        }
    }

    handleFilledCaptcha = (value) => {
        this.setState({
            filledCaptcha: true,
            captchaToken: value
        });
    };

    handleExpiredCaptcha = () => {
        this.setState({
            filledCaptcha: false,
            captchaToken: null
        });
    };

    getCaptchaIfEnabled = () => {
        if (serverConfig.ENABLE_CAPTCHA) {
            return (
                <Box className='register-captcha-box'>
                    <Captcha captchaRef={this.captchaRef}
                             filledCaptchaCallback={this.handleFilledCaptcha}
                             expiredCaptchaCallback={this.handleExpiredCaptcha}/>
                </Box>
            );
        }
    }

    isButtonDisabled = () => {
        if (!this.validateInputs()) {
            return true;
        }

        if (serverConfig.ENABLE_CAPTCHA) {
            return this.state.loading || !this.state.filledCaptcha;
        } else {
            return this.state.loading;
        }
    }

    render() {
        const {t} = this.props;
        document.title = t("addNewAccount.pageTitle");

        return (
            <Card>
                <CardHeader
                    title={t("addNewAccount.addHeader")}
                    className="card-header"
                />
                <CardContent>
                    <Grid container
                          direction="column"
                          justify="center"
                          alignItems="center">
                        <Grid container
                              item
                              justify="center"
                              xs={12}
                              sm={7}
                              md={5}
                              lg={4}
                              xl={3}>
                            <form onSubmit={this.handleSubmit}
                                  method="post">

                                <TextFieldComponent onChange={this.handleChange}
                                                    label={t("addNewAccount.loginField")}
                                                    name="login"
                                                    error={!validateLogin(this.state.login)}
                                                    inputProps={{
                                                        maxLength: 20
                                                    }}/>

                                <TextFieldComponent onChange={this.handleChange}
                                                    label={t("addNewAccount.firstNameField")}
                                                    name="firstName"
                                                    error={!validateFirstName(this.state.firstName)}
                                                    inputProps={{
                                                        maxLength: 20
                                                    }}/>

                                <TextFieldComponent onChange={this.handleChange}
                                                    label={t("addNewAccount.lastNameField")}
                                                    name="lastName"
                                                    error={!validateLastName(this.state.lastName)}
                                                    inputProps={{
                                                        maxLength: 50
                                                    }}/>

                                <TextFieldComponent onChange={this.handleChange}
                                                    label={t("addNewAccount.emailField")}
                                                    name="emailAddress"
                                                    helperText={t("addNewAccount.emailFieldTip")}
                                                    error={!validateEmailAddress(this.state.emailAddress)}/>

                                <FormControl component="fieldset"
                                             style={{color: "black"}}
                                             margin="normal"
                                             fullWidth>

                                    <RadioGroup row aria-label="position"
                                                name="position"
                                                defaultValue="top">
                                        <Grid container
                                              justify='center'
                                              alignItems="center">
                                            <div>
                                                <FormControlLabel
                                                    control={
                                                        <Radio color="primary"
                                                               checked={this.state.activeRole === roles.admin}
                                                               onChange={this.handleChangeRole}
                                                               value={roles.admin}
                                                               inputProps={{'aria-label': 'ROLE_ADMIN'}}
                                                               defaultChecked/>
                                                    }
                                                    label={t("addNewAccount.admin")}
                                                />
                                            </div>
                                            <div>
                                                <FormControlLabel
                                                    control={
                                                        <Radio color="primary"
                                                               checked={this.state.activeRole === roles.employee}
                                                               onChange={this.handleChangeRole}
                                                               value={roles.employee}
                                                               inputProps={{'aria-label': 'ROLE_EMPLOYEE'}}/>
                                                    }
                                                    label={t("addNewAccount.employee")}
                                                />
                                            </div>
                                            <div>
                                                <FormControlLabel
                                                    control={
                                                        <Radio color="primary"
                                                               checked={this.state.activeRole === roles.customer}
                                                               onChange={this.handleChangeRole}
                                                               value={roles.customer}
                                                               inputProps={{'aria-label': 'ROLE_CUSTOMER'}}/>
                                                    }
                                                    label={t("addNewAccount.customer")}
                                                />
                                            </div>
                                        </Grid>
                                    </RadioGroup>
                                </FormControl>

                                {this.state.activeRole === roles.admin &&
                                <TextFieldComponent onChange={this.handleChange}
                                                    onInput={this.validateElevenCharsCardNumberLimit}
                                                    label={t("addNewAccount.cardNumberField")}
                                                    name="cardNumber"
                                                    error={!validateCardNumber(this.state.cardNumber)}/>
                                }

                                {this.state.activeRole === roles.employee &&
                                <TextFieldComponent onChange={this.handleChange}
                                                    onInput={this.validateNineCharsPhoneNumberLimit}
                                                    label={t("addNewAccount.workPhoneNumberField")}
                                                    name="workPhoneNumber"
                                                    error={!validatePhoneNumber(this.state.workPhoneNumber)}/>
                                }

                                {this.state.activeRole === roles.customer &&
                                <TextFieldComponent onChange={this.handleChange}
                                                    onInput={this.validateNineCharsPhoneNumberLimit}
                                                    label={t("addNewAccount.phoneNumberField")}
                                                    name="phoneNumber"
                                                    error={!validatePhoneNumber(this.state.phoneNumber)}/>
                                }

                                <TextFieldComponent type="password"
                                                    onChange={this.handleChange}
                                                    name="password"
                                                    label={t("addNewAccount.passwordField")}
                                                    error={!validatePasswordIsSafe(this.state.password)}
                                                    helperText={t("addNewAccount.passwordSafetyTip")}/>

                                <TextFieldComponent type="password"
                                                    onChange={this.handleChange}
                                                    name="repeatedPassword"
                                                    label={t("addNewAccount.repeatPasswordField")}
                                                    error={!this.validatePasswords()}/>
                                    {this.getCaptchaIfEnabled()}
                                <Button
                                    name="submitButton"
                                    type="submit"
                                    fullWidth
                                    variant="contained"
                                    color="primary"
                                    className="submit-button"
                                    disabled={this.isButtonDisabled()}>
                                    {this.state.loading ? <CircularProgress/> : t("addNewAccount.addButton")}
                                </Button>
                            </form>

                            <ConfirmDialog open={this.state.openDialog}
                                           title={t("addNewAccount.confirmDialogTitle")}
                                           content={t("addNewAccount.confirmDialogContent")}
                                           handleDialogResponse={this.handleDialogResponse}/>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>
        )
    }
}

export default withTranslation()(CreateNewAccountComponent);
