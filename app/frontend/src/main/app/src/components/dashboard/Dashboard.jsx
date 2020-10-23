import React, {Component} from 'react';
import {Redirect, Route, Switch, withRouter} from "react-router-dom";
import {roles} from "../../const/Roles";
import {urls} from "../../const/Urls";
import {localStorageKeys} from "../../const/LocalStorageKeys";
import DashboardTemplate from "./templates/DashboardTemplate";
import {withTranslation} from "react-i18next";
import {AddBox, CalendarToday, People, PersonAdd} from '@material-ui/icons';
import ShowAllAccountsComponent from "./admin/ShowAllAccountsComponent";
import CreateNewAccountComponent from "./admin/CreateNewAccountComponent";
import AccountDetailsComponent from "./admin/AccountDetailsComponent";
import OwnAccountDetailsComponent from "./common/OwnAccountDetailsComponent";
import AdminReportComponent from "./admin/AdminReportComponent";
import {postFetch} from "../../utils/fetchUtility";
import MakeReservationComponent from "./customer/MakeReservationComponent";
import BookIcon from '@material-ui/icons/Book';
import CustomerAlleyDetailsComponent from "./customer/CustomerAlleyDetailsComponent";
import EmployeeAlleyDetailsComponent from "./employee/EmployeeAlleyDetailsComponent";
import ShowCustomerAllAlleysComponent from "./customer/ShowCustomerAllAlleysComponent";
import ShowEmployeeAllAlleysComponent from "./employee/ShowEmployeeAllAlleysComponent";
import ReservationDetailsComponent from "./employee/ReservationDetailsComponent";
import ListIcon from '@material-ui/icons/List';
import ShowCustomerAllWeaponModelsComponent from "./customer/ShowCustomerAllWeaponModelsComponent";
import ShowEmployeeAllWeaponModelsComponent from "./employee/ShowEmployeeAllWeaponModelsComponent";
import AddNewWeaponModelComponent from "./employee/AddNewWeaponModelComponent";
import OwnReservationDetailsComponent from "./customer/OwnReservationDetailsComponent";
import AddAlleyComponent from "./employee/AddNewAlleyComponent";
import ShowAllOwnReservationsComponent from "./customer/ShowAllOwnReservationsComponent";
import ShowAllReservationsComponent from "./employee/ShowAllReservationsComponent";
import ShowAllWeaponsComponent from "./employee/ShowAllWeaponsComponent";
import AddWeaponComponent from "./employee/AddWeaponComponent";
import ShowEmployeeWeaponModelDetailsComponent from "./employee/ShowWeaponModelDetailsComponent";
import ShowWeaponModelDetailsComponent from "./customer/ShowWeaponModelDetailsComponent";


class Dashboard extends Component {

    constructor(props) {
        super(props);
        this.state = {
            activeRole: localStorage.getItem(localStorageKeys.activeRole),
            roles: JSON.parse(localStorage.getItem(localStorageKeys.roles)),
            userName: localStorage.getItem(localStorageKeys.userName),
        };
    }

    getHighestRole = (userRoles) => {
        let RoleHierarchy = [roles.customer, roles.employee, roles.admin];
        let maxRole = null;
        RoleHierarchy.forEach(role => {
            if (userRoles.includes(role)) {
                maxRole = role;
            }
        });
        return maxRole;
    };

    changeRoleHandler = (targetRole) => {
        const {t} = this.props;
        const header = {
            headers: {"targetRole": targetRole}
        };
        if (this.state.roles.includes(targetRole)) {
            postFetch("/app/change-role", null, header)
                .then(
                    (res) => {
                        if (res.ok) {
                            this.setState({activeRole: targetRole});
                        } else if (res.status === 400) {
                            res.text().then(
                                (data) => window.alert(t(data)));
                        }
                    }
                ).catch(() => {
                window.alert(t("changeRole.error"));
            });
            localStorage.setItem(localStorageKeys.activeRole, targetRole);
        }
    };

