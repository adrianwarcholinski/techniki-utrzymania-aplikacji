import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import React, {Component} from "react";
import CardContent from "@material-ui/core/CardContent";
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import {withTranslation} from "react-i18next";
import {withRouter} from "react-router-dom";
import TablePagination from "@material-ui/core/TablePagination";
import {SnackBarType} from "../templates/SnackBar";
import {deleteFetch, getFetch} from "../../../utils/fetchUtility";
import Button from "@material-ui/core/Button";

import "../../../resources/styles/ShowAllWeaponsPage.scss";
import ConfirmDialog from "../templates/ConfirmDialog";
import LinearProgress from "@material-ui/core/LinearProgress";

class ShowAllWeaponsComponent extends Component {

    constructor(props) {
        super(props);
        const {t} = this.props;
        document.title = t("showAllWeapons.pageTitle");

        const columns = [
            {
                id: "weaponModelName",
                label: t("showAllWeapons.weaponModelName")
            },
            {
                id: "serialNumber",
                label: t("showAllWeapons.serialNumber")
            },
            {
                id: "DeleteButton",
                label: ""
            }
        ];

        this.state = {
            columns: columns,
            rowsPerPage: 5,
            currentPage: 0,
            data: [],
            loading: true,
            openDialog: false,
            weaponToRemove: undefined
        };
    }

    componentDidMount() {
        this.loadWeapons();
    }

    loadWeapons = () => {
        const {t} = this.props;
        getFetch('/app/weapon/get-active-weapons')
            .then(response => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    response.text()
                        .then(data => {
                            this.props.displaySnackbar(SnackBarType.error, t(data));
                        })
                }
            })
            .then(response => response.json())
            .then(data => {
                this.setState({
                    data: data,
                    loading: false
                });
                this.changePageIfCurrentIsHigher();
            })
            .catch(() => this.props.displaySnackbar(SnackBarType.error, t("showAllWeapons.error")));
    }

    changePageIfCurrentIsHigher = () => {
        if (this.state.currentPage > this.calculatePages() - 1) {
            this.setState({
                currentPage: 0
            });
        }
    };

    calculatePages = () => {
        return Math.ceil(this.state.data.length / this.state.rowsPerPage);
    };

    handleChangeRowsPerPage = (event) => {
        this.setState({
            rowsPerPage: event.target.value,
            currentPage: 0
        });
    };

    handleChangePage = (event, newPage) => {
        this.setState({
            currentPage: newPage
        });
    };

    handleCloseDialog = () => {
        this.setState({
            openDialog: false
        })
    };

    handleOpenDialog = () => {
        this.setState({
            openDialog: true
        })
    };

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({
                loading: true
            });
            this.deleteWeaponRequest();
        }
    };

    removeWeapon = (name) => {
        this.setState({
            weaponToRemove: name
        }, this.handleOpenDialog)
    };

    deleteWeaponRequest = () => {
        const {t} = this.props;
        deleteFetch(`/app/weapon/remove?serialNumber=` + this.state.weaponToRemove)
            .then(response => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("showAllWeapons.deleteSuccess"));
                } else if (response.status === 400) {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
                this.loadWeapons()
            }).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        });
    };

    render() {
        const {t} = this.props;
        document.title = t("showAllWeapons.pageTitle");
        if (this.state.loading) {
            return <LinearProgress/>
        } else {
            return (
                <Card>
                    <CardHeader name="showAllWeaponsCardHeader" title={t("showAllWeapons.header")} className="card-header"/>
                    <CardContent name="weaponsTable">
                        <TableContainer>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        {this.state.columns.map(column => {
                                            return (
                                                <TableCell>
                                                    {column.label}
                                                </TableCell>
                                            )
                                        })}
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {this.state.data.slice(this.state.currentPage * this.state.rowsPerPage,
                                        this.state.currentPage * this.state.rowsPerPage + this.state.rowsPerPage).map(row => {
                                        return (
                                            <TableRow name={row.serialNumber} hover role="checkbox" tabIndex={-1} key={row.serialNumber}>
                                                <TableCell>
                                                    {row.weaponModelName}
                                                </TableCell>
                                                <TableCell>
                                                    {row.serialNumber}
                                                </TableCell>
                                                <TableCell style={{width: "110px"}}>
                                                    <Button name="deleteWeaponButton"
                                                            style={{background: "pink"}}
                                                            onClick={() => {
                                                                this.removeWeapon(row.serialNumber)
                                                            }}>
                                                        {t("showAllWeapons.delete")}
                                                    </Button>
                                                </TableCell>
                                            </TableRow>
                                        );
                                    })}
                                </TableBody>
                            </Table>
                        </TableContainer>
                        <TablePagination
                            rowsPerPageOptions={[5, 10, 15]}
                            component="div"
                            count={this.state.data.length}
                            rowsPerPage={this.state.rowsPerPage}
                            page={this.state.currentPage}
                            onChangeRowsPerPage={this.handleChangeRowsPerPage}
                            labelRowsPerPage={t("showAllWeapons.rowsPerPage")}
                            onChangePage={this.handleChangePage}
                        />
                    </CardContent>
                    <ConfirmDialog open={this.state.openDialog}
                                   title={t("showAllWeapons.confirmDeleteDialogTitle")}
                                   content={t("showAllWeapons.confirmDeleteDialogContent")}
                                   handleDialogResponse={this.handleDialogResponse}/>
                </Card>
            );
        }
    }
}

export default withTranslation()(withRouter(ShowAllWeaponsComponent));