import React, {Component} from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import Box from '@material-ui/core/Box';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import "../../../resources/styles/Dashboard.scss";
import Grid from "@material-ui/core/Grid";
import Header from "./Header";
import {withTranslation} from "react-i18next";
import Copyright from "../../templates/Copyright";
import appLogo from "../../../resources/images/logo.svg"
import ListItemIcon from "@material-ui/core/ListItemIcon";
import Typography from "@material-ui/core/Typography";
import SnackBar, {SnackBarType} from "./SnackBar";
import {withRouter} from "react-router-dom";
import {urls} from "../../../const/Urls";

class DashboardTemplate extends Component {
    constructor(props) {
        super(props);
        this.state = {
            active_tab: this.props.buttons[0].text,
            snackbarOpen: false,
            snackbarMessage: null,
            snackbarType: null,
            snackbarAnchorOrigin: {
                vertical: 'bottom',
                horizontal: 'center'
            }
        }
    }

    componentDidMount() {
        const info = this.props.authenticationTimesData;
        if (info.correct !== undefined || info.incorrect !== undefined) {
            this.handleDisplaySnackbar(SnackBarType.info, this.getAuthenticationMessage(), {
                vertical: 'bottom',
                horizontal: 'right'
            });
        }
    }

    getAuthenticationMessage() {
        const {t} = this.props;
        const {correct, incorrect} = this.props.authenticationTimesData;
        return <>{correct !== undefined && t("dashboard.lastCorrectAuthentication") + ": " + correct}
            {(correct !== undefined && incorrect !== undefined)&&<br/>}
            {incorrect !== undefined && t("dashboard.lastIncorrectAuthentication") + ": " + incorrect}</>;
    }

    handleDisplaySnackbar = (type, message, anchorOrigin = {
        horizontal: 'center',
        vertical: 'bottom'
    }) => {
        this.setState({
            snackbarMessage: message,
            snackbarType: type,
            snackbarOpen: true,
            snackbarAnchorOrigin: anchorOrigin
        });
    };

    handleCloseSnackbar = () => {
        this.setState({snackbarOpen: false});
    };

    render() {
        const {t} = this.props;
        return (
            <div className="root">
                <SnackBar open={this.state.snackbarOpen} message={this.state.snackbarMessage}
                          type={this.state.snackbarType} handleSnackBarClose={this.handleCloseSnackbar}
                          anchorOrigin={this.state.snackbarAnchorOrigin}/>
                <CssBaseline/>
                <Grid container direction="row" wrap={"nowrap"} alignItems="stretch" style={{minHeight: "100vh"}}>
                    <Grid item style={{width: "230px", minWidth: "230px"}}>
                        <Box className="sidebar">
                            <List>
                                <ListItem name="logo" style={{top: "-3px"}} button className="logo-item"
                                          onClick={() => this.props.history.push(urls.dashboard)}>
                                    <ListItemIcon className="logo-icon">
                                        <img src={appLogo} alt={"logo"} style={{width: "30px"}}/>
                                    </ListItemIcon>
                                    <ListItemText primary={t("dashboard.appName").toUpperCase()}/>
                                </ListItem>
                                <Divider variant="middle" className="divider"/>
                                {this.props.buttons.map((button, index) => (
                                    <ListItem button key={index}
                                              className={this.props.history.location.pathname === button.href ? this.props.activeButtonClass + " menu-button-item" : "menu-button-item"}
                                              onClick={() => this.props.history.push(button.href)}>
                                        <ListItemIcon className="menu-icon">
                                            {button.icon}
                                        </ListItemIcon>
                                        <ListItemText className="menu-text"
                                                      primary={<Typography variant="body2"
                                                                           style={{fontWeight: "300"}}>{button.text}</Typography>}/>
                                    </ListItem>
                                ))}
                            </List>
                        </Box>
                    </Grid>
                    <Grid item style={{width:"calc(100% - 230px)"}}>
                        <Grid item xs={12} className="header">
                            <Header activeRole={this.props.activeRole}
                                    roles={this.props.availableRoles}
                                    userName={this.props.userName}
                                    changeRoleHandler={this.props.changeRoleHandler}
                                    userLogoutHandler={this.props.userLogoutHandler}
                                    buttonColorClass={this.props.activeButtonClass}/>

                        </Grid>
                        <Grid item xs={12} className="content">
                            {this.props.component ? React.cloneElement(this.props.component, {displaySnackbar: this.handleDisplaySnackbar, ...this.props}) : null}
                        </Grid>
                        <Grid item xs={12} className="footer">
                            <Copyright/>
                        </Grid>
                    </Grid>
                </Grid>
            </div>
        )
    }
}

export default withTranslation()(withRouter(DashboardTemplate));
