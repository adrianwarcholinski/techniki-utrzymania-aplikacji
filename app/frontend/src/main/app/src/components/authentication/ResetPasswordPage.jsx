import React, {Component} from "react";
import {withTranslation} from "react-i18next";
import Box from "@material-ui/core/Box";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Icon from "@material-ui/core/Icon";
import InfoIcon from "@material-ui/icons/Info";
import Typography from "@material-ui/core/Typography";
import CircularProgress from "@material-ui/core/CircularProgress";
import Copyright from "../templates/Copyright";
import {postFetch} from "../../utils/fetchUtility";
import {withRouter} from "react-router-dom";
import Link from "@material-ui/core/Link";
import TextField from "@material-ui/core/TextField";
import LockOpenlinedIcon from "@material-ui/icons/LockOpen";
import Button from "@material-ui/core/Button";
import {validatePasswordIsSafe} from "../../utils/regexpUtils";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import CardActions from '@material-ui/core/CardActions';
import {urls} from "../../const/Urls";

class ResetPasswordPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            newPassword: "",
            repeatNewPassword: "",
            theSamePasswords: true,
            loading: true,
            success: false,
            safePassword: true,
            token: this.props.match.params.toVerify,
            renderForm: false
        };
        this.sendRequestForValidateToken();

    }

    sendRequestForValidateToken = () => {
        const {t} = this.props;
        postFetch("/app/auth/verify-reset-link", null, {
            headers: {
                "token": this.state.token
            }
        })
            .then((res) => {
                    if (res.status === 200) {
                        this.setState({renderForm: true});
                    } else if(res.status===400) {
                        res.text().then(
                            (data) => this.setState({message: t(data), renderForm: false}));
                    }
                }
            ).catch(() => {
            this.setState({message: t(this.props.errorMessage), renderForm: false});
        }).finally(() => {
            this.setState({loading: false});
        });
    };

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    handleSubmit = (event) => {
        event.preventDefault();
        if (this.state.theSamePasswords && this.state.safePassword) {
            this.sendRequestForResetPassword();
        }
    };

    sendRequestForResetPassword = () => {
        const {t} = this.props;
        this.setState({loading: true, renderForm: false});
        postFetch("/app/account/reset-password", null, {
            headers: {
                "token": this.state.token,
                "newPassword": this.state.newPassword
            }
        })
            .then((res) => {
                    if (res.status === 200) {
                        this.setState({message: t("resetPasswordPage.successReset"), success: true});
                    } else {
                        res.text().then(
                            (data) => this.setState({message: t(data), success: false}));
                    }
                }
            ).catch(() => {
            this.setState({message: t(this.props.errorMessage), success: false});
        }).finally(() => {
            this.setState({loading: false});
        });
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
            return true;
        } else {
            this.setState({safePassword: false});
            return false
        }
    }

    render() {
        const {t} = this.props;
        return (
            <Box className="info-page-main">
                <Box className="info-page-content">
                    <Card m="auto" className="info-page-card">
                        {this.state.loading === true &&
                        <CardContent className="info-page-card-content">
                            <CircularProgress/>
                        </CardContent>
                        }

                        {this.state.loading === false && this.state.success === true &&
                        <CardContent className="info-page-card-content">
                            <Icon>
                                <CheckCircleIcon fontSize="large"/>
                            </Icon>
                            <Typography color="" variant="body1">
                                {this.state.message}
                            </Typography>
                        </CardContent>
                        }

                        {this.state.loading === false && (this.state.renderForm === false && this.state.success === false) &&
                        <CardContent className="info-page-card-content">
                            <Icon>
                                <InfoIcon fontSize="large"/>
                            </Icon>
                            <Typography color="error" variant="body1">
                                {this.state.message}
                            </Typography>
                        </CardContent>
                        }

                        {this.state.loading === false && this.state.renderForm === true &&
                        <CardContent className="info-page-card-content">
                            <form onSubmit={this.handleSubmit}>
                                <Icon>
                                    <LockOpenlinedIcon fontSize="large"/>
                                </Icon>
                                <Typography component="h1" variant="h4">
                                    {t("resetPasswordPage.resetPassword")}
                                </Typography>
                                <TextField size="medium" style={{background: "lightgrey"}}
                                           required
                                           type="password"
                                           onChange={this.handleChange}
                                           variant="filled"
                                           margin="normal"
                                           label={t("changeOwnPassword.newPasswordField")}
                                           name="newPassword"
                                           error={!this.state.safePassword}
                                           helperText={t("changeOwnPassword.newPasswordSafetyTip")}
                                           onKeyUp={this.validatePasswordSafety}
                                />
                                <div>
                                    <TextField style={{background: "lightgrey"}}
                                               required
                                               type="password"
                                               onChange={this.handleChange}
                                               variant="filled"
                                               margin="normal"
                                               fullWidth
                                               name="repeatNewPassword"
                                               label={t("changeOwnPassword.repeatNewPasswordField")}
                                               error={!this.state.theSamePasswords}
                                               helperText={!this.state.theSamePasswords ? t("changeOwnPassword.passwordsMustMatchTip") : null}
                                               onKeyUp={this.validateTheSamePasswords}
                                    />
                                </div>
                                <Button
                                    name="resetPass"
                                    style={{marginTop: "15px"}}
                                    type="submit"
                                    variant="contained"
                                    color={"primary"}
                                    disabled={this.state.loading}
                                >
                                    {this.state.loading ? <CircularProgress/> : t("resetPasswordPage.reset")}
                                </Button>
                            </form>
                        </CardContent>
                        }
                        <CardActions className="info-page-card-action">
                            <Link href={urls.login} color="inherit" variant="body2">
                                {t("infoPage.login")}
                            </Link>
                        </CardActions>
                    </Card>
                </Box>
                <Box className="info-page-footer">
                    <Copyright/>
                </Box>
            </Box>
        )
    }
}

export default withTranslation()(withRouter(ResetPasswordPage));
