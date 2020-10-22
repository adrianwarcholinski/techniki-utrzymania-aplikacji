import React, {Component} from 'react';
import '../../../resources/styles/Dashboard.scss';
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import {withTranslation} from "react-i18next";
import TablePagination from "@material-ui/core/TablePagination";
import TableBody from "@material-ui/core/TableBody";
import LinearProgress from "@material-ui/core/LinearProgress";
import Checkbox from "@material-ui/core/Checkbox";
import {SnackBarType} from "../templates/SnackBar";
import {getFetch, postFetch, putFetch} from "../../../utils/fetchUtility";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import {Card} from "@material-ui/core";
import TextField from "@material-ui/core/TextField";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import i18n from "./../../../i18n"
import {withRouter} from "react-router-dom";
import {urls} from "../../../const/Urls";
import CircularProgress from "@material-ui/core/CircularProgress";
import {validateNameAndSurname} from "../../../utils/regexpUtils";


class ShowAllAccountsComponent extends Component {
    constructor(props) {
        super(props);
        const {t} = this.props;
        document.title = t("showAllAccounts.pageTitle");

        const columnsToDisplay = [
            {id: "name", label: t("showAllAccounts.name")},
            {id: "lastName", label: t("showAllAccounts.surname")},
            {id: "login", label: "Login"},
            {id: "email", label: "Email"},
            {id: "active", label: t("showAllAccounts.active")},
            {id: "verified", label: t("showAllAccounts.verified")},
            {id: "button", label: " "},
            {id: "sendVerificationButton", label: " "}
        ];

        this.state = {
            columns: columnsToDisplay,
            rowsPerPage: 5,
            page: 0,
            dataToShow: [],
            detailsData: null,
            details: false,
            response: null,
            searchPhrase: "",
            loading: true,
            loadingLock: ""
        };

        this.sendGetAllAccountsRequest = this.sendGetAllAccountsRequest.bind(this);
        this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
        this.handleChangePage = this.handleChangePage.bind(this);
        this.handleChangeSearch = this.handleChangeSearch.bind(this);
        this.sendGetFilteredAccount = this.sendGetFilteredAccount.bind(this);
        this.handleOnKeyPressSearch = this.handleOnKeyPressSearch.bind(this);
        this.sendAppropriateRequest = this.sendAppropriateRequest.bind(this);
    }

    componentDidMount() {
        this.sendGetAllAccountsRequest();
    }

    sendAppropriateRequest() {
        if (this.state.searchPhrase === "") {
            this.sendGetAllAccountsRequest();
        } else {
            this.sendGetFilteredAccount(this.state.searchPhrase);
        }
    }

    sendGetAllAccountsRequest() {
        const {t} = this.props;
        getFetch(`/app/account`)
            .then(response => {
                if (response.ok) {
                    return response;
                } else if(response.status===400) {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            })
            .then(response => response.json())
            .then(data => {
                const newState = {
                    dataToShow: data,
                    loading: false
                };
                this.setState(newState);
                this.changePageIfCurrentIsHigher();
            })
            .catch(() => this.props.displaySnackbar(SnackBarType.error, t("showAllAccounts.error")))
            .finally(() => {
                this.setState({loadingLock: ""});
            })
    }

    sendGetFilteredAccount(phrase) {
        const {t} = this.props;
        getFetch(`/app/account/filter-accounts-by-full-name?phrase=` + phrase)
            .then(response => {
                if (response.ok) {
                    return response;
                } else {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data))
                    )
                }
            })
            .then(response => response.json())
            .then(data => {
                const newState = {
                    dataToShow: data
                };
                this.setState(newState);
                this.changePageIfCurrentIsHigher();
            })
            .catch(() => this.props.displaySnackbar(SnackBarType.error, t("showAllAccounts.error")))
            .finally(() => {
                this.setState({loadingLock: ""});
            })
    }

    handleOnKeyPressSearch(event) {
        const value = event.target.value.toLowerCase().split(' ').join('');
        this.setState({
            searchPhrase: value
        });
        if (value !== "" && validateNameAndSurname(value)) {
            this.sendGetFilteredAccount(value);
            this.setState({
                page: 0,
                // searchPhrase: value
            });
        }
    }

    handleChangeSearch(event) {
        if (event.target.value === "") {
            this.sendGetAllAccountsRequest();
            this.setState({
                searchPhrase: ""
            });
        }
    }

    handleChangeRowsPerPage(event) {
        this.setState({
            rowsPerPage: event.target.value,
            page: 0
        });
    }

    handleChangePage(event, newPage) {
        this.setState({
            page: newPage
        });
    }

    calculatePages = () => {
        const rowsPerPage = this.state.rowsPerPage;
        const dataLength = this.state.dataToShow.length;
        return Math.ceil(dataLength / rowsPerPage);
    };

    changePageIfCurrentIsHigher = () => {
        if (this.state.page > this.calculatePages() - 1) {
            this.setState({
                page: 0
            });
        }
    };

    handleLockAccount = (account) => {
        const {t} = this.props;
        this.setState({loadingLock: account.login});
        putFetch('/app/account/lock-account?login=' + account.login)
            .then(response => {
                if (response.ok) {
                    this.sendNotificationEmail(true, account.email);
                    this.props.displaySnackbar(SnackBarType.success, t("showAllAccounts.successLockAccount"));
                } else {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
                this.sendAppropriateRequest();
            }).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("error.generalLockAccount"));
            account.active = true;
            this.forceUpdate();
            this.sendAppropriateRequest();
        })
    };

    handleUnlockAccount = (account) => {
        const {t} = this.props;
        this.setState({loadingLock: account.login});
        putFetch('/app/account/unlock-account?login=' + account.login)
            .then(response => {
                if (response.ok) {
                    this.sendNotificationEmail(false, account.email);
                    this.props.displaySnackbar(SnackBarType.success, t("showAllAccounts.successUnlockAccount"));
                } else {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
                this.sendAppropriateRequest();
            }).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("error.generalUnlockAccount"));
            account.active = false;
            this.forceUpdate();
            this.sendAppropriateRequest();
        })
    };

    sendNotificationEmail = (isBlocking, email) => {
        const header = {
            headers: {
                "language": i18n.language,
                "email": email,
                "isBlocking": isBlocking
            }
        };
        postFetch("/app/send-email", null, header);
    };

    handleChangeAccountActivity = (event, account) => {
        account.loading = true;
        if (event.target.checked) {
            this.handleUnlockAccount(account)
        } else {
            this.handleLockAccount(account);
        }
    };

    handleSendVerificationLink = (login) => {
        const {t} = this.props;
        const params = "login=" + login + "&language=" + navigator.language;
        postFetch('/app/account/send-verification-link?' + params)
            .then((response) => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("showAllAccounts.sendVerificationLinkMessage"));
                } else if (response.status === 400) {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        })
    };

    render() {
        const {t} = this.props;
        const tableHeader = () => {
            return (
                <Grid
                    container
                    direction="row"
                    justify="space-between"
                    alignItems="center">
                    <Grid item>
                        {t("showAllAccounts.tableTitle")}
                    </Grid>
                    <Grid item xs={3}>
                        <TextField name="showAllAccountSearchField"
                                   style={{backgroundColor: "white", borderRadius: "4px"} }
                                   label={t("showAllAccounts.search")}
                                   variant="filled"
                                   error={!validateNameAndSurname(this.state.searchPhrase)}
                                   onKeyPress={(event) => {
                                       if (event.key === 'Enter') {
                                           this.handleOnKeyPressSearch(event)
                                       }
                                   }}
                                   onChange={this.handleChangeSearch}
                                   fullWidth
                        />
                    </Grid>
                </Grid>
            );
        };

        document.title = t("showAllAccounts.pageTitle");
        if (!this.state.loading && this.state.details === false) {
            return (
                <Card>
                    <CardHeader
                        name={"showAllAccountsCardHeader"}
                        title={tableHeader()}
                        className="card-header"
                    />
                    <CardContent name="accountsTable">
                        <TableContainer>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        {this.state.columns.map((column) => {
                                            if (column.id === "verified" || column.id === "active") {
                                                return (
                                                    <TableCell align="center">
                                                        {column.label}
                                                    </TableCell>)
                                            } else {
                                                return (
                                                    <TableCell>
                                                        {column.label}
                                                    </TableCell>)
                                            }
                                        })}
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {this.state.dataToShow.slice(this.state.page * this.state.rowsPerPage, this.state.page * this.state.rowsPerPage + this.state.rowsPerPage)
                                        .map((row) => {
                                                return (
                                                    <TableRow name={row.login} hover role="checkbox" tabIndex={-1} key={row.login}>
                                                        <TableCell>
                                                            {row.name}
                                                        </TableCell>
                                                        <TableCell>
                                                            {row.surname}
                                                        </TableCell>
                                                        <TableCell name="login">
                                                            {row.login}
                                                        </TableCell>
                                                        <TableCell>
                                                            {row.email}
                                                        </TableCell>
                                                        <TableCell align="center">
                                                            {this.state.loadingLock !== row.login &&
                                                            <Checkbox name="activateCheckbox"
                                                                      checked={row.active}
                                                                      onChange={(event) => this.handleChangeAccountActivity(event, row)}
                                                                      disabled={this.state.loadingLock !== ""}/>}
                                                            {this.state.loadingLock === row.login &&
                                                            <CircularProgress color="primary" size={20}/>}
                                                        </TableCell>
                                                        <TableCell style={{width: "150px"}} align="center">
                                                            <Checkbox checked={row.verified} name="verifiedCheckbox"
                                                                      disabled={true}/>
                                                        </TableCell>
                                                        <TableCell style={{width: "150px"}}>
                                                            <Button name="showAccountDetailsButton" style={{background: "pink"}}
                                                                    onClick={() => {
                                                                        this.props.history.push(urls.accountDetails.split(":")[0] + row.login)
                                                                    }}>{t("account.details")}</Button>
                                                        </TableCell>
                                                        <TableCell style={{width: "150px"}}>
                                                            <Button name="sendVerificationLinkButton" style={{background: "pink"}}
                                                                    onClick={() => this.handleSendVerificationLink(row.login)}
                                                                    disabled={row.verified}>{t("showAllAccounts.sendVerificationLink")}
                                                            </Button>
                                                        </TableCell>
                                                    </TableRow>
                                                )
                                            }
                                        )}
                                </TableBody>
                            </Table>
                        </TableContainer>
                        <TablePagination
                            rowsPerPageOptions={[5, 10, 15]}
                            component="div"
                            count={this.state.dataToShow.length}
                            rowsPerPage={this.state.rowsPerPage}
                            page={this.state.page}
                            onChangeRowsPerPage={this.handleChangeRowsPerPage}
                            labelRowsPerPage={t("showAllAccounts.rowsPerPage")}
                            onChangePage={this.handleChangePage}
                        />
                    </CardContent>
                </Card>
            )
        } else {
            return <LinearProgress/>
        }
    }
}


export default withTranslation()(withRouter(ShowAllAccountsComponent));
