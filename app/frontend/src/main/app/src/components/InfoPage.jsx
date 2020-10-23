import React, {Component} from 'react';
import {withTranslation} from "react-i18next";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import CircularProgress from "@material-ui/core/CircularProgress";
import {putFetch} from "../utils/fetchUtility";
import Copyright from "./templates/Copyright";
import Box from "@material-ui/core/Box";
import "../resources/styles/InfoPage.scss"
import Icon from "@material-ui/core/Icon";
import InfoIcon from '@material-ui/icons/Info';
import Button from "@material-ui/core/Button";
import Cookies from "js-cookie"
import {withRouter} from "react-router-dom";
import {urls} from "../const/Urls";

class InfoPage extends Component {

    constructor(props) {
        super(props);
        this.state = {
            toVerify: this.props.match.params.toVerify,
            message: "",
            loading: true,
            success: null
        };
        this.sendRequest();
    }

    sendRequest = () => {
        const {t} = this.props;
        putFetch(this.props.endpoint + '?toVerify=' + this.state.toVerify + '&language=' + navigator.language)
            .then((res) => {
                    if (res.status === 200) {
                        this.setState({message: t(this.props.successMessage), success: true});
                    } else if(res.status===400) {
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

    render() {
        const {t} = this.props;
        return (
            <Box className="info-page-main">
                <Box className="info-page-content">
                    <Card maxWidth="sm" m="auto" className="info-page-card">
                        <CardContent className="info-page-card-content">
                            <Icon>
                                <InfoIcon fontSize="large"/>
                            </Icon>
                            <Typography component="h1" variant="h4" style={{marginBottom: "15px"}}>
                                {t(this.props.headerMessage)}
                            </Typography>

                            {this.state.loading === true &&
                            <CircularProgress/>
                            }

                            {this.state.loading === false && this.state.success === true &&
                            <div className="successConfirmation">
                                <Typography color="" variant="body1">
                                    {this.state.message}
                                </Typography>
                            </div>
                            }

                            {this.state.loading === false && this.state.success === false &&
                            <Typography color="error" variant="body1">
                                {this.state.message}
                            </Typography>
                            }
                            {Cookies.get("JREMEMBERMEID") &&
                            <Button
                                name="submitButton"
                                style={{marginTop: "20px"}}
                                type="button"
                                fullWidth
                                variant="contained"
                                color={"primary"}
                                disabled={this.state.loading}
                                onClick={() => window.location.href = urls.dashboard}
                            >
                                Dashboard
                            </Button>
                            }
                            {!Cookies.get("JREMEMBERMEID") &&
                            <Button
                                name="submitButton"
                                style={{marginTop: "20px"}}
                                type="button"
                                fullWidth
                                variant="contained"
                                color={"primary"}
                                disabled={this.state.loading}
                                onClick={() => window.location.href = urls.login}
                            >
                                {t("infoPage.login")}
                            </Button>
                            }

                        </CardContent>
                    </Card>
                </Box>
                <Box className="info-page-footer">
                    <Copyright/>
                </Box>
            </Box>
        )
    }
}

export default withTranslation()(withRouter(InfoPage));
