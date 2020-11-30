import React, {Component} from 'react';
import LoginPage from "./components/authentication/LoginPage"
import {BrowserRouter, Redirect, Route, Switch, withRouter} from "react-router-dom";
import RegisterPage from "./components/authentication/RegisterPage";
import Dashboard from "./components/dashboard/Dashboard";
import Cookies from "js-cookie"
import {localStorageKeys} from "./const/LocalStorageKeys";
import InitResetPasswordPage from "./components/authentication/InitResetPasswordPage";
import ResetPasswordPage from "./components/authentication/ResetPasswordPage.jsx";
import {getFetch} from "./utils/fetchUtility"
import {withTranslation} from "react-i18next";
import InfoPage from "./components/InfoPage";
import {urls} from "./const/Urls";

class App extends Component {

    authenticationTimesData = {correct: undefined, incorrect: undefined, renderAfterLogin: false};

    SuccessfulLoginHandler(authenticationData) {
        localStorage.setItem(localStorageKeys.userName, authenticationData.user);
        localStorage.setItem(localStorageKeys.roles, JSON.stringify(authenticationData.roles));
        if (authenticationData.successfulAuthenticationTime !== null) {
            this.authenticationTimesData.correct = new Date(authenticationData.successfulAuthenticationTime).toLocaleString();
        }
        if (authenticationData.failedAuthenticationTime !== null) {
            this.authenticationTimesData.incorrect = new Date(authenticationData.failedAuthenticationTime).toLocaleString();
        }
        this.props.history.push(urls.dashboard);
    };

    userLogoutHandler() {
        getFetch('/app/auth/logout')
            .then((response) => {
                if (!response.ok) {
                    throw Error(response.status);
                }
                return response;
            })
            .then(() => this.clearLocalStorageAndCookies())
            .catch((error) => {
                this.clearLocalStorageAndCookies();
                if (error.message !== "401") {
                    alert(this.props.t("common.requestError"));
                }
            });
    };

    clearLocalStorageAndCookies() {
        localStorage.clear();
        Cookies.remove("JREMEMBERMEID", {path: process.env.PUBLIC_URL});
        Cookies.remove("JREMEMBERMEID");
        window.location.href = urls.login;
    }

    checkLocalStorage() {
        if (localStorage.getItem(localStorageKeys.roles) === null ||
            localStorage.getItem(localStorageKeys.userName) === null) {
            this.clearLocalStorageAndCookies();
        }
    }

    render() {
        if (Cookies.get("JREMEMBERMEID")) { //Authenticated user
            this.checkLocalStorage();
            return (
                <div>
                    <BrowserRouter basename={process.env.PUBLIC_URL}>
                        <Switch>
                            <Route path={urls.dashboard}>
                                <Dashboard userLogoutHandler={this.userLogoutHandler.bind(this)}
                                           authenticationTimesData={this.authenticationTimesData}/>
                            </Route>
                            <Route exact path={urls.newAccountVerify}
                                   render={(props) => <InfoPage {...props} endpoint={"/app/account/verify"}
                                                                headerMessage={"verifyNewAccount.title"}
                                                                successMessage={"verifyNewAccount.SUCCESS"}
                                                                errorMessage={"verifyNewAccount.ERROR"}/>}/>
                            <Route exact path={urls.changeEmailVerify}
                                   render={(props) => <InfoPage {...props} endpoint={"/app/account/change-email"}
                                                                headerMessage={"infoPage.changeEmailHeader"}
                                                                successMessage={"infoPage.changeEmailSuccess"}
                                                                errorMessage={"infoPage.changeEmailError"}/>}/>
                            <Route path="/">
                                <Redirect to={urls.dashboard}/>
                            </Route>
                        </Switch>
                    </BrowserRouter>
                </div>
            );
        } else { //Not authenticated user
            localStorage.clear();
            return (
                <div>
                    <BrowserRouter>
                        <Switch>
                            <Route exact path={urls.register}>
                                <RegisterPage/>
                            </Route>

                            <Route exact path={urls.login}>
                                <LoginPage SuccessfulLoginHandler={this.SuccessfulLoginHandler.bind(this)}/>
                            </Route>

                            <Route exact path={urls.newAccountVerify}
                                   render={(props) => <InfoPage {...props} endpoint={"/app/account/verify"}
                                                                headerMessage={"verifyNewAccount.title"}
                                                                successMessage={"verifyNewAccount.SUCCESS"}
                                                                errorMessage={"verifyNewAccount.ERROR"}/>}/>

                            <Route exact path={urls.changeEmailVerify}
                                   render={(props) => <InfoPage {...props} endpoint={"/app/account/change-email"}
                                                                headerMessage={"infoPage.changeEmailHeader"}
                                                                successMessage={"infoPage.changeEmailSuccess"}
                                                                errorMessage={"infoPage.changeEmailError"}/>}/>

                            <Route exact path={urls.resetPasswordInit}>
                                <InitResetPasswordPage history={this.props.history}/>
                            </Route>

                            <Route exact path={urls.resetPasswordVerify} component={ResetPasswordPage}/>

                            <Route path="/">
                                <Redirect to={urls.login}/>
                            </Route>
                        </Switch>
                    </BrowserRouter>
                </div>
            );
        }
    }
}

export default withTranslation()(withRouter(App));