    componentDidMount() {
        if (!(this.state.activeRole && this.state.roles.includes(this.state.activeRole))) {
            this.setState({
                activeRole: this.getHighestRole(this.state.roles)
            });
        }
    };

    render() {
        let activeButtonClass;
        let buttons;
        const {t} = this.props;
        document.title = t("dashboard.pageTitle");
        const props = {
            availableRoles: this.state.roles.filter(role => role !== this.state.activeRole),
            activeRole: this.state.activeRole,
            userName: this.state.userName,
            authenticationTimesData: this.props.authenticationTimesData,
            changeRoleHandler: this.changeRoleHandler,
            userLogoutHandler: this.props.userLogoutHandler
        };

        switch (this.state.activeRole) {
            case roles.admin:
                activeButtonClass = "menu-active-item-admin";
                buttons = [
                    {
                        icon: <People name="showAllAccountsButton"/>,
                        text: t("showAllAccounts.button"),
                        href: urls.allAccounts
                    },
                    {
                        icon: <People name="adminReportButton"/>,
                        text: t("adminReport.button"),
                        href: urls.adminReport
                    },
                    {
                        icon: <PersonAdd name="addNewAccountButton"/>,
                        text: t("addNewAccount.button"),
                        href: urls.createNewAccount
                    }
                ];
                return (
                    <Switch>
                        <Route exact path={urls.ownAccount}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<OwnAccountDetailsComponent/>}/>
                        </Route>
                        <Route exact path={urls.adminReport}
                               render={(params) =>
                                   <DashboardTemplate {...params} {...props}
                                                      buttons={buttons}
                                                      activeButtonClass={activeButtonClass}
                                                      component={<AdminReportComponent/>}/>
                               }>
                        </Route>
                        <Route exact path={urls.accountDetails}
                               render={(params) =>
                                   <DashboardTemplate {...params} {...props}
                                                      buttons={buttons}
                                                      activeButtonClass={activeButtonClass}
                                                      component={<AccountDetailsComponent/>}/>
                               }>

                        </Route>
                        <Route exact path={urls.allAccounts}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<ShowAllAccountsComponent/>}/>
                        </Route>
                        <Route exact path={urls.createNewAccount}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<CreateNewAccountComponent/>}/>
                        </Route>
                        <Route path={urls.dashboard}>
                            <Redirect to={urls.allAccounts}/>
                        </Route>
                    </Switch>
                );
            case roles.employee:
                buttons = [
                    {
                        icon: <CalendarToday name="customerReservationsButton"/>,
                        text: t("allReservations.showAllReservationsButton"),
                        href: urls.allReservation
                    },
                    {
                        icon: <ListIcon name="customerAlleysButton"/>,
                        text: t("showAllAlleys.alleysButton"),
                        href: urls.allAlleys
                    },
                    {
                        icon: <AddBox name="addNewAlleyButton"/>,
                        text: t("addNewAlley.button"),
                        href: urls.addNewAlley
                    },
                    {
                        icon: <ListIcon name="customerWeaponModelsButton"/>,
                        text: t("showAllWeaponModels.weaponModelsButton"),
                        href: urls.allWeaponModels
                    },
                    {
                        icon: <AddBox name="addNewWeaponModelButton"/>,
                        text: t("addNewWeaponModel.button"),
                        href: urls.addNewWeaponModel
                    },
                    {
                        icon: <ListIcon name="customerAlleysButton"/>,
                        text: t("showAllWeapons.button"),
                        href: urls.allWeapons
                    },
                    {
                        icon: <AddBox name="addNewWeaponButton"/>,
                        text: t("addNewWeapon.button"),
                        href: urls.addWeapon
                    },];
                activeButtonClass = "menu-active-item-employee";
                return (
                    <Switch>
                        <Route exact path={urls.addNewAlley}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<AddAlleyComponent/>}/>
                        </Route>
                        <Route exact path={urls.ownAccount}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<OwnAccountDetailsComponent/>}/>
                        </Route>
                        <Route exact path={urls.addNewWeaponModel}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<AddNewWeaponModelComponent/>}/>
                        </Route>
                        <Route exact path={urls.reservationDetails}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<ReservationDetailsComponent/>}/>
                        </Route>
                        <Route exact path={urls.allWeaponModels}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<ShowEmployeeAllWeaponModelsComponent/>}/>
                        </Route>
                        <Route exact path={urls.alleyDetails}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<EmployeeAlleyDetailsComponent/>}/>
                        </Route>
                        <Route exact path={urls.allAlleys}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<ShowEmployeeAllAlleysComponent/>}/>
                        </Route>
                        <Route exact path={urls.allReservation}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<ShowAllReservationsComponent/>}/>
                        </Route>
                        <Route exact path={urls.allWeapons}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<ShowAllWeaponsComponent/>}/>
                        </Route>
                        <Route exact path={urls.addWeapon}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<AddWeaponComponent/>}/>
                        </Route>
                        <Route exact path={urls.weaponModelDetails}
                               render={(params) =>
                                   <DashboardTemplate {...params} {...props}
                                                      buttons={buttons}
                                                      activeButtonClass={activeButtonClass}
                                                      component={<ShowEmployeeWeaponModelDetailsComponent/>}/>
                               }>
                        </Route>
                        <Route path={urls.dashboard}>
                            <Redirect to={urls.allReservation}/>
                        </Route>
                    </Switch>
                );
            case roles.customer:
                buttons = [
                    {
                        icon: <CalendarToday name="customerReservationsButton"/>,
                        text: t("allReservations.showOwnReservationsButton"),
                        href: urls.allReservation
                    },
                    {
                        icon: <BookIcon/>,
                        text: t("makeReservation.makeReservationHeader"),
                        href: urls.makeReservation
                    },
                    {
                        icon: <ListIcon name="customerAlleysButton"/>,
                        text: t("showAllAlleys.alleysButton"),
                        href: urls.allAlleys
                    },
                    {
                        icon: <ListIcon name="customerWeaponModelsButton"/>,
                        text: t("showAllWeaponModels.weaponModelsButton"),
                        href: urls.allWeaponModels
                    }];
                activeButtonClass = "menu-active-item-customer";
                return (
                    <Switch>
                        <Route exact path={urls.ownAccount}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<OwnAccountDetailsComponent/>}/>
                        </Route>
                        <Route exact path={urls.makeReservation}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<MakeReservationComponent/>}/>
                        </Route>
                        <Route exact path={urls.allReservation}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<ShowAllOwnReservationsComponent/>}/>
                        </Route>
                        <Route exact path={urls.alleyDetails}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<CustomerAlleyDetailsComponent/>}/>
                        </Route>
                        <Route exact path={urls.allAlleys}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<ShowCustomerAllAlleysComponent/>}/>
                        </Route>
                        <Route exact path={urls.reservationDetails}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<OwnReservationDetailsComponent/>}/>
                        </Route>
                        <Route exact path={urls.allWeaponModels}>
                            <DashboardTemplate {...props}
                                               buttons={buttons}
                                               activeButtonClass={activeButtonClass}
                                               component={<ShowCustomerAllWeaponModelsComponent/>}/>
                        </Route>
                        <Route exact path={urls.weaponModelDetails}
                               render={(params) =>
                                   <DashboardTemplate {...params} {...props}
                                                      buttons={buttons}
                                                      activeButtonClass={activeButtonClass}
                                                      component={<ShowWeaponModelDetailsComponent/>}/>
                               }>
                        </Route>
                        <Route path={urls.dashboard}>
                            <Redirect to={urls.allReservation}/>
                        </Route>
                    </Switch>
                );
            default:
                console.log(t("dashboard.wrongRoleWarning"));
                return null;
        }
    }
}

export default withTranslation()(withRouter(Dashboard));
