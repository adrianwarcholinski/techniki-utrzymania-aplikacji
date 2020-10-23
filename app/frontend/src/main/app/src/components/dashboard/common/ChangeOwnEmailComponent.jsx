import React, {Component} from 'react';
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress";
import '../../../resources/styles/LoginPage.scss';
import {withTranslation} from "react-i18next";
import Grid from "@material-ui/core/Grid";
import ConfirmDialog from "../templates/ConfirmDialog";
import {SnackBarType} from "../templates/SnackBar";
import {postFetch} from "../../../utils/fetchUtility";
import Captcha from "../templates/Captcha";
import serverConfig from "../../../properties.json";
import Box from "@material-ui/core/Box";
import {validateEmailAddress} from "../../../utils/regexpUtils";
import i18n from "./../../../i18n"
import Paper from "@material-ui/core/Paper";
import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Typography from "@material-ui/core/Typography";
import ExpansionPanel from "@material-ui/core/ExpansionPanel";
import TextFieldComponent from "../templates/TextFieldComponent";

class ChangeOwnEmailComponent extends Component {
    constructor(props) {
        super(props);

        this.state = {
            currentEmail: this.props.currentEmail,
            newEmail: "",
            loading: false,
            openDialog: false,
            validEmail: true,
            response: null,
            differentFromCurrentEmail: true,
            filledCaptcha: false,
            captchaToken: null

        };

        this.captchaRef = React.createRef();
    }

    componentDidMount() {
        this.setState({})
    }


    sendRequest = () => {
        const {t} = this.props;
        const header = {
            headers: {
                "captchaToken": this.state.captchaToken,
            }
        };

        postFetch('/app/account/send-email-change-link?email=' + this.state.newEmail + '&lang=' + i18n.language, null, header)
            .then(
                (res) => {
                    if (res.status === 200) {
                        this.props.displaySnackbar(SnackBarType.success, t("changeOwnEmail.successChangeOwnEmail"));
                    } else if(res.status===400) {
                        res.text().then(
                            (data) => this.props.displaySnackbar(SnackBarType.error, t(data)))
                    }
                }
            ).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("error.generalChangeOwnEmail"));
        }).finally(() => {
            this.setState({loading: false});
            if (serverConfig.ENABLE_CAPTCHA) {
                this.captchaRef.current.reset();
            }
            this.props.refreshHandler(t);
        });
    }

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
        this.validateEmail(event.target.value);
    }

    handleSubmit = (event) => {
        event.preventDefault();
        if (this.state.differentFromCurrentEmail && this.state.validEmail) {
            this.handleOpenDialog();
        }
    }

    validateNewEmailDifferentFromCurrent = (newEmail) => {
        return this.state.currentEmail !== newEmail;
    }

    validateEmail = (email) => {
        this.setState({
            differentFromCurrentEmail: this.validateNewEmailDifferentFromCurrent(email),
            validEmail: validateEmailAddress(email) && email !== ''
        });
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

    handleFilledCaptcha = (value) => {
        this.setState({filledCaptcha: true})
        this.setState({captchaToken: value})
    }

    handleExpiredCaptcha = () => {
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
        return this.state.differentFromCurrentEmail && this.state.validEmail && this.state.newEmail.trim() !== ''
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
                                            name="changeEmail"
                                            expandIcon={<ExpandMoreIcon/>}
                                            aria-controls="panel1a-content"
                                            id="panel1a-header">
                                            <Typography>{t("account.changeEmail.changeEmail")}</Typography>
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
                                            label={t("changeOwnEmail.currentEmailField")}
                                            value={this.state.currentEmail}
                                            name="currentEmail"
                                            disabled
                                        />
                                        <TextFieldComponent
                                            type="text"
                                            label={t("changeOwnEmail.newEmailField")}
                                            name="newEmail"
                                            error={!this.state.differentFromCurrentEmail || !this.state.validEmail}
                                            helperText={(!this.state.differentFromCurrentEmail && t("changeOwnEmail.newEmailMustBeDifferentFromCurrent")) || (!this.state.validEmail && t("changeOwnEmail.invalidEmail"))}
                                            onChange={this.handleChange}
                                            inputProps={{
                                                maxLength: 50
                                            }}
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
                                            name="changeEmailSubmit"
                                            type="button"
                                            onClick={this.handleSubmit}
                                            color="primary"
                                            disabled={this.isButtonDisabled()}>
                                            {t("account.changeEmail.changeEmail")}
                                        </Button>}
                                </Grid>
                            </ExpansionPanel>
                        </form>
                    </Paper>
                </Grid>
                <ConfirmDialog open={this.state.openDialog} title={t("changeOwnEmail.confirmDialogTitle")}
                               content={t("changeOwnEmail.confirmDialogContent")}
                               handleDialogResponse={this.handleDialogResponse}/>
            </Grid>
        )
    }
}

export default withTranslation()(ChangeOwnEmailComponent);
