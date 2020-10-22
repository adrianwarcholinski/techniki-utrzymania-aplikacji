import TableCell from "@material-ui/core/TableCell";
import Button from "@material-ui/core/Button";
import TableRow from "@material-ui/core/TableRow";
import React, {Component} from "react";
import {withTranslation} from "react-i18next";
import {withRouter} from "react-router-dom";
import {getFetch, putFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import {Card} from "@material-ui/core";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableBody from "@material-ui/core/TableBody";
import TablePagination from "@material-ui/core/TablePagination";
import LinearProgress from "@material-ui/core/LinearProgress";
import {urls} from "../../../const/Urls";
import ConfirmDialog from "../templates/ConfirmDialog";

class ShowEmployeeAllAlleysComponent extends Component{
    constructor(props) {
        super(props);
        const {t} = this.props;
        document.title = t("showAllAlleys.pageTitle");

        const columnsToDisplay = [
            {id: "name", label: t("showAllAlleys.name"), align: "left"},
            {id: "difficultyLevelName", label: t("showAllAlleys.difficultyLevel"), align: "center"},
            {id: "detailsButton", label: " "},
            {id: "deleteButton", label: " "}
        ];

        this.state = {
            dataToShow: [],
            columns: columnsToDisplay,
            rowsPerPage: 10,
            page: 0,
            loading: true,
            openDial: false,
            alleyToDelete: ""
        };
    }

    componentDidMount() {
        this.getAllAlleysRequest();
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

    handleRemoveAlley = (alleyName) => {
        this.setState({
            alleyToDelete: alleyName
        }, this.handleOpenDialog);
    };

    removeAlleyRequest = () => {
        const {t} = this.props;
        putFetch(`/app/alley/remove-alley?alleyName=${this.state.alleyToDelete}`)
            .then(response => {
                if(response.ok){
                    this.props.displaySnackbar(SnackBarType.success, t("showAllAlleys.successDelete"));
                }else {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("account.ERROR"));
        })
            .finally(() => {
                this.setState({
                    loading: true
                }, () => this.getAllAlleysRequest());
            })
    }

    getAllAlleysRequest = () => {
        const {t} = this.props;
        getFetch(`/app/alley/get-active-alleys`)
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
                    dataToShow: data,
                    loading: false
                });
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("account.ERROR"));
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
            }, this.removeAlleyRequest);
        }
    };

    render() {
        const {t} = this.props;
        if (this.state.loading ) {
            return <LinearProgress/>
        }
        else {
            return (
                <Card>
                    <CardHeader
                        title={t("showAllAlleys.pageTitle")}
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
                                                <TableRow name={row.name} hover role="checkbox" tabIndex={-1} key={row.name}>
                                                    <TableCell>
                                                        {row.name}
                                                    </TableCell>
                                                    <TableCell>
                                                        {t("alleyDifficultyLevel." + row.difficultyLevelName)}
                                                    </TableCell>
                                                    <TableCell style={{width: "150px"}}>
                                                        <Button name="showAlleyDetailsButton"
                                                                style={{background: "pink"}}
                                                                onClick={() => {
                                                                    this.props.history.push(urls.alleyDetails.split(":")[0] + row.name)
                                                                }}>
                                                            {t("showAllAlleys.details")}
                                                        </Button>
                                                    </TableCell>
                                                    <TableCell style={{width: "150px"}}>
                                                        <Button name="deleteAlleyButton"
                                                                style={{background: "pink"}}
                                                                onClick={() => this.handleRemoveAlley(row.name)}>
                                                            {t("showAllAlleys.delete")}
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
                                   title={t("showAllAlleys.confirmDeleteDialogTitle")}
                                   content={t("showAllAlleys.confirmDeleteDialogContent")}
                                   handleDialogResponse={this.handleDialogResponse}/>
                </Card>
            )
        }
    }
}

export default withTranslation()(withRouter(ShowEmployeeAllAlleysComponent));