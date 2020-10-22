import * as React from "react";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import {withTranslation} from "react-i18next";
import {withRouter} from "react-router-dom";
import SelectFieldComponent from "../templates/SelectFieldComponent";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import {getFetch, postFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import {addZeroToDateElement} from "../../../utils/dateUtils";
import ReservationTimePickerComponent from "../templates/ReservationTimePickerComponent";
import ConfirmDialog from "../templates/ConfirmDialog";
import '../../../resources/styles/CreateNewAccountPage.scss'
import CircularProgress from "@material-ui/core/CircularProgress";
import i18n from "../../../i18n";
import LinearProgress from "@material-ui/core/LinearProgress";

class MakeReservationComponent extends React.Component {
    constructor(props) {
        super(props);
        const {t} = this.props;
        this.state = {
            alleyName: "",
            weaponModelName: "",
            alleyNames: [],
            weaponModelNames: [],
            reservationDate: this.formatDate(new Date()),
            startDate: "",
            endDate: "",
            openDialog: false,
            loading: false,
            conflictLoading: true,
            conflictReservations: null,
            dateError: false,
            disabled: false,
            weaponLabel: t("common.loading"),
            alleyLabel: t("common.loading"),
        }
    }

    componentDidMount() {
        this.getWeaponModelNames();
        this.getAlleyNames();
    }

    refresh = () => {
        this.getWeaponModelNames();
        this.getAlleyNames();
        this.getConflictReservationsIfPossible();
    };

    sendRequest = () => {
        const {t} = this.props;
        const body = {
            "startDate": this.state.startDate,
            "endDate": this.state.endDate,
            "weaponModelName": this.state.weaponModelName,
            "alleyName": this.state.alleyName
        };

        const header = {
            headers: {
                "language": i18n.language,
                "Content-Type": "application/json; charset=utf-8"
            }
        };

        postFetch(
            '/app/reservation/make-reservation', body, header)
            .then((res) => {
                    if (res.ok) {
                        this.props.displaySnackbar(SnackBarType.success, t("makeReservation.SUCCESS"));
                    } else {
                        res.text().then((data) => {
                            if (data === "error.internalProblem") {
                                this.props.displaySnackbar(SnackBarType.error, t("error.reservationNumberIsAlreadyTaken"))

                            } else {
                                this.props.displaySnackbar(SnackBarType.error, t(data))
                            }
                        });
                    }
                }
            ).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("makeReservation.ERROR"));
        }).finally(() => {
            this.setState({
                loading: false,
                conflictLoading: true,
                weaponLabel: t("common.loading"),
                alleyLabel: t("common.loading"),
                alleyName: "",
                weaponModelName: "",
            }, () => this.refresh());
        });
    };

    getConflictReservations = () => {
        const {t} = this.props;

        getFetch(`/app/reservation/get-conflict-reservations-by-weapon-model?alleyName=${this.state.alleyName}&date=${this.state.reservationDate}T00:00&weaponModelName=${this.state.weaponModelName}`)
            .then(response => {
                if (response.ok) {
                    return response;
                } else {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                this.setState({
                    conflictReservations: data,
                    conflictLoading: false
                });
            }).catch();
    };

    getAlleyNames = () => {
        const {t} = this.props;
        getFetch(`/app/alley/get-active-alleys`)
            .then(response => {
                if (response.ok) {
                    return response;
                } else {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                if (data.length !== 0) {
                    const names = data.map(item => {
                        return {
                            value: item.name,
                            content: item.name
                        }
                    });
                    this.setState({
                        alleyNames: names,
                        alleyName: names[0].value,
                        alleyLabel: t("makeReservation.alleyName")
                    }, () => this.getConflictReservationsIfPossible());
                } else {
                    this.props.displaySnackbar(SnackBarType.info, t("makeReservation.lackOfResourcesInformation"));
                    this.setState({
                        disabled: true,
                        conflictLoading: false,
                        alleyLabel: t("makeReservation.lackOfResources")
                    });
                }
            }).catch((error) => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        });
    };

    getWeaponModelNames = () => {
        const {t} = this.props;
        getFetch("/app/weapon-model/get-active-weapon-models-with-active-weapons")
            .then(response => {
                if (response.ok) {
                    return response;
                } else {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                if (data.length !== 0) {
                    const names = data.map(item => {
                        return {
                            value: item.name,
                            content: item.name
                        }
                    });
                    this.setState({
                        weaponModelNames: names,
                        weaponModelName: names[0].value,
                        weaponLabel: t("makeReservation.weaponModelName")
                    }, () => this.getConflictReservationsIfPossible());
                } else {
                    this.props.displaySnackbar(SnackBarType.info, t("makeReservation.lackOfResourcesInformation"));
                    this.setState({
                        disabled: true,
                        conflictLoading: false,
                        weaponLabel: t("makeReservation.lackOfResources")
                    });
                }
            }).catch();
    };

    formatDate = (date) => {
        const year = date.getFullYear();
        const month = addZeroToDateElement(date.getMonth() + 1);
        const day = addZeroToDateElement(date.getDate());
        return `${year}-${month}-${day}`;
    };


    handleSubmit = (event) => {
        event.preventDefault();
        if (!this.validate()) {
            this.handleOpenDialog();
        }
    };

    handleOpenDialog = () => {
        this.setState({
            openDialog: true
        })
    };

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({loading: true});
            this.sendRequest();
        }
    };

    handleCloseDialog = () => {
        this.setState({
            openDialog: false
        })
    };

    handleDateError = (dateError) => {
        this.setState({
            dateError: dateError
        });
    };


    getConflictReservationsIfPossible = () => {
        if (this.state.alleyName !== "" && this.state.weaponModelName !== "" && this.state.reservationDate !== "") {
            this.getConflictReservations();
        } else {
            this.setState({
                conflictReservations: null
            })
        }
    };

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value,
            conflictLoading: true
        }, () => this.getConflictReservationsIfPossible());
    };

    handleChangeReservationDate = (event) => {
        this.setState({
            reservationDate: event.target.value,
            startDFate: "",
            endDate: "",
            conflictLoading: true
        }, () => this.getConflictReservationsIfPossible());
    };


    handleChangeDates = (startDate, endDate) => {
        this.setState({
            startDate: startDate,
            endDate: endDate
        });

    };


    validate = () => {
        return this.state.alleyName.trim() === ""
            || this.state.weaponModelName.trim() === ""
            || this.state.startDate.trim() === ""
            || this.state.dateError === true
            || this.state.endDate.trim() === ""
            || this.state.weaponModelNames.length === 0
            || this.state.alleyNames.length === 0
    };

    isButtonDisabled = () => {
        return this.validate()
            || this.state.loading;
    };

    render() {
        const {t} = this.props;
        return (
            <Card>
                <CardHeader
                    name={"makeReservationCardHeader"}
                    title={t("makeReservation.makeReservationHeader")}
                    className="card-header"/>
                <CardContent>
                    <form onSubmit={this.handleSubmit}
                          method="post">
                        <SelectFieldComponent
                            labelId="select-alley-name"
                            label={this.state.alleyLabel}
                            value={this.state.alleyName}
                            disabled={this.state.alleyNames.length === 0 || this.state.disabled}
                            onChange={this.handleChange}
                            name="alleyName"
                            items={this.state.alleyNames}/>
                        <SelectFieldComponent
                            labelId="select-weapon-model-name"
                            label={this.state.weaponLabel}
                            value={this.state.weaponModelName}
                            disabled={this.state.weaponModelNames.length === 0 || this.state.disabled}
                            onChange={this.handleChange}
                            name="weaponModelName"
                            items={this.state.weaponModelNames}/>
                        <TextField
                            id="date"
                            label={t("makeReservation.reservationDate")}
                            variant="outlined"
                            fullWidth
                            type="date"
                            required
                            margin="normal"
                            disabled={this.state.disabled}
                            value={this.state.reservationDate}
                            onChange={this.handleChangeReservationDate}
                            name="reservationDate"
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                        {this.state.conflictLoading && this.state.reservationDate !== "" ? <LinearProgress/> :
                            this.state.conflictReservations === null ?
                                <p>{t("makeReservation.selectDataInfo")}</p> :
                                <ReservationTimePickerComponent
                                    alleyName={this.state.alleyName}
                                    displaySnackbar={this.props.displaySnackbar}
                                    t={this.props.t}
                                    onChange={this.handleChangeDates}
                                    conflictReservations={this.state.conflictReservations}
                                    date={this.state.reservationDate}
                                    handleDateError={this.handleDateError}
                                    err={this.state.dateError}
                                />}
                        <Button
                            name="submitButton"
                            type="submit"
                            fullWidth
                            variant="contained"
                            color="primary"
                            style={{minHeight: 60}}
                            className="submit-button"
                            disabled={this.isButtonDisabled()}>
                            {this.state.loading ? <CircularProgress/> : t("addNewAccount.addButton")}
                        </Button>
                    </form>

                    <ConfirmDialog open={this.state.openDialog}
                                   title={t("makeReservation.confirmDialogTitle")}
                                   content={t("makeReservation.confirmDialogContent")}
                                   handleDialogResponse={this.handleDialogResponse}/>
                </CardContent>
            </Card>
        );
    }
}

export default withTranslation()(withRouter(MakeReservationComponent));
