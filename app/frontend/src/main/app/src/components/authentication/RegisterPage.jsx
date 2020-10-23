import React, {Component} from 'react';
import Container from "@material-ui/core/Container";
import CssBaseline from "@material-ui/core/CssBaseline";
import Icon from "@material-ui/core/Icon";
import LockOutlinedIcon from "@material-ui/icons/LockOutlined";
import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import Link from "@material-ui/core/Link";

import Copyright from "../templates/Copyright";
import {withTranslation} from "react-i18next";
import {
    validateEmailAddress,
    validateFirstName,
    validateLastName,
    validateLogin,
    validateOnlyDigits,
    validatePasswordIsSafe,
    validatePhoneNumber
} from "../../utils/regexpUtils";
import Box from "@material-ui/core/Box";
import serverConfig from "../../properties.json";
import Captcha from "../dashboard/templates/Captcha";
import SnackBar, {SnackBarType} from "../dashboard/templates/SnackBar";
import {postFetch} from "../../utils/fetchUtility";
import CircularProgress from "@material-ui/core/CircularProgress";
import ConfirmDialog from "../dashboard/templates/ConfirmDialog";
import {urls} from "../../const/Urls";

import '../../resources/styles/RegisterPage.scss';

class RegisterPage extends Component {

    constructor(props) {
        super(props);

        this.state = {
            login: "",
            firstName: "",
            lastName: "",
            phoneNumber: "",
            emailAddress: "",
            password: "",
            repeatedPassword: "",
            captchaToken: null,
            loading: false,
            openSnackBar: false,
            response: null,
            openDialog: false
        };

        this.captchaRef = React.createRef();
    }

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    handleSubmit = (event) => {
        event.preventDefault();
        this.handleOpenDialog();
    };

    handleOpenDialog = () => {
        this.setState({
            openDialog: true
        })
    };

