import React, {Component} from 'react';
import {Card} from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import CardContent from "@material-ui/core/CardContent";
import TextField from "@material-ui/core/TextField";
import {withTranslation} from 'react-i18next';
import {withRouter} from "react-router-dom";
import Button from "@material-ui/core/Button";
import {getFetch, putFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import {validateCardNumber, validateFirstName, validateLastName, validatePhoneNumber} from "../../../utils/regexpUtils";
import LinearProgress from "@material-ui/core/LinearProgress";
import ConfirmDialog from "../templates/ConfirmDialog";
import ChangeOwnPasswordComponent from "./ChangeOwnPasswordComponent";
import ChangeOwnEmailComponent from "./ChangeOwnEmailComponent";
import CardHeader from "@material-ui/core/CardHeader";
import serverConfig from "../../../properties.json";
import Box from "@material-ui/core/Box";
import Captcha from "../templates/Captcha";

import "../../../resources/styles/OwnAccountDetails.scss"


class OwnAccountDetailsComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            openDialog: false,
            loading: false,
            login: this.props.match.params.login,
            details: true,
            captchaToken: null,
            filledCaptcha: false
        };

        this.captchaRef = React.createRef();
    }

    componentDidMount = () => {
        this.sendGetRequest();
    };


    sendGetRequest = () => {
        let uri = '/app/account/own-details';


        const {t} = this.props;
        const newState = {
            loading: true
        };
        this.setState(newState);
        getFetch(uri)
            .then(response => {
                if (response.ok) {
                    return response;
                } else if(response.status===400) {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }

            })
            .then(response => response.json())
            .then(data => {
                const newState = {
                    id: data.id,
                    version: data.version,
                    login: data.login,
                    email: data.email,
                    name: data.name,
                    surname: data.surname,

                };
                this.setState(newState);
                if (data["cardNumberDto"] !== undefined) {
                    this.setState({
                        cardNumber: data["cardNumberDto"].cardNumber,
                        cardNumberId: data["cardNumberDto"].id,
                        cardNumberVersion: data["cardNumberDto"].version,
                    })
                } else {
                    this.setState({
                        cardNumber: undefined,
                        cardNumberId: undefined,
                        cardNumberVersion: undefined,
                    })
                }
                if (data["phoneNumberDto"] !== undefined) {
                    this.setState({
                        phoneNumber: data["phoneNumberDto"].phoneNumber,
                        phoneNumberId: data["phoneNumberDto"].id,
                        phoneNumberVersion: data["phoneNumberDto"].version,
                    })
                } else {
                    this.setState({
                        phoneNumber: undefined,
                        phoneNumberId: undefined,
                        phoneNumberVersion: undefined,
                    })
                }
                if (data["workPhoneNumberDto"] !== undefined) {
                    this.setState({
                        workPhoneNumber: data["workPhoneNumberDto"].workPhoneNumber,
                        workPhoneNumberId: data["workPhoneNumberDto"].id,
                        workPhoneNumberVersion: data["workPhoneNumberDto"].version,
                    })
                } else {
                    this.setState({
                        workPhoneNumber: undefined,
                        workPhoneNumberId: undefined,
                        workPhoneNumberVersion: undefined,
                    })
                }

            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("account.ERROR"));
        })
            .finally(() => {
                const newState = {
                    loading: false
                };
                this.setState(newState);
            });
    };


    sendPutRequest = () => {
        const {t} = this.props;
        const body = {
            "id": this.state.id,
            "email": this.state.email,
            "version": this.state.version,
            "login": this.state.login,
            "name": this.state.name,
            "surname": this.state.surname,
            "cardNumberDto": {
                "cardNumber": this.state.cardNumber,
                "id": this.state.cardNumberId,
                "version": this.state.cardNumberVersion,
            },
            "phoneNumberDto": {
                "phoneNumber": this.state.phoneNumber,
                "id": this.state.phoneNumberId,
                "version": this.state.phoneNumberVersion,
            },
            "workPhoneNumberDto": {
                "workPhoneNumber": this.state.workPhoneNumber,
                "id": this.state.workPhoneNumberId,
                "version": this.state.workPhoneNumberVersion,
            },
        };

        if (body["cardNumberDto"].cardNumber === undefined) {
            body.cardNumberDto = null;
        }
        if (body["phoneNumberDto"].phoneNumber === undefined) {
            body.phoneNumberDto = null;
        }
        if (body["workPhoneNumberDto"].workPhoneNumber === undefined) {
            body.workPhoneNumberDto = null;
        }

        const header = {
            headers: {
                "Content-Type": "application/json; charset=utf-8",
                "captchaToken": this.state.captchaToken
            }
        };

        putFetch('/app/account/edit-own', body, header)
            .then((response) => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("account.editionSuccess"));
                } else {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("account.ERROR"));
        }).finally(() => {
            this.sendGetRequest();
            this.setState({details: !this.state.details});
        })
    };


    renderButtonBack = (details) => {
        const {t} = this.props;
        if (details === false) {
            return <Button
                name="back"
                aria-controls="customized-menu"
                aria-haspopup="true"
                color="primary"
                onClick={this.handleEditClick}>
                {t("account.comeBackButton")}
            </Button>
        }
    };


    validateInputs = () => {
        const {t} = this.props;
        let err = "";
        let answer = true;
        if (this.state.cardNumber !== undefined) {
            answer = answer && validateCardNumber(this.state.cardNumber);
            if (!validateCardNumber(this.state.cardNumber)) {
                err += " " + t("account.incorrectCardNumber");
            }
        }
        if (this.state.workPhoneNumber !== undefined) {
            answer = answer && validatePhoneNumber(this.state.workPhoneNumber);
            if (!validatePhoneNumber(this.state.workPhoneNumber)) {
                err += " " + t("account.incorrectNumberError");
            }
        }
        if (this.state.phoneNumber !== undefined) {
            answer = answer && validatePhoneNumber(this.state.phoneNumber);
            if (!validatePhoneNumber(this.state.phoneNumber)) {
                err += " " + t("account.incorrectNumberError");
            }
        }
        if (this.state.cardNumber === "") {
            answer = answer && this.state.cardNumber !== "";
            err += " " + t("account.cardNumberError");
        }
        if (this.state.workPhoneNumber === "") {
            answer = answer && this.state.workPhoneNumber !== "";
            err += " " + t("account.workPhoneNumberError");
        }
        if (this.state.phoneNumber === "") {
            answer = answer && this.state.phoneNumber !== "";
            err += " " + t("account.phoneNumberError");
        }
        if (this.state.name === "") {
            answer = answer && this.state.name !== '';
            err += " " + t("account.nameError");
        }
        if (this.state.surname === "") {
            answer = answer && this.state.surname !== '';
            err += " " + t("account.surnameError");
        }
        if (!answer) {
            this.props.displaySnackbar(SnackBarType.error, err);
        }
        return answer;
    };


    validateOnlyLetterAndDigits = (event) => {
        return /^(\w+-?)+$/i.test(event.target.value);
    };

    validateOnlyDigits = (event) => {
        return /^\d+$/i.test(event.target.value);
    };


    validateElevenCharsCardNumberLimit = (event) => {
        if (!this.validateOnlyLetterAndDigits(event)) {
            const lastCharIndex = event.target.value.length - 1;
            event.target.value = event.target.value.slice(0, lastCharIndex);
        }

        if (event.target.value.length > 12) {
            event.target.value = event.target.value.slice(0, 12);
        }
    };


    validateNineCharsPhoneNumberLimit = (event) => {
        if (!this.validateOnlyDigits(event)) {
            const lastCharIndex = event.target.value.length - 1;
            event.target.value = event.target.value.slice(0, lastCharIndex);
        }

        if (event.target.value.length > 9) {
            event.target.value = event.target.value.slice(0, 9);
        }
    };

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    handleEditClick = () => {
        if (this.state.details === false) {
            this.sendGetRequest()
        }
        const newState = {
            details: !this.state.details
        };
        this.setState(newState);
    };

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({loading: true});
            this.sendPutRequest();
        }
    };

    handleCloseDialog = () => {
        this.setState({
            openDialog: false
        })
    };

    handleOpenDialog = () => {
        if (this.validateInputs()) {
            this.setState({
                openDialog: true
            })
        }
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

    getCaptchaIfEnabled = () => {
        if (serverConfig.ENABLE_CAPTCHA) {
            return (
                <Box className='captcha-box'>
                    <Captcha captchaRef={this.captchaRef}
                             filledCaptchaCallback={this.handleFilledCaptcha}
                             expiredCaptchaCallback={this.handleExpiredCaptcha}/>
                </Box>
            );
        }
    };

    getButtonWithCaptchaIfEnabled = () => {
        const {t} = this.props;
        if (this.state.details) {
            return (
                <Button name="edit"
                        aria-controls="customized-menu"
                        aria-haspopup="true"
                        variant="contained"
                        color="primary"
                        onClick={this.handleEditClick}>
                    {t("account.edit")}
                </Button>
            )
        } else {
            return (
                <div>
                    {this.getCaptchaIfEnabled()}
                    <Button name="submit"
                            aria-controls="customized-menu"
                            aria-haspopup="true"
                            variant="contained"
                            color="secondary"
                            onClick={this.handleOpenDialog}
                            style={{align: "center", marginLeft: "30%", marginTop: "10%"}}
                            disabled={this.isButtonDisabled()}>
                        {t("account.submit")}
                    </Button>
                </div>
            )
        }
    };

    validateForm = () => {
        if (this.state.name.trim() === '' || this.state.surname.trim() === '') {
            return false;
        }
        if (this.state.phoneNumber !== undefined && (!validatePhoneNumber(this.state.phoneNumber) || this.state.phoneNumber.trim() === '')) {
            return false;
        }
        if (this.state.workPhoneNumber !== undefined && (!validatePhoneNumber(this.state.workPhoneNumber) || this.state.workPhoneNumber.trim() === '')) {
            return false;
        }
        if (this.state.cardNumber !== undefined && (!validateCardNumber(this.state.cardNumber)) || this.state.cardNumber === '') {
            return false;
        }
        return true;
    };

    isButtonDisabled = () => {
        if (!this.validateForm()
            || !validateFirstName(this.state.name) || this.state.name.trim() === ""
            || !validateLastName(this.state.surname) || this.state.name.trim() === "") {
            return true;
        }

        if (serverConfig.ENABLE_CAPTCHA) {
            return this.state.loading || !this.state.filledCaptcha;
        } else {
            return this.state.loading;
        }
    };

    generateAccountDetails = () => {
        const {t} = this.props;
        return (
            <div>
                <Card>
                    <CardHeader
                        title={t("account.details")}
                        className="card-header"
                    />
                    <CardContent>
                        <div>
                            <Grid container
                                  spacing={5}
                                  style={{
                                      paddingLeft: 30,
                                      paddingRight: 30,
                                      paddingTop: 15,
                                      paddingBottom: 5
                                  }}>
                                <Grid item xs={6}>
                                    <Grid container
                                          direction="row"
                                          justify="center"
                                          alignItems="flex-start"
                                          item xs={12}>
                                        <Grid item xs={12}>
                                            {this.renderButtonBack(this.state.details)}
                                        </Grid>
                                        <Grid item xs={12}>
                                            <Grid item xs={12} container justify='flex-start' alignItems="center"
                                                  style={{padding: 3}}>
                                                <Grid item xs={12}>
                                                    <TextField
                                                        name="name"
                                                        disabled={(this.state.details) ? "disabled" : ""}
                                                        style={{
                                                            width: '100%',
                                                            color: "black",
                                                            fontSize: 20,
                                                        }}
                                                        label={t("account.accountName")}
                                                        margin="normal"
                                                        variant="outlined"
                                                        value={this.state.name}
                                                        onChange={this.handleChange}
                                                        error={!validateFirstName(this.state.name)}
                                                        required
                                                        inputProps={{
                                                            maxLength: 20
                                                        }}>
                                                    </TextField>
                                                </Grid>
                                            </Grid>
                                            <Grid item xs={12} container justify='flex-start' alignItems="center"
                                                  style={{padding: 3}}>
                                                <Grid item xs={12}>
                                                    <TextField
                                                        name="surname"
                                                        disabled={(this.state.details) ? "disabled" : ""}
                                                        style={{
                                                            width: '100%',
                                                            color: "black",
                                                            fontSize: 20,
                                                        }}
                                                        label={t("account.accountSurname")}
                                                        margin="normal"
                                                        variant="outlined"
                                                        value={this.state.surname}
                                                        onChange={this.handleChange}
                                                        error={!validateLastName(this.state.surname)}
                                                        required
                                                        inputProps={{
                                                            maxLength: 50
                                                        }}>
                                                    </TextField>
                                                </Grid>
                                            </Grid>
                                            {this.state.cardNumber !== undefined &&
                                            <Grid item xs={12} container justify='flex-start' alignItems="center"
                                                  style={{padding: 3}}>
                                                <Grid item xs={12}>
                                                    <TextField
                                                        name="cardNumber"
                                                        onChange={this.handleChange}
                                                        onInput={this.validateElevenCharsCardNumberLimit}
                                                        disabled={(this.state.details) ? "disabled" : ""}
                                                        style={{
                                                            width: '100%',
                                                            color: "black",
                                                            fontSize: 20,
                                                        }}
                                                        label={t("account.accountCardNumber")}
                                                        margin="normal"
                                                        variant="outlined"
                                                        value={this.state.cardNumber}
                                                        error={!validateCardNumber(this.state.cardNumber)}
                                                        required>
                                                    </TextField>
                                                </Grid>
                                            </Grid>}
                                            {this.state.phoneNumber !== undefined &&
                                            <Grid item xs={12} container justify='flex-start' alignItems="center"
                                                  style={{padding: 3}}>
                                                <Grid item xs={12}>
                                                    <TextField
                                                        name="phoneNumber"
                                                        onChange={this.handleChange}
                                                        onInput={this.validateNineCharsPhoneNumberLimit}
                                                        disabled={(this.state.details) ? "disabled" : ""}
                                                        style={{
                                                            width: '100%',
                                                            color: "black",
                                                            fontSize: 20,
                                                        }}
                                                        label={t("account.accountPhoneNumber")}
                                                        margin="normal"
                                                        variant="outlined"
                                                        value={this.state.phoneNumber}
                                                        error={!validatePhoneNumber(this.state.phoneNumber)}
                                                        required>
                                                    </TextField>
                                                </Grid>
                                            </Grid>}
                                            {this.state.workPhoneNumber !== undefined &&
                                            <Grid item xs={12} container justify='flex-start' alignItems="center"
                                                  style={{padding: 3}}>
                                                <Grid item xs={12}>
                                                    <TextField
                                                        name="workPhoneNumber"
                                                        onChange={this.handleChange}
                                                        onInput={this.validateNineCharsPhoneNumberLimit}
                                                        disabled={(this.state.details) ? "disabled" : ""}
                                                        style={{
                                                            width: '100%',
                                                            color: "black",
                                                            fontSize: 20,
                                                        }}
                                                        label={t("account.accountWorkPhoneNumber")}
                                                        margin="normal"
                                                        variant="outlined"
                                                        value={this.state.workPhoneNumber}
                                                        error={!validatePhoneNumber(this.state.workPhoneNumber)}
                                                        required>
                                                    </TextField>
                                                </Grid>
                                            </Grid>}

                                            <Grid container
                                                  direction="row"
                                                  justify="center"
                                                  alignItems="center"
                                                  item xs={12}>
                                                {this.getButtonWithCaptchaIfEnabled()}
                                            </Grid>
                                            <ConfirmDialog open={this.state.openDialog}
                                                           title={t("account.confirmDialogTitle")}
                                                           content={t("account.confirmDialogContent")}
                                                           handleDialogResponse={this.handleDialogResponse}/>
                                        </Grid>
                                    </Grid>
                                </Grid>
                                <Grid item xs={6}>
                                    <ChangeOwnPasswordComponent
                                        refreshHandler={this.sendGetRequest}
                                        displaySnackbar={this.props.displaySnackbar}/>
                                    <ChangeOwnEmailComponent
                                        refreshHandler={this.sendGetRequest}
                                        displaySnackbar={this.props.displaySnackbar}
                                        currentEmail={this.state.email}/>
                                </Grid>
                            </Grid>
                        </div>
                    </CardContent>
                </Card>
            </div>);
    };

    render() {
        const {t} = this.props;
        if (this.state.loading === false) {
            return (
                this.generateAccountDetails()
            )
        }
        if (this.state.loading === true) {
            return <LinearProgress/>
        }
    }
}

export default withTranslation()(withRouter(OwnAccountDetailsComponent));
