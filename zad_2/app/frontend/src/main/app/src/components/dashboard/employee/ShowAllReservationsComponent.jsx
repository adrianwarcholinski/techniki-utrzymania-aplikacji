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
import {getFetch, putFetch} from "../../../utils/fetchUtility";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import {Card} from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import {withRouter} from "react-router-dom";
import {urls} from "../../../const/Urls";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import ConfirmDialog from "../templates/ConfirmDialog";
import i18n from "./../../../i18n"


class ShowAllReservationsComponent extends Component {
    constructor(props) {
        super(props);
        const {t} = this.props;
        document.title = t("allReservations.employeePageTitle");

        const columnsToDisplay = [
            {id: "reservationNumber", label: t("allReservations.reservationNumber")},
            {id: "customerLogin", label: t("allReservations.login")},
            {id: "alleyName", label: t("allReservations.alleyName")},
            {id: "weaponModelName", label: t("allReservations.weaponModelName")},
            {id: "startDate", label: t("allReservations.startDate")},
            {id: "endDate", label: t("allReservations.endDate")},
            {id: "status", label: t("allReservations.status")},
            {id: "detailsButton", label: " "},
            {id: "cancelButton", label: " "},
        ];

        this.state = {
            openDialog: false,
            reservationNumber: undefined,
            columns: columnsToDisplay,
            rowsPerPage: 5,
            page: 0,
            dataToShow: [],
            detailsData: null,
            loading: true,
            tableLoading: false,
            getCanceled: false,
            getPast: false
        };
    }

    componentDidMount() {
        this.sendAllReservationsRequest();
    }

    calculateState = (data) => {
        let startDate = new Date(data.startDate);
        let endDate = new Date(data.endDate);
        let currentDate = new Date();
        if (data.active === false) {
            return "reservationStatuses.canceled";
        } else if (currentDate < startDate) {
            return "reservationStatuses.booked";
        } else if (currentDate >= startDate && currentDate <= endDate) {
            return "reservationStatuses.inProgress";
        } else if (currentDate > endDate) {
            return "reservationStatuses.finished";
        }
    };