    handleCloseDialog = () => {
        this.setState({
            openDialog: false
        })
    };

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({loading: true});
            this.sendRequest();
        }
    };

    sendRequest() {
        const body = {
            "login": this.state.login,
            "password": this.state.password,
            "name": this.state.firstName,
            "surname": this.state.lastName,
            "email": this.state.emailAddress,
            "phoneNumber": this.state.phoneNumber,
        };

        const headers = {
            headers: {
                "Content-Type": "application/json; charset=utf-8",
                "language": navigator.language,
                "captchaToken": this.state.captchaToken
            }
        };

        postFetch('/app/account/register', body, headers)
            .then((response) => {
                if (response.status === 200) {
                    this.handleOpenSnackBar(response.status, null);
                } else if(response.status===400) {
                    response.text()
                        .then((data => {
                            this.handleOpenSnackBar(response.status, data);
                        }))
                }
            }).finally(() => {
            this.setState({
                loading: false,
                filledCaptcha: false,
            });

            if (serverConfig.ENABLE_CAPTCHA) {
                this.captchaRef.current.reset();
            }
        });
    }

    handleOpenSnackBar = (result, reason) => {
        const {t} = this.props;
        const message = t(reason);
        if (result === 200) {
            this.setState({
                response: SnackBarType.success,
                responseMessage: t("registerPage.success"),
                openSnackBar: true
            });
        } else {
            this.setState({
                response: SnackBarType.error,
                responseMessage: message,
                openSnackBar: true
            });
        }
    };

    handleSnackBarClose = () => {
        this.setState({
            openSnackBar: false
        });
    };

    validateNineCharsPhoneNumberLimit = (event) => {
        if (!validateOnlyDigits(event.target.value)) {
            const lastCharIndex = event.target.value.length - 1;
            event.target.value = event.target.value.slice(0, lastCharIndex);
        }

        if (event.target.value.length > 9) {
            event.target.value = event.target.value.slice(0, 9);
        }
    };

    validatePasswords = () => {
        return this.state.password === this.state.repeatedPassword;
    };

    validateNotEmptyInputs = () => {
        return this.state.login.trim() !== '' && this.state.firstName.trim() !== '' && this.state.lastName.trim() !== ''
            && this.state.phoneNumber.trim() !== '' && this.state.emailAddress.trim() !== '' && this.state.password.trim() !== ''
            && this.state.repeatedPassword.trim() !== '';
    };

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

    validateForm = () => {
        return this.validateNotEmptyInputs()
            && this.validatePasswords()
            && validateEmailAddress(this.state.emailAddress)
            && validatePasswordIsSafe(this.state.password)
            && validatePhoneNumber(this.state.phoneNumber)
            && validateFirstName(this.state.firstName)
            && validateLastName(this.state.lastName)
            && validateLogin(this.state.login);
    }

    isButtonDisabled = () => {
        if (!this.validateForm()) {
            return true;
        }

        if (serverConfig.ENABLE_CAPTCHA) {
            return this.state.loading || !this.state.filledCaptcha;
        } else {
            return this.state.loading;
        }
    }

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

    render() {
        const {t} = this.props;
        document.title = t("registerPage.pageTitle");

        return (
            <div className="background" style={{scale: "50%"}}>
                <Grid container
                      className="center-content"
                      direction="row"
                      alignItems="center"
                      justify="center">
                    <Container maxWidth="sm" m="auto">
                        <CssBaseline/>
                        <div className="background-form">
                            <div className="back-inside" style={{backdropFilter: "blur(3px)"}}>
                                <Icon>
                                    <LockOutlinedIcon fontSize="large"/>
                                </Icon>
                                <Typography component="h1" variant="h4">
                                    {t("registerPage.registerHeader")}
                                </Typography>
                                <form onSubmit={this.handleSubmit}>
                                    <TextField style={{background: "lightgrey"}}
                                               onChange={this.handleChange}
                                               variant="filled"
                                               margin="normal"
                                               fullWidth
                                               label={t("registerPage.loginField")}
                                               name="login"
                                               inputProps={{
                                                   maxLength: 20
                                               }}
                                               error={!validateLogin(this.state.login)}
                                               required/>
                                    <TextField style={{background: "lightgrey"}}
                                               onChange={this.handleChange}
                                               variant="filled"
                                               margin="normal"
                                               fullWidth
                                               label={t("registerPage.firstNameField")}
                                               name="firstName"
                                               inputProps={{
                                                   maxLength: 20
                                               }}
                                               error={!validateFirstName(this.state.firstName)}
                                               required/>
                                    <TextField style={{background: "lightgrey"}}
                                               onChange={this.handleChange}
                                               variant="filled"
                                               margin="normal"
                                               fullWidth
                                               label={t("registerPage.lastNameField")}
                                               name="lastName"
                                               inputProps={{
                                                   maxLength: 50
                                               }}
                                               error={!validateLastName(this.state.lastName)}
                                               required/>
                                    <TextField style={{background: "lightgrey"}}
                                               onChange={this.handleChange}
                                               variant="filled"
                                               margin="normal"
                                               fullWidth
                                               label={t("registerPage.emailField")}
                                               name="emailAddress"
                                               helperText={t("registerPage.emailFieldTip")}
                                               error={!validateEmailAddress(this.state.emailAddress)}
                                               inputProps={{
                                                   maxLength: 50
                                               }}
                                               required/>
                                    <TextField style={{background: "lightgrey"}}
                                               onChange={this.handleChange}
                                               onInput={this.validateNineCharsPhoneNumberLimit}
                                               variant="filled"
                                               margin="normal"
                                               fullWidth
                                               label={t("registerPage.phoneNumberField")}
                                               name="phoneNumber"
                                               inputProps={{
                                                   maxLength: 9
                                               }}
                                               error={!validatePhoneNumber(this.state.phoneNumber)}
                                               required/>
                                    <TextField style={{background: "lightgrey"}}
                                               type="password"
                                               onChange={this.handleChange}
                                               variant="filled"
                                               margin="normal"
                                               fullWidth
                                               name="password"
                                               label={t("registerPage.passwordField")}
                                               error={!validatePasswordIsSafe(this.state.password)}
                                               helperText={t("registerPage.passwordSafetyTip")}
                                               required/>
                                    <TextField style={{background: "lightgrey"}}
                                               type="password"
                                               onChange={this.handleChange}
                                               variant="filled"
                                               margin="normal"
                                               fullWidth
                                               name="repeatedPassword"
                                               label={t("registerPage.repeatPasswordField")}
                                               error={!this.validatePasswords()}
                                               required/>
                                    {this.getCaptchaIfEnabled()}
                                    <Button
                                        name="submitButton"
                                        type="submit"
                                        fullWidth
                                        variant="contained"
                                        color="primary"
                                        disabled={this.isButtonDisabled()}
                                        style={{marginTop: "8%"}}>
                                        {this.state.loading ? <CircularProgress/> : t("registerPage.registerButton")}
                                    </Button>
                                    <Grid container>
                                        <Grid item xs>
                                            <Link href={urls.login} color="inherit" variant="body2" name="loginLink">
                                                {t("registerPage.loginLink")}
                                            </Link>
                                        </Grid>
                                    </Grid>
                                </form>
                            </div>
                        </div>
                        <SnackBar open={this.state.openSnackBar} message={this.state.responseMessage}
                                  type={this.state.response} handleSnackBarClose={this.handleSnackBarClose}/>
                        <ConfirmDialog open={this.state.openDialog} title={t("registerPage.confirmDialogTitle")}
                                       content={t("registerPage.confirmDialogContent")}
                                       handleDialogResponse={this.handleDialogResponse.bind(this)}/>
                    </Container>
                </Grid>
                <Grid item xs={12}
                      className="common-footer">
                    <Copyright/>
                </Grid>
            </div>
        )
    }
}

export default withTranslation()(RegisterPage);
