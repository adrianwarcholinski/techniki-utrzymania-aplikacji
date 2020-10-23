import React, {Component} from "react";
import Grid from "@material-ui/core/Grid";
import {withRouter} from "react-router-dom";
import {withTranslation} from "react-i18next";
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress/CircularProgress";
import ConfirmDialog from "../templates/ConfirmDialog";
import {validatePasswordIsSafe} from "../../../utils/regexpUtils";
import Paper from "@material-ui/core/Paper";
import {putFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import Typography from "@material-ui/core/Typography";
import ExpansionPanel from "@material-ui/core/ExpansionPanel";
import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import TextFieldComponent from "../templates/TextFieldComponent";


class ChangePasswordComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            openForm: false,
            buttonLabel: this.props.t("account.changePassword.change"),
            loading: false,
            safePassword: true,
            theSamePasswords: true,
            newPassword: "",
            repeatNewPassword: "",
            openDialog: false
        };
    }

    sendPutRequest = () => {
        const {t} = this.props;
        const header = {
            headers: {
                "newPassword": this.state.newPassword
            }
        };
        putFetch('/app/account/change-password?login=' + this.props.login, undefined, header)
            .then((response) => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("account.changePassword.passwordChangeSuccess"));
                } else if(response.status===400){
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        }).finally(
            () => this.props.refreshHandler(t)
        )
    };

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        }, this.validateForm);
    };

    handleSubmit = (event) => {
        event.preventDefault();
        if (this.state.theSamePasswords && this.state.safePassword) {
            this.handleOpenDialog();
        }
    };

    validateTheSamePasswords = () => {
        if (this.state.newPassword === this.state.repeatNewPassword) {
            this.setState({theSamePasswords: true});
        } else {
            this.setState({theSamePasswords: false});
        }
    };

    validatePasswordSafety = () => {
        if (validatePasswordIsSafe(this.state.newPassword)) {
            this.setState({safePassword: true});
        } else {
            this.setState({safePassword: false});
        }
    };

    validateForm = () => {
        if (this.state.newPassword.length > 0 && this.state.repeatNewPassword.length > 0) {
            this.validateTheSamePasswords();
            this.validatePasswordSafety();
        } else if (this.state.newPassword.length > 0) {
            this.validatePasswordSafety()
        } else if (this.state.repeatNewPassword.length > 0) {
            this.validateTheSamePasswords();
        }
    };

    handleShowButton = () => {
        this.setState({
            openForm: !this.state.openForm,
            buttonLabel: this.props.t(this.state.openForm ? "account.changePassword.change" : "account.changePassword.cancel"),
            safePassword: true,
            theSamePasswords: true,
            newPassword: "",
            repeatNewPassword: "",
        })
    };

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({loading: true});
            this.sendPutRequest();
        }
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
                              method="put">
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

                                        <TextFieldComponent type="password"
                                                            onChange={this.handleChange}
                                                            label={t("changeOwnPassword.newPasswordField")}
                                                            value={this.state.newPassword}
                                                            name="newPassword"
                                                            error={!this.state.safePassword}
                                                            helperText={t("changeOwnPassword.newPasswordSafetyTip")}/>

                                        <TextFieldComponent type="password"
                                                            onChange={this.handleChange}
                                                            name="repeatNewPassword"
                                                            label={t("changeOwnPassword.repeatNewPasswordField")}
                                                            value={this.state.repeatNewPassword}
                                                            error={!this.state.theSamePasswords}
                                                            helperText={!this.state.theSamePasswords ? t("changeOwnPassword.passwordsMustMatchTip") : null}/>
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
                                                disabled={loading || !(this.state.safePassword && this.state.theSamePasswords
                                                    && this.state.newPassword !== "" && this.state.repeatNewPassword !== "")}>
                                            {t("changeOwnPassword.changePasswordButton")}
                                        </Button>}
                                </Grid>
                            </ExpansionPanel>
                        </form>
                    </Paper>
                </Grid>
                <ConfirmDialog open={this.state.openDialog} title={t("account.changePassword.confirmDialogTitle")}
                               content={t("account.changePassword.confirmDialogContent")}
                               handleDialogResponse={this.handleDialogResponse.bind(this)}/>
            </Grid>
        )
    }
}

export default withTranslation()(withRouter(ChangePasswordComponent));
