import React, {Component} from "react";
import {withTranslation} from "react-i18next";
import {withRouter} from "react-router-dom";
import {getFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import TablePagination from "@material-ui/core/TablePagination";

class AdminReportComponent extends Component {

    constructor(props) {
        super(props);
        const {t} = this.props;
        const columnsToDisplay = [
            {id: "login", label: "Login", align: "left"},
            {id: "ip", label: t("adminReport.ip"), align: "center"},
            {id: "lastAuthentication", label: t("adminReport.lastAuthentication"), align: "center"}
            ];

        this.state = {
            dataToShow: [],
            columns: columnsToDisplay,
            rowsPerPage: 5,
            page: 0
        };
    }

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

    componentDidMount() {
        this.sendGetRequest();
    }

    sendGetRequest = () => {
        const {t} = this.props;
        getFetch(`/app/account/admin-report`)
            .then(response => {
                if (response.ok) {
                    return response;
                } else if(response.status===400) {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                this.setState({
                    dataToShow: data
                });
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("account.ERROR"));
        });
    };


    showUserReport = (row) => {
        const date = new Date(row.lastAuthentication);
        const dateToShow = date.getFullYear() + "/"
            + this.addZeroToDateElement(date.getMonth()+1) + "/"
            + this.addZeroToDateElement(date.getDate()) + ", "
            + this.addZeroToDateElement(date.getHours()) + ":"
            + this.addZeroToDateElement(date.getMinutes()) + ":"
            + this.addZeroToDateElement(date.getSeconds());

        return(
            <TableRow hover role="checkbox" tabIndex={-1} key={row.login}>
                <TableCell>
                    {row.login}
                </TableCell>
                <TableCell align="center">
                    {row.ip}
                </TableCell>
                <TableCell align="center">
                    {dateToShow}
                </TableCell>
            </TableRow>
        )
    };

    addZeroToDateElement = (element) => {
        if (element < 10) {
            return "0" + element;
        } else return element;
    };


    render() {
        const {t} = this.props;
        return (
            <Card>
                <CardHeader
                    title={t("adminReport.button")}
                    className="card-header"
                />
                <CardContent>
                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    {this.state.columns.map((column) => (
                                        <TableCell align={column.align}>
                                            {column.label}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {this.state.dataToShow.slice(this.state.page * this.state.rowsPerPage, this.state.page * this.state.rowsPerPage + this.state.rowsPerPage)
                                    .map((row) => {
                                        return this.showUserReport(row);
                                    })}
                            </TableBody>
                        </Table>
                        <TablePagination
                            rowsPerPageOptions={[5, 10, 15]}
                            component="div"
                            count={this.state.dataToShow.length}
                            rowsPerPage={this.state.rowsPerPage}
                            page={this.state.page}
                            onChangePage={this.handleChangePage}
                            onChangeRowsPerPage={this.handleChangeRowsPerPage}
                            labelRowsPerPage={t("showAllAccounts.rowsPerPage")}
                        />
                    </TableContainer>
                </CardContent>
            </Card>
        );
    }
}

export default withTranslation()(withRouter(AdminReportComponent));