    sendAllReservationsRequest = () => {
        const {t} = this.props;
        getFetch(`/app/reservation?canceled=` + this.state.getCanceled + "&past=" + this.state.getPast)
            .then(response => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            })
            .then(response => response.json())
            .then(data => {
                this.setState({
                    dataToShow: data,
                    loading: false,
                    tableLoading: false
                });
                this.changePageIfCurrentIsHigher();
            })
            .catch(() => this.props.displaySnackbar(SnackBarType.error, t("allReservations.error")))
    };

    sendPutCancelRequest = () => {
        const {t} = this.props;
        const header = {
            headers: {
                "reservationNumber": this.state.reservationNumber,
                "language": i18n.language,
            }
        };

        putFetch('/app/reservation/cancel-reservation', undefined, header)
            .then((response) => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("allReservations.canceled"));
                } else if(response.status===400) {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("account.ERROR"));
        }).finally(() => {
            this.sendAllReservationsRequest();
        })
    };


    handleChangeRowsPerPage = (event) => {
        this.setState({
            rowsPerPage: event.target.value,
            page: 0
        });
    };

    handleChangePage = (event, newPage) => {
        this.setState({
            page: newPage
        });
    };

    handleCloseDialog = () => {
        this.setState({
            openDialog: false
        })
    };

    handleOpenDialog = () =>  {
        this.setState({
            openDialog: true
        })
    };

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({
                tableLoading: true
            });
            this.sendPutCancelRequest();
        }
    };

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

    handleCheckboxChange = (event) => {
        this.setState({
                [event.target.name]: event.target.checked,
                tableLoading: true
            },
            this.sendAllReservationsRequest);
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
                        {t("allReservations.employeeTableTitle")}
                    </Grid>
                    <Grid item xs={6}
                          direction="row"
                          justify="space-between"
                          alignItems="center"
                          container>
                    <Grid item xs={6}>
                        <FormControlLabel
                            control={<Checkbox checked={this.state.getPast} disabled={this.state.tableLoading}
                                               onChange={this.handleCheckboxChange} name="getPast" style={{margin:0, marginRight:5, padding: 0}}/>}
                            label={t("allReservations.getFinished")}
                        />
                    </Grid>
                    <Grid item xs={6} >
                        <FormControlLabel
                            control={<Checkbox checked={this.state.getCanceled} disabled={this.state.tableLoading}
                                               onChange={this.handleCheckboxChange} name="getCanceled" style={{margin:0, marginRight:5, padding: 0}}/>}
                            label={t("allReservations.getCanceled")}
                        />
                    </Grid>
                    </Grid>
                </Grid>
            );
        };

        document.title = t("allReservations.employeePageTitle");
        if (!this.state.loading) {
            return (
                <Card>
                    <CardHeader
                        name={"allReservationsHeader"}
                        title={tableHeader()}
                        className="card-header"
                    />
                    <CardContent>
                        {this.state.tableLoading ? <LinearProgress/> :
                            <TableContainer>
                                <Table>
                                    <TableHead>
                                        <TableRow>
                                            {this.state.columns.map((column) => {
                                                return (
                                                    <TableCell>
                                                        {column.label}
                                                    </TableCell>);
                                            })}
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {this.state.dataToShow.slice(this.state.page * this.state.rowsPerPage, this.state.page * this.state.rowsPerPage + this.state.rowsPerPage)
                                            .map((row) => {
                                                    return (
                                                        <TableRow name={row.reservationNumber} hover role="checkbox"
                                                                  tabIndex={-1} key={row.reservationNumber}>
                                                            <TableCell>
                                                                {row.reservationNumber}
                                                            </TableCell>
                                                            <TableCell>
                                                                {row.login}
                                                            </TableCell>
                                                            <TableCell>
                                                                {row.alleyName}
                                                            </TableCell>
                                                            <TableCell>
                                                                {row.weaponModelName}
                                                            </TableCell>
                                                            <TableCell>
                                                                {new Date(row.startDate).toLocaleDateString()}<br/>{new Date(row.startDate).toLocaleTimeString()}
                                                            </TableCell>
                                                            <TableCell>
                                                                {new Date(row.endDate).toLocaleDateString()}<br/>{new Date(row.endDate).toLocaleTimeString()}
                                                            </TableCell>
                                                            <TableCell>
                                                                {t(this.calculateState(row))}
                                                            </TableCell>
                                                            <TableCell >
                                                                <Button name="showReservationDetailsButton"
                                                                        style={{background: "pink"}}
                                                                        onClick={() => {
                                                                            this.props.history.push(urls.reservationDetails.split(":")[0] + row.reservationNumber)
                                                                        }}>{t("allReservations.details")}</Button>
                                                            </TableCell>
                                                            <TableCell >
                                                                <Button name="cancelReservationDetailsButton"
                                                                        style={{background: "pink"}}
                                                                        disabled={this.calculateState(row) !== "reservationStatuses.booked"}
                                                                        onClick={() => {
                                                                            this.handleOpenDialog();
                                                                            this.setState({
                                                                                reservationNumber: row.reservationNumber
                                                                            });
                                                                        }}>
                                                                    {t("allReservations.cancel")}</Button>
                                                            </TableCell>
                                                        </TableRow>
                                                    )
                                                }
                                            )}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        }
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
                        <ConfirmDialog open={this.state.openDialog}
                                       title={t("allReservations.confirmDialogTitle")}
                                       content={t("allReservations.confirmDialogContent")}
                                       handleDialogResponse={this.handleDialogResponse.bind(this)}/>
                    </CardContent>
                </Card>
            )
        } else {
            return <LinearProgress/>
        }
    }
}


export default withTranslation()(withRouter(ShowAllReservationsComponent));
