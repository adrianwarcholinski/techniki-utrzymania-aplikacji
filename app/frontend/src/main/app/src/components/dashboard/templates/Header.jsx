import React, {Component} from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import Grid from '@material-ui/core/Grid';
import AccountCircleIcon from '@material-ui/icons/AccountCircle';
import MeetingRoomIcon from '@material-ui/icons/MeetingRoom';
import {withTranslation} from "react-i18next";

import RoleComponent from "./RoleComponent";
import '../../../resources/styles/Header.scss';
import MenuItem from "@material-ui/core/MenuItem";
import Menu from "@material-ui/core/Menu";
import {Link as RouterLink, withRouter} from "react-router-dom";
import {breadcrumbsNameMap, disabledUrls, notRepresentedUrls, urls} from "../../../const/Urls";
import Breadcrumbs from "@material-ui/core/Breadcrumbs";
import Link from "@material-ui/core/Link";
import {Home} from "@material-ui/icons";

class Header extends Component {

    constructor(props) {
        super(props);
        this.state = {
            activeRole: props.activeRole,
            roles: props.roles,
            userName: props.userName,
            changeRoleHandler: props.changeRoleHandler,
            loading: false,
            anchorEl: null
        };
    }

    handleMenu = event => {
        this.setState({anchorEl: event.currentTarget});
    };

    handleClose = () => {
        this.setState({anchorEl: null});
    };

    handleLogoutButton = () => {
        this.setState({
            loading: true
        });
        this.props.userLogoutHandler();
    };

    render() {
        const pathnames = window.location.pathname.split('/').filter((x) => x);

        const {t} = this.props;
        const open = Boolean(this.state.anchorEl);
        const buttonColorClass = this.props.history.location.pathname === urls.ownAccount ? this.props.buttonColorClass : 'account-button-color';

        const LinkRouter = (props) => <Link {...props} component={RouterLink}/>;

        return (
            <AppBar class='navbar'>
                <Toolbar>
                    <Grid container direction='row' justify='space-between' alignItems="center">
                        <Grid item>
                            <Breadcrumbs aria-label="breadcrumb">
                                {pathnames.map((value, index) => {
                                    const to = `/${pathnames.slice(0, index + 1).join('/')}`;
                                    const last = index === pathnames.length - 1;
                                    const first = index === 0;

                                    if (notRepresentedUrls.includes(value) || breadcrumbsNameMap[to] === undefined) {
                                        return null;
                                    }

                                    const disabled = disabledUrls.includes(to);

                                    return last || disabled ? (
                                        <Typography className='breadcrumbs-text' key={to}>
                                            {t(breadcrumbsNameMap[to])}
                                        </Typography>
                                    ) : first ? (
                                            <Grid container alignItems='center'>
                                                <Home className='home-icon'/>
                                                <LinkRouter className='breadcrumbs-text' to={to} key={to}>
                                                    {t(breadcrumbsNameMap[to])}
                                                </LinkRouter>
                                            </Grid>
                                        ) :
                                        (
                                            <LinkRouter className='breadcrumbs-text' to={to} key={to}>
                                                {t(breadcrumbsNameMap[to])}
                                            </LinkRouter>
                                        );
                                })}
                            </Breadcrumbs>
                        </Grid>
                        <Grid item>
                            <Grid container alignItems='center'>
                                <RoleComponent
                                    activeRole={this.props.activeRole}
                                    roles={this.props.roles}
                                    changeRoleHandler={this.props.changeRoleHandler}
                                    buttonColorClass={this.props.buttonColorClass}/>
                                <Button
                                    name="AccountMenu"
                                    variant="contained"
                                    color="primary"
                                    size="large"
                                    className={'account-button ' + buttonColorClass}
                                    endIcon={<AccountCircleIcon className='account-circle-icon'/>}
                                    aria-controls="customized-menu"
                                    aria-haspopup="true"
                                    onClick={this.handleMenu}>
                                    {this.state.userName}
                                </Button>
                                <Menu
                                      name="AccountMenu"
                                      keepMounted
                                      anchorEl={this.state.anchorEl}
                                      open={open}
                                      onClose={this.handleClose}
                                      getContentAnchorEl={null}
                                      anchorOrigin={{vertical: "bottom", horizontal: "center"}}
                                      transformOrigin={{vertical: "top", horizontal: "center"}}>
                                    <MenuItem name="ownAccount" onClick={() => {
                                        this.props.history.push(urls.ownAccount);
                                        this.handleClose();
                                    }}>
                                        <AccountCircleIcon className='account-menu-item-icon'/>
                                        <Typography className='account-menu-item-typography' variant='body1'
                                                    align='center'>
                                            {t('dashboard.accountButton')}
                                        </Typography>
                                    </MenuItem>
                                    <MenuItem name="logOut" onClick={this.handleLogoutButton}>
                                        <MeetingRoomIcon className='account-menu-item-icon'/>
                                        <Typography className='account-menu-item-typography' variant='body1'
                                                    align='center'>
                                            {t('dashboard.logoutButton')}
                                        </Typography>
                                    </MenuItem>
                                </Menu>
                            </Grid>
                        </Grid>
                    </Grid>
                </Toolbar>
            </AppBar>
        );
    }
}

export default withTranslation()(withRouter(Header));
