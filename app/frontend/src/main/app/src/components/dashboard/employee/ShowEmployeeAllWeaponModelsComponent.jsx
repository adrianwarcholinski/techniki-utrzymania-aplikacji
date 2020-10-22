import React, {Component} from "react";
import {Card} from "@material-ui/core";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import {withTranslation} from "react-i18next";
import {withRouter} from "react-router-dom";
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import Button from "@material-ui/core/Button";
import {deleteFetch, getFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import TablePagination from "@material-ui/core/TablePagination";
import LinearProgress from "@material-ui/core/LinearProgress";
import Rating from '@material-ui/lab/Rating';
import ConfirmDialog from "../templates/ConfirmDialog";
import {urls} from "../../../const/Urls";

class ShowEmployeeAllWeaponModelsComponent extends Component {
    constructor(props) {
        super(props);
        const {t} = this.props;
        document.title = t("showAllWeaponModels.pageTitle");

        const columnsToDisplay = [
            {id: "name", label: t("showAllWeaponModels.name"), align: "left"},
            {id: "weaponCategoryName", label: t("showAllWeaponModels.weaponCategoryName"), align: "center"},
            {id: "caliberMm", label: t("showAllWeaponModels.caliber"), align: "center"},
            {id: "averageRate", label: t("showAllWeaponModels.averageRate"), align: "center"},
            {id: "detailsButton", label: " "},
            {id: "deleteButton", label: " "}
        ];

        this.state = {
            dataToShow: [],
            columns: columnsToDisplay,
            rowsPerPage: 10,
            page: 0,
            loading: true,
            openDialog: false,
            weaponModelToRemove: undefined
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
        this.getAllAlleysRequest();
    }

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
            this.deleteWeaponModelRequest();
        }
    };

    removeWeaponModel = (name) => {
        this.setState({
            weaponModelToRemove: name
        }, this.handleOpenDialog)
    };

    getAllAlleysRequest = () => {
        const {t} = this.props;
        getFetch(`/app/weapon-model/get-active-weapon-models`)
            .then(response => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                this.setState({
                    dataToShow: data,
                    loading: false
                });
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("account.ERROR"));
        });
    };

    deleteWeaponModelRequest = () => {
        const {t} = this.props;
        deleteFetch(`/app/weapon-model?name=` + this.state.weaponModelToRemove)
            .then(response => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("showAllWeaponModels.deleteSuccess"));
                } else if (response.status === 400) {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
                this.getAllAlleysRequest();
            }).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        });
    };

    render() {
        const {t} = this.props;
        if (this.state.loading) {
            return <LinearProgress/>
        } else {
            return (
                <Card>
                    <CardHeader
                        title={t("showAllWeaponModels.pageTitle")}
                        className="card-header"
                    >
                    </CardHeader>
                    <CardContent>
                        <TableContainer>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        {this.state.columns.map((column) => {
                                            return (
                                                <TableCell>
                                                    {column.label}
                                                </TableCell>)
                                        })
                                        }
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {this.state.dataToShow.slice(this.state.page * this.state.rowsPerPage, this.state.page * this.state.rowsPerPage + this.state.rowsPerPage)
                                        .map((row) => {
                                            return (
                                                <TableRow name={row.name} hover role="checkbox" tabIndex={-1}
                                                          key={row.name}>
                                                    <TableCell>
                                                        {row.name}
                                                    </TableCell>
                                                    <TableCell>
                                                        {t("weaponCategory." + row.weaponCategoryName)}
                                                    </TableCell>
                                                    <TableCell>
                                                        {row.caliberMm}
                                                    </TableCell>
                                                    <TableCell>
                                                        {row.averageRate !== undefined &&
                                                        <Rating precision={0.5} value={row.averageRate} readOnly/>
                                                        }
                                                    </TableCell>
                                                    <TableCell style={{width: "150px"}}>
                                                        <Button name="showAlleyDetailsButton"
                                                                style={{background: "pink"}}
                                                                onClick={() => {
                                                                    this.props.history.push(urls.weaponModelDetails.split(":")[0] + row.name)
                                                                }}>
                                                            {t("showAllWeaponModels.details")}
                                                        </Button>
                                                    </TableCell>
                                                    <TableCell style={{width: "110px"}}>
                                                        <Button name="deleteWeaponModelButton"
                                                                style={{background: "pink"}}
                                                                onClick={() => {
                                                                    this.removeWeaponModel(row.name)
                                                                }}
                                                        >
                                                            {t("showAllWeaponModels.delete")}
                                                        </Button>
                                                    </TableCell>
                                                </TableRow>)
                                        })
                                    }
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
                    <ConfirmDialog open={this.state.openDialog}
                                   title={t("showAllWeaponModels.confirmDeleteDialogTitle")}
                                   content={t("showAllWeaponModels.confirmDeleteDialogContent")}
                                   handleDialogResponse={this.handleDialogResponse}/>
                </Card>
            )
        }
    }
}

export default withTranslation()(withRouter(ShowEmployeeAllWeaponModelsComponent));
