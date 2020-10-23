import React, {Component} from "react";

import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Typography from "@material-ui/core/Typography";
import ExpansionPanel from "@material-ui/core/ExpansionPanel";
import {withTranslation} from "react-i18next";
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress";
import {roles} from "../../../const/Roles";
import FormControl from "@material-ui/core/FormControl";
import RadioGroup from "@material-ui/core/RadioGroup";
import Grid from "@material-ui/core/Grid";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Radio from "@material-ui/core/Radio";
import {validateCardNumber, validateOnlyDigits, validatePhoneNumber} from "../../../utils/regexpUtils";
import {putFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import Paper from "@material-ui/core/Paper";
import ConfirmDialog from "../templates/ConfirmDialog";
import TextFieldComponent from "../templates/TextFieldComponent";

class ManageUserAccessLevelComponent extends Component {

    constructor(props) {
        super(props);

        this.state = {
            loading: false,
            login: this.props.login,
            selectedRole: roles.customer,
            selectedIsGrant: (this.props.phoneNumber !== undefined),
            phoneNumber: undefined,
            workPhoneNumber: undefined,
            cardNumber: undefined,
            admin: (this.props.cardNumber !== undefined),
            employee: (this.props.workPhoneNumber !== undefined),
            customer: (this.props.phoneNumber !== undefined),
            openDialog: false
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleChangeRole = this.handleChangeRole.bind(this);
        this.validateElevenCharsCardNumberLimit = this.validateElevenCharsCardNumberLimit.bind(this);
        this.validateNineCharsPhoneNumberLimit = this.validateNineCharsPhoneNumberLimit.bind(this);
        this.validateOnlyLetterAndDigits = this.validateOnlyLetterAndDigits.bind(this);
        this.validateNotEmptyInputs = this.validateNotEmptyInputs.bind(this);
        this.sendRequest = this.sendRequest.bind(this);
        this.handleOpenDialog = this.handleOpenDialog.bind(this);
        this.handleCloseDialog = this.handleCloseDialog.bind(this);
        this.validateButtonEnable = this.validateButtonEnable.bind(this);
    }

    sendRequest(link, param) {
        const {t} = this.props;
        this.setState({loading: true})
        const header = {
            headers: {
                "login": this.state.login,
                "param": param
            }
        };

        putFetch(
            '/app/account/' + link, null, header)
            .then((res) => {
                    if (res.status === 200) {
                        if (link === "add-admin-access-level")
                            this.props.displaySnackbar(SnackBarType.success, t("manageAccessLevels.GRANT_ADMIN_SUCCESS"));
                        else if (link === "add-employee-access-level")
                            this.props.displaySnackbar(SnackBarType.success, t("manageAccessLevels.GRANT_EMPLOYEE_SUCCESS"));
                        else if (link === "add-customer-access-level")
                            this.props.displaySnackbar(SnackBarType.success, t("manageAccessLevels.GRANT_CUSTOMER_SUCCESS"));
                        else if (link === "revoke-access-level") {
                            if (param[0] === roles.admin)
                                this.props.displaySnackbar(SnackBarType.success, t("manageAccessLevels.REVOKE_ADMIN_SUCCESS"));
                            else if (param[0] === roles.employee) {
                                this.props.displaySnackbar(SnackBarType.success, t("manageAccessLevels.REVOKE_EMPLOYEE_SUCCESS"));
                            } else if (param[0] === roles.customer)
                                this.props.displaySnackbar(SnackBarType.success, t("manageAccessLevels.REVOKE_CUSTOMER_SUCCESS"));
                        }
                    } else if(res.status===400){
                        res.text().then((data) => {
                            this.props.displaySnackbar(SnackBarType.error, t(data));
                        });
                    }
                }
            ).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("manageAccessLevels.ERROR"));
        }).finally(() => {
            this.setState({loading: false});
            this.props.sendGetRequest(t);
        });
    }

    handleDialogResponse(response) {
        this.handleCloseDialog();
        if (response === true) {
            if (this.state.selectedIsGrant === false) {
                if (this.state.selectedRole === roles.admin) {
                    this.sendRequest("add-admin-access-level", [this.state.cardNumber]);
                } else if (this.state.selectedRole === roles.employee) {
                    this.sendRequest("add-employee-access-level", [this.state.workPhoneNumber]);
                } else if (this.state.selectedRole === roles.customer) {
                    this.sendRequest("add-customer-access-level", [this.state.phoneNumber]);
                }
            } else {
                this.sendRequest("revoke-access-level", [this.state.selectedRole]);
            }
        }
    }

    handleSubmit(event) {
        event.preventDefault();
        this.handleOpenDialog();
    }

    handleChangeRole(event) {
        this.setState({
            selectedRole: event.target.value
        });

        if (event.target.value === roles.admin) {
            this.setState({selectedIsGrant: this.state.admin});
        } else if (event.target.value === roles.employee) {
            this.setState({selectedIsGrant: this.state.employee});
        } else if (event.target.value === roles.customer) {
            this.setState({selectedIsGrant: this.state.customer});
        }
    };

    handleChange(event) {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

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

    validateButtonEnable() {
        if(this.state.selectedRole === roles.admin && !this.state.admin && this.state.cardNumber !== ''
            && this.state.cardNumber !== undefined && validateCardNumber(this.state.cardNumber)) {
            return false;
        }
        if(this.state.selectedRole === roles.employee && !this.state.employee && this.state.workPhoneNumber !== ''
            && this.state.workPhoneNumber !== undefined && validatePhoneNumber(this.state.workPhoneNumber)) {
            return false;
        }
        if(this.state.selectedRole === roles.customer && !this.state.customer && this.state.phoneNumber !== ''
            && this.state.phoneNumber !== undefined && validatePhoneNumber(this.state.phoneNumber)) {
            return false;
        }
        return true
    }


    validateNineCharsPhoneNumberLimit(event) {
        if (!validateOnlyDigits(event.target.value)) {
            const lastCharIndex = event.target.value.length - 1;
            event.target.value = event.target.value.slice(0, lastCharIndex);
        }

        if (event.target.value.length > 9) {
            event.target.value = event.target.value.slice(0, 9);
        }
    }

    validateElevenCharsCardNumberLimit(event) {
        if (!this.validateOnlyLetterAndDigits(event)) {
            const lastCharIndex = event.target.value.length - 1;
            event.target.value = event.target.value.slice(0, lastCharIndex);
        }

        if (event.target.value.length > 12) {
            event.target.value = event.target.value.slice(0, 12);
        }
    }

    validateNotEmptyInputs() {
        if (this.state.activeRole === roles.admin)
            return this.state.cardNumber !== '';
        else if (this.state.activeRole === roles.employee)
            return this.state.workPhoneNumber !== '';
        else
            return this.state.phoneNumber !== '';
    }

    validateOnlyLetterAndDigits(event) {
        return /^(\w+-?)+$/i.test(event.target.value);
    }

    render() {
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
                                    <Paper name="manageAccessLevelsButton">
                                        <ExpansionPanelSummary
                                            expandIcon={<ExpandMoreIcon/>}
                                            aria-controls="panel1a-content"
                                            id="panel1a-header">
                                            <Typography>{t("manageAccessLevels.title")}</Typography>
                                        </ExpansionPanelSummary>
                                    </Paper>
                                </Grid>

                                <Grid item xs={12}
                                      container
                                      direction="column"
                                      justify="center"
                                      alignItems="center">
                                    <div>
                                        <FormControl component="fieldset"
                                                     style={{color: "black"}}
                                                     margin="normal"
                                                     fullWidth>

                                            <RadioGroup row aria-label="position"
                                                        name="position"
                                                        defaultValue="top"
                                                        fullWidth>
                                                <FormControlLabel
                                                    control={
                                                        <Radio color="primary"
                                                               checked={this.state.selectedRole === roles.admin}
                                                               onChange={this.handleChangeRole}
                                                               value={roles.admin}
                                                               inputProps={{'aria-label': 'ROLE_ADMIN'}}/>
                                                    }
                                                    label={t("manageAccessLevels.admin")}
                                                />
                                                <FormControlLabel
                                                    control={
                                                        <Radio color="primary"
                                                               checked={this.state.selectedRole === roles.employee}
                                                               onChange={this.handleChangeRole}
                                                               value={roles.employee}
                                                               inputProps={{'aria-label': 'ROLE_EMPLOYEE'}}/>
                                                    }
                                                    label={t("manageAccessLevels.employee")}
                                                />
                                                <FormControlLabel
                                                    control={
                                                        <Radio color="primary"
                                                               checked={this.state.selectedRole === roles.customer}
                                                               onChange={this.handleChangeRole}
                                                               value={roles.customer}
                                                               inputProps={{'aria-label': 'ROLE_CUSTOMER'}}/>
                                                    }
                                                    label={t("manageAccessLevels.customer")}
                                                />
                                            </RadioGroup>
                                        </FormControl>
                                    </div>
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

                                        {this.state.selectedRole === roles.admin && !this.state.admin &&
                                        <TextFieldComponent id="card-number-text-field"
                                                            onChange={this.handleChange}
                                                            onInput={this.validateElevenCharsCardNumberLimit}
                                                            label={t("manageAccessLevels.cardNumberField")}
                                                            name="cardNumber"
                                                            error={!validateCardNumber(this.state.cardNumber) && this.state.cardNumber !== "" && this.state.cardNumber !== undefined}/>}

                                        {this.state.selectedRole === roles.employee && !this.state.employee &&
                                        <TextFieldComponent id="work-phone-number-text-field"
                                                            onChange={this.handleChange}
                                                            onInput={this.validateNineCharsPhoneNumberLimit}
                                                            label={t("manageAccessLevels.workPhoneNumberField")}
                                                            name="workPhoneNumber"
                                                            error={!validatePhoneNumber(this.state.workPhoneNumber) && this.state.workPhoneNumber !== "" && this.state.workPhoneNumber !== undefined}/>}

                                        {this.state.selectedRole === roles.customer && !this.state.customer &&
                                        <TextFieldComponent id="phone-number-text-field"
                                                            onChange={this.handleChange}
                                                            onInput={this.validateNineCharsPhoneNumberLimit}
                                                            label={t("manageAccessLevels.phoneNumberField")}
                                                            name="phoneNumber"
                                                            error={!validatePhoneNumber(this.state.phoneNumber) && this.state.phoneNumber !== "" && this.state.phoneNumber !== undefined}/>}
                                    </div>
                                </Grid>

                                <Grid item xs={12}
                                      style={{
                                          paddingLeft: 10,
                                          paddingRight: 10,
                                          paddingBottom: 10,
                                          paddingTop: 0
                                      }}>
                                    {this.state.selectedIsGrant === true &&
                                    <Button
                                        name="revokeAccessLevelButton"
                                        type="submit"
                                        color="secondary"
                                        disabled={
                                            this.state.loading}>
                                        {this.state.loading ?
                                            <CircularProgress/> : t("manageAccessLevels.revokeButton")}
                                    </Button>}

                                    {this.state.selectedIsGrant === false &&
                                    <Button
                                        name="grantAccessLevelButton"
                                        type="submit"
                                        color="primary"
                                        disabled={this.validateButtonEnable()}>
                                        {this.state.loading ?
                                            <CircularProgress/> : t("manageAccessLevels.grantButton")}
                                    </Button>}
                                </Grid>
                            </ExpansionPanel>
                        </form>
                    </Paper>
                </Grid>
                <ConfirmDialog open={this.state.openDialog}
                               title={t("manageAccessLevels.confirmDialogTitle")}
                               content={t("manageAccessLevels.confirmDialogContent")}
                               handleDialogResponse={this.handleDialogResponse.bind(this)}/>
            </Grid>

        )

    }

}

export default withTranslation()(ManageUserAccessLevelComponent);
