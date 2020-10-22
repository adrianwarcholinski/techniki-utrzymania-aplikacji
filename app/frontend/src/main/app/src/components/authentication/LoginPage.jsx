import React, {Component} from 'react';
import Container from "@material-ui/core/Container";
import CssBaseline from "@material-ui/core/CssBaseline";
import Icon from "@material-ui/core/Icon";
import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import LockOutlinedIcon from "@material-ui/icons/LockOutlined";
import Copyright from "../templates/Copyright";
import Link from "@material-ui/core/Link";
import '../../resources/styles/LoginPage.scss';
import CircularProgress from '@material-ui/core/CircularProgress';
import {withTranslation} from "react-i18next";
import {postFetch} from "../../utils/fetchUtility";
import SnackBar, {SnackBarType} from "../dashboard/templates/SnackBar";
import i18n from "../../i18n";
import {urls} from "../../const/Urls";
import {validateLogin} from "../../utils/regexpUtils";

class LoginPage extends Component {

    constructor(props) {
        super(props);
        this.state = {
            snackbarOpen: false,
            snackbarMessage: null,
            snackbarType: SnackBarType.error,
            snackbarAnchorOrigin: {
                vertical: 'bottom',
                horizontal: 'center'
            },
            login: "",
            password: "",
            loading: false
        };

    }

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    private_request = (credentials) => {
        const {t} = this.props;
        postFetch('/app/auth/login', undefined, credentials)
            .then((response) => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    this.setState({loading: false});
                    response.text().then((data) => {
                        if (data === "error.unauthorizedAccountWasBlocked")
                            this.handleDisplaySnackbar(<>{t("loginPage.wrongCredentials")}<br/>{t(data)}</>);
                        else
                            this.handleDisplaySnackbar(t("loginPage.wrongCredentials"));
                    });
                    throw Error("catched");
                }
            })
            .then(res => res.json())
            .then((data) => {
                this.props.SuccessfulLoginHandler(data);
            })
            .catch((e) => {
                if (e.message !== "catched"){
                    this.handleDisplaySnackbar(t("common.requestError"));
                }
            })
    }

    handleSubmit = (event) => {
        const credentials = {
            headers: {
                login: this.state.login,
                password: this.state.password,
                language: i18n.language
            }
        };
        this.setState({loading: true});
        this.private_request(credentials);
        event.preventDefault();
    };


    handleDisplaySnackbar = (message) => {
        this.setState({
            snackbarMessage: message,
            snackbarOpen: true,
        });
    };

    handleCloseSnackbar = () => {
        this.setState({
            snackbarOpen: false,
            loading: false
        });
    };

    render() {
        const {loading} = this.state;
        const {t} = this.props;
        document.title = t('loginPage.pageTitle');
        return (
                    <div className="background">
                            <Grid container
                                  className="center-content"
                                  direction="row"
                                  alignItems="center"
                                  justify="center">
                            <Container maxWidth="sm" m="auto" >
                                <SnackBar open={this.state.snackbarOpen} message={this.state.snackbarMessage}
                                          type={this.state.snackbarType} handleSnackBarClose={this.handleCloseSnackbar}
                                          anchorOrigin={this.state.snackbarAnchorOrigin}/>
                                <CssBaseline/>
                                <CssBaseline/>
                                <div className="background-form">
                                    <div className="back-inside">
                                        <Icon>
                                            <LockOutlinedIcon fontSize="large"/>
                                        </Icon>
                                        <Typography component="h1" variant="h4">
                                            {t("loginPage.loginHeader")}
                                        </Typography>
                                        <form onSubmit={this.handleSubmit}>
                                            <TextField style={{background: "lightgrey"}}
                                                       onChange={this.handleChange}
                                                       variant="filled"
                                                       margin="normal"
                                                       disabled={loading}
                                                       fullWidth
                                                       label={t("loginPage.loginField")}
                                                       error={!validateLogin(this.state.login)}
                                                       name="login"
                                                       required
                                                       inputProps={{
                                                           maxLength: 20
                                                       }}
                                            />
                                            <TextField style={{background: "lightgrey"}}
                                                       type="password"
                                                       onChange={this.handleChange}
                                                       variant="filled"
                                                       margin="normal"
                                                       disabled={loading}
                                                       fullWidth
                                                       name="password"
                                                       label={t("loginPage.passwordField")}
                                                       required
                                            />
                                            <Button
                                                name="signInButton"
                                                type="submit"
                                                fullWidth
                                                variant="contained"
                                                color={"primary"}
                                                disabled={loading || !validateLogin(this.state.login) || this.state.login.trim() === ""}
                                            >
                                                {loading ? <CircularProgress/> : t("loginPage.loginButton")}
                                            </Button>
                                    <Grid container>
                                        <Grid item xs>
                                            <Link href={urls.resetPasswordInit} color="inherit" variant="body2">
                                                {t("loginPage.forgotPasswordLink")}
                                            </Link>
                                        </Grid>
                                        <Grid item xs>
                                            <Link href={urls.register} color="inherit" variant="body2">
                                                {t("loginPage.registerLink")}
                                            </Link>
                                        </Grid>
                                    </Grid>
                                </form>
                            </div>
                        </div>
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

export default withTranslation()(LoginPage);
