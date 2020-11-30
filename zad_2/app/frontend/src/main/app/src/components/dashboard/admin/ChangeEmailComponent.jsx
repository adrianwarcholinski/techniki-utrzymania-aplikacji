import React, {Component} from "react";
import Grid from "@material-ui/core/Grid";
import {withRouter} from "react-router-dom";
import {withTranslation} from "react-i18next";
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress/CircularProgress";
import ConfirmDialog from "../templates/ConfirmDialog";
import {validateEmailAddress} from "../../../utils/regexpUtils";
import Paper from "@material-ui/core/Paper";
import {postFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import ExpansionPanel from "@material-ui/core/ExpansionPanel";
import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Typography from "@material-ui/core/Typography";
import TextFieldComponent from "../templates/TextFieldComponent";


class ChangeEmailComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            openForm: false,
            buttonLabel: this.props.t("account.changeEmail.change"),
            loading: false,
            correctEmail: true,
            newEmail: this.props.oldEmail,
            openDialog: false
        };
    }

    sendPostRequest = () => {
        const {t} = this.props;
        const params = "login=" + this.props.login + "&email=" + this.state.newEmail + "&lang=" + navigator.language;
        postFetch('/app/account/send-users-email-change-link?' + params)
            .then((response) => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("account.changeEmail.emailChangeSuccess"));
                } else if(response.status===400) {
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
            newEmail: event.target.value
        }, this.validateEmail);
    };

    handleSubmit = (event) => {
        event.preventDefault();
        if (this.state.correctEmail) {
            this.handleOpenDialog();
        }
    };


    validateEmail = () => {
        if (validateEmailAddress(this.state.newEmail) && this.state.newEmail !== '') {
            this.setState({correctEmail: true});
        } else {
            this.setState({correctEmail: false});
        }
    };


    handleShowButton = () => {
        this.setState({
            openForm: !this.state.openForm,
            buttonLabel: this.props.t(this.state.openForm ? "account.changeEmail.change" : "account.changeEmail.cancel"),
            correctEmail: true,
            newEmail: this.props.oldEmail,
        })
    };

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({loading: true});
            this.sendPostRequest();
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
                                            name="emailManagementPanel"
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
                                                            onChange={this.handleChange}
                                                            label={t("account.changeEmail.currentEmail")}
                                                            value={this.state.newEmail}
                                                            helperText={(!this.state.correctEmail && t("account.changeEmail.invalidEmail"))}
                                                            name="newEmail"
                                                            error={!this.state.correctEmail}
                                                            inputProps={{
                                                                maxLength: 50
                                                            }}/>

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
                                            name="handleEmailChangeButton"
                                            type="button"
                                            onClick={this.handleSubmit}
                                            color="primary"
                                            disabled={loading || !this.state.correctEmail || this.state.newEmail === this.props.oldEmail}>
                                            {t("account.changeEmail.changeEmail")}
                                        </Button>}
                                </Grid>
                            </ExpansionPanel>
                        </form>
                    </Paper>
                </Grid>
                <ConfirmDialog open={this.state.openDialog} title={t("account.changeEmail.confirmDialogTitle")}
                               content={t("account.changeEmail.confirmDialogContent")}
                               handleDialogResponse={this.handleDialogResponse}/>
            </Grid>
        )
    }

}

export default withTranslation()(withRouter(ChangeEmailComponent));
