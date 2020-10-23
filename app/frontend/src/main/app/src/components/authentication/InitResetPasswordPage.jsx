import React, {Component} from "react";
import Container from "@material-ui/core/Container";
import CssBaseline from "@material-ui/core/CssBaseline";
import Icon from "@material-ui/core/Icon";
import LockOpenlinedIcon from "@material-ui/icons/LockOpen";
import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import Copyright from "../templates/Copyright";
import {withTranslation} from "react-i18next";
import CircularProgress from "@material-ui/core/CircularProgress";
import SnackBar, {SnackBarType} from "../dashboard/templates/SnackBar";
import {postFetch} from "../../utils/fetchUtility";
import Link from "@material-ui/core/Link";
import "../../resources/styles/Footer.scss";
import {urls} from "../../const/Urls";

class InitResetPasswordPage extends Component {

    constructor(props) {
        super(props);

        this.state = {
            email: "",
            loading: false,
            openSnackBar: false,
            response: null,
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(event) {
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    sendRequest() {
        postFetch('/app/auth/send-reset-link', null, {
            headers: {
                "email": this.state.email,
                "language": navigator.language,
            }
        }).then(
            (res) => {
                if (res.status === 200) {
                    this.handleOpenSnackBar(res.status, null);
                } else if(res.status===400){
                    res.text().then(
                        (data) => this.handleOpenSnackBar(res.status, data));
                }
            }
        ).finally(() => {
            this.setState({loading: false})
        });
    }

    handleSubmit(event) {
        this.sendRequest();
        this.setState({loading: true});
        event.preventDefault();
    }

    handleOpenSnackBar(result, reason) {
        const {t} = this.props;
        const message = t(reason);
        if (result === 200) {
            this.setState({
                response: SnackBarType.success,
                responseMessage: t("resetPasswordPage.successResult"),
                openSnackBar: true
            });
        } else {
            this.setState({
                response: SnackBarType.error,
                responseMessage: message,
                openSnackBar: true
            });
        }
    }

    handleSnackBarClose() {
        this.setState({openSnackBar: false})
    }

    render() {
        const {loading} = this.state;
        const {t} = this.props;
        document.title = t("loginPage.pageTitle");
        return (
            <div className="background">
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
                                    <LockOpenlinedIcon fontSize="large"/>
                                </Icon>
                                <Typography component="h1" variant="h4">
                                    {t("resetPasswordPage.resetPassword")}
                                </Typography>
                                <form onSubmit={this.handleSubmit}>
                                    <TextField style={{background: "lightgrey"}}
                                               type="email"
                                               onChange={this.handleChange}
                                               variant="filled"
                                               margin="normal"
                                               fullWidth
                                               name="email"
                                               label={t("resetPasswordPage.emailField")}
                                               required
                                    />
                                    {loading === false &&
                                    <Grid container style={{paddingTop: 15}}>
                                        <Grid item xs>
                                            <Button
                                                name="resetButton"
                                                type="submit"
                                                fullWidth
                                                variant="contained"
                                                color="primary"
                                            >
                                                {t("resetPasswordPage.send")}
                                            </Button>
                                        </Grid>

                                        <Grid item xs={1}/>

                                        <Grid item xs={3}>
                                            <Button
                                                fullWidth
                                                variant="contained"
                                                onClick={this.props.history.goBack}
                                            >
                                                {t("resetPasswordPage.cancel")}
                                            </Button>
                                        </Grid>
                                    </Grid>
                                    }
                                    {loading === true &&
                                    <Grid>
                                        <CircularProgress/>
                                    </Grid>
                                    }
                                    <Grid container style={{paddingTop: 5}}>
                                        <Grid item sm>
                                            {loading === false &&
                                            <Link href={urls.login} color="inherit" variant="body2">
                                                {t("loginPage.pageTitle")}
                                            </Link>
                                            }
                                        </Grid>
                                    </Grid>
                                </form>
                            </div>
                        </div>
                        <SnackBar open={this.state.openSnackBar} message={this.state.responseMessage}
                                  type={this.state.response} handleSnackBarClose={this.handleSnackBarClose.bind(this)}/>
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

export default withTranslation()(InitResetPasswordPage);
