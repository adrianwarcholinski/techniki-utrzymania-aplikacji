import React, {Component} from 'react';
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress";
import '../../../resources/styles/LoginPage.scss';
import {withTranslation} from "react-i18next";
import {validatePasswordIsSafe} from "../../../utils/regexpUtils";
import Grid from "@material-ui/core/Grid";
import ConfirmDialog from "../templates/ConfirmDialog";
import {SnackBarType} from "../templates/SnackBar";
import {putFetch} from "../../../utils/fetchUtility";
import Captcha from "../templates/Captcha";
import Paper from "@material-ui/core/Paper";
import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Typography from "@material-ui/core/Typography";
import ExpansionPanel from "@material-ui/core/ExpansionPanel";
import TextFieldComponent from "../templates/TextFieldComponent";
import Box from "@material-ui/core/Box";
import serverConfig from "../../../properties.json";

class ChangeOwnPasswordComponent extends Component {
    constructor(props) {
        super(props);

        this.state = {
            oldPassword: "",
            newPassword: "",
            repeatNewPassword: "",
            loading: false,
            openDialog: false,
            theSamePasswords: true,
            safePassword: true,
            response: null,
            differentFromOldPassword: true,
            filledCaptcha: false,
            captchaToken: null

        };

        this.captchaRef = React.createRef();

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleCloseDialog = this.handleCloseDialog.bind(this);
        this.validateTheSamePasswords = this.validateTheSamePasswords.bind(this);
        this.validatePasswordSafety = this.validatePasswordSafety.bind(this);
        this.validateForm = this.validateForm.bind(this);
    }


    sendRequest() {
        const {t} = this.props;
        putFetch('/app/account/change-own-password', null, {
            headers: {
                "oldPassword": this.state.oldPassword,
                "newPassword": this.state.newPassword,
                "captchaToken": this.state.captchaToken
            }
        }).then(
            (res) => {
                if (res.status === 200) {
                    this.props.displaySnackbar(SnackBarType.success, t("changeOwnPassword.successChangeOwnPassword"));
                } else if(res.status===400) {
                    res.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)))
                }
            }
        ).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("error.generalChangeOwnPassword"));
        }).finally(() => {
            this.setState({loading: false});
            if (serverConfig.ENABLE_CAPTCHA) {
                this.captchaRef.current.reset();
            }
            this.props.refreshHandler(t);
        });
    }

    handleChange(event) {
        this.setState({
            [event.target.name]: event.target.value
        }, () => this.validateForm());
    }

    handleSubmit(event) {
        event.preventDefault();
        if (this.state.theSamePasswords && this.state.safePassword && this.state.differentFromOldPassword) {
            this.handleOpenDialog();
        }
    }

    validateTheSamePasswords() {
        if (this.state.newPassword === this.state.repeatNewPassword) {
            this.setState({theSamePasswords: true});
        } else {
            this.setState({theSamePasswords: false});
        }
    }

    validatePasswordSafety() {
        if (validatePasswordIsSafe(this.state.newPassword)) {
            this.setState({safePassword: true});
            return true;
        } else {
            this.setState({safePassword: false});
            return false
        }
    }

    validateNewPasswordDifferentFromOld() {
        if (this.state.oldPassword.length > 0 && this.state.oldPassword === this.state.newPassword) {
            this.setState({differentFromOldPassword: false});
        } else {
            this.setState({differentFromOldPassword: true});
        }
    }

    validateForm() {
        if (this.state.newPassword.length > 0 && this.state.repeatNewPassword.length > 0) {
            this.validateTheSamePasswords();
            if (this.validatePasswordSafety()) {
                this.validateNewPasswordDifferentFromOld();
            }
        } else if (this.state.newPassword.length > 0) {
            if (this.validatePasswordSafety()) {
                this.validateNewPasswordDifferentFromOld();
            }
        } else if (this.state.repeatNewPassword.length > 0) {
            this.validateTheSamePasswords();
        }
    }

    handleDialogResponse(response) {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({loading: true});
            this.sendRequest();
        }
    }

    handleCloseDialog() {
        this.setState({
            openDialog: false
        })
    }

    handleOpenDialog() {
        this.setState({
            openDialog: true
        })
    }

    handleFilledCaptcha(value) {
        this.setState({filledCaptcha: true})
        this.setState({captchaToken: value})
    }

    handleExpiredCaptcha() {
        this.setState({filledCaptcha: false})
        this.setState({captchaToken: null})
    }

    getCaptchaIfEnabled = () => {
        if (serverConfig.ENABLE_CAPTCHA) {
            return (
                <Box style={{padding: "15px"}}>
                    <Captcha captchaRef={this.captchaRef}
                             filledCaptchaCallback={this.handleFilledCaptcha.bind(this)}
                             expiredCaptchaCallback={this.handleExpiredCaptcha.bind(this)}/>
                </Box>
            );
        }
    }

    validateInputs = () => {
        return this.state.safePassword && this.state.theSamePasswords && this.state.newPassword.trim() !== ""
            && this.state.repeatNewPassword.trim() !== ""
            && this.state.oldPassword.trim() !== '' && this.state.oldPassword !== this.state.newPassword;
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
        const {loading} = this.state;
        const {t} = this.props;
        return (

            <Grid container
                  direction="row"
                  justify="center"
                  alignItems="flex-start"
                  item xs={12}
                  style={{
                      paddingLeft: 0,
                      paddingRight: 0,
                      paddingBottom: 0,
                      paddingTop: 0
                  }}>
                <Grid item xs={12}>
                    <Paper>
                        <form onSubmit={this.handleSubmit}
                              method="post">
                            <ExpansionPanel>
                                <Grid item xs={12}>
                                    <Paper>
                                        <ExpansionPanelSummary
                                            name="changePass"
                                            expandIcon={<ExpandMoreIcon/>}
                                            aria-controls="panel1a-content"
                                            id="panel1a-header">
                                            <Typography>{t("account.changePassword.password")}</Typography>
                                        </ExpansionPanelSummary>
                                    </Paper>
                                </Grid>
                                <Grid item xs={12}
                                      container
                                      direction="column"
                                      justify="center"
                                      alignItems="center"
                                      style={{
                                          paddingTop: 0,
                                          paddingBottom: 0
                                      }}>
                                    <div style={{width: "80%"}}>
                                        <TextFieldComponent
                                            type="password"
                                            onChange={this.handleChange}
                                            label={t("changeOwnPassword.oldPasswordField")}
                                            name="oldPassword"
                                            required
                                        />
                                        <TextFieldComponent
                                            type="password"
                                            onChange={this.handleChange}
                                            label={t("changeOwnPassword.newPasswordField")}
                                            name="newPassword"
                                            error={!this.state.safePassword || !this.state.differentFromOldPassword}
                                            helperText={!this.state.differentFromOldPassword ? t("changeOwnPassword.newPasswordMustBeDifferentFromOld") : t("changeOwnPassword.newPasswordSafetyTip")}
                                            required
                                        />
                                        <TextFieldComponent
                                            type="password"
                                            onChange={this.handleChange}
                                            name="repeatNewPassword"
                                            label={t("changeOwnPassword.repeatNewPasswordField")}
                                            error={!this.state.theSamePasswords}
                                            helperText={!this.state.theSamePasswords ? t("changeOwnPassword.passwordsMustMatchTip") : null}
                                            required
                                        />
                                        <Grid
                                            container
                                            direction="column"
                                            justify="center"
                                            alignItems="center"
                                        >
                                        {this.getCaptchaIfEnabled()}
                                        </Grid>
                                    </div>
                                </Grid>
                                <Grid item xs={12}
                                      style={{
                                          paddingLeft: 10,
                                          paddingRight: 10,
                                          paddingBottom: 10,
                                          paddingTop: 0
                                      }}>
                                    {loading ? <CircularProgress/> :
                                        <Button
                                                name="submitChangePass"
                                                type="submit"
                                                color="primary"
                                                disabled={this.isButtonDisabled()}>
                                            {t("changeOwnPassword.changePasswordButton")}
                                        </Button>}
                                </Grid>
                            </ExpansionPanel>
                        </form>
                        <ConfirmDialog open={this.state.openDialog} title={t("changeOwnPassword.confirmDialogTitle")}
                                       content={t("changeOwnPassword.confirmDialogContent")}
                                       handleDialogResponse={this.handleDialogResponse.bind(this)}/>
                    </Paper>
                </Grid>
            </Grid>
        )
    }
}

export default withTranslation()(ChangeOwnPasswordComponent);
