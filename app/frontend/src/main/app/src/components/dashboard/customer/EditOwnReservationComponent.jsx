import * as React from "react";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import {withTranslation} from "react-i18next";
import {withRouter} from "react-router-dom";
import SelectFieldComponent from "../templates/SelectFieldComponent";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import {getFetch, putFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import {addZeroToDateElement} from "../../../utils/dateUtils";
import ReservationTimePickerComponent from "../templates/ReservationTimePickerComponent";
import ConfirmDialog from "../templates/ConfirmDialog";
import '../../../resources/styles/CreateNewAccountPage.scss'
import CircularProgress from "@material-ui/core/CircularProgress";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import Grid from "@material-ui/core/Grid";
import i18n from "../../../i18n";

class EditOwnReservationComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            alleyName: "",
            alleyNames: [],
            weaponModelName: "",
            weaponModelNames: [],
            reservationDate: "",
            startDate: "",
            endDate: "",
            openDialog: false,
            loading: false,
            conflictReservations: null,
            reservationNumber: this.props.match.params.reservationNumber
        }
    }

    componentDidMount() {
        this.getReservationData();
        this.getWeaponModelNames();
        this.getAlleyNames();
    }

    getReservationData = () => {
        const {t} = this.props;
        getFetch('/app/reservation/get-own-reservation?reservationNumber=' + this.state.reservationNumber)
            .then((response) => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                this.setState({
                    alleyName: data.alley.name,
                    weaponModelName: data.weaponModelName,
                    startDate: data.startDate,
                    endDate: data.endDate,
                    reservationDate: this.formatDate(new Date(data.startDate)),
                    id: data.id,
                    version: data.version
                }, () => this.getConflictReservations());
            }).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        })
    };


    refresh = () => {
        this.getConflictReservations();
        this.getWeaponModelNames();
        this.getAlleyNames();
    };

    sendRequest = () => {
        const {t} = this.props;
        const body = {
            "startDate": this.state.startDate,
            "endDate": this.state.endDate,
            "weaponModelName": this.state.weaponModelName,
            "alleyName": this.state.alleyName,
            "version": this.state.version,
            "id": this.state.id,
        };
        const header = {
            headers: {
                "language": i18n.language,
                "Content-Type": "application/json; charset=utf-8"
            }
        };
        putFetch(
            '/app/reservation/update-own-reservation', body, header)
            .then((response) => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("editReservation.SUCCESS"));
                    this.props.handleDisplayEdit(false);
                } else if (response.status === 400) {
                    response.text().then((data) => {
                    if (data === "error.internalProblem") {
                        this.props.displaySnackbar(SnackBarType.error, t("editReservation.ERROR"))

                    } else {
                        this.props.displaySnackbar(SnackBarType.error, t(data))
                    }
                }
            );
    }
}

).
catch(() => {
    this.props.displaySnackbar(SnackBarType.error, t("editReservation.ERROR"));
}).finally(() => {
    this.setState({loading: false}, () => this.refresh());
});
}
;

getConflictReservations = () => {
    if (this.state.alleyName === "" || this.state.weaponModelName === "" || this.state.reservationDate === "") {
        return;
    }
    const {t} = this.props;
    getFetch("/app/reservation/get-conflict-reservations-by-weapon-model?date=" + this.state.reservationDate +
        "T00:00&alleyName=" + this.state.alleyName + "&weaponModelName=" + this.state.weaponModelName +
        "&excludedReservationNumber=" + this.state.reservationNumber)
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
                conflictReservations: data
            });
        }).catch(() => {
        this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
    });
};

getAlleyNames = () => {
    const {t} = this.props;
    getFetch(`/app/alley/get-active-alleys`)
        .then(response => {
            if (response.ok) {
                return response;
            } else if (response.status === 400) {
                response.text().then(
                    (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
            }
        }).then(response => response.json())
        .then(data => {
            if (data.length === 0) {
                this.props.displaySnackbar(SnackBarType.error, t("error.noAvailableAlleys"));
            }
            const names = data.map(item => {
                return {
                    value: item.name,
                    content: item.name + " | " + t("alleyDifficultyLevel." + item.difficultyLevelName)
                }
            });
            this.setState({
                alleyNames: names
            });
        }).catch(() => {
        this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
    });
};

getWeaponModelNames = () => {
    const {t} = this.props;
    getFetch("/app/weapon-model/get-active-weapon-models-with-active-weapons")
        .then(response => {
            if (response.ok) {
                return response;
            } else if (response.status === 400) {
                response.text().then(
                    (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
            }
        }).then(response => response.json())
        .then(data => {
            if (data.length === 0) {
                this.props.displaySnackbar(SnackBarType.error, t("error.noAvailableWeaponModels"));
            }
            const names = data.map(item => {
                return {
                    value: item.name,
                    content: item.name
                }
            });
            this.setState({
                weaponModelNames: names
            });
        }).catch(() => {
        this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
    });
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

handleChange = (event) => {
    this.setState({
        [event.target.name]: event.target.value
    }, () => this.getConflictReservations());
};

handleChangeDates = (startDate, endDate) => {
    this.setState({
        startDate: startDate,
        endDate: endDate
    });

};

handleDateError = (dateError) => {
    this.setState({
        dateError: dateError
    });

};

validate = () => {
    return this.state.alleyName.trim() === ""
        || this.state.weaponModelName.trim() === ""
        || this.state.dateError === true
        || this.state.reservationDate.trim() === ""
        || this.state.weaponModelNames.length === 0
        || this.state.alleyNames.length === 0
        || this.state.startDate.trim() === ""
        || this.state.endDate.trim() === ""
};

isButtonDisabled = () => {
    return this.validate()
        || this.state.loading;
};

render()
{
    const {t} = this.props;
    return (
        <Card>
            <CardHeader
                title={t("reservationDetails.header") + " " + this.props.match.params.reservationNumber}
                className="card-header"
            />
            <CardContent>
                <form onSubmit={this.handleSubmit}
                      method="post">
                    <SelectFieldComponent
                        labelId="select-alley-name"
                        label={t("reservationDetails.alleyNameAndDifficultyLevel")}
                        value={this.state.alleyName}
                        onChange={this.handleChange}
                        name="alleyName"
                        items={this.state.alleyNames}/>
                    <SelectFieldComponent
                        labelId="select-weapon-model-name"
                        label={t("reservationDetails.weaponModel")}
                        value={this.state.weaponModelName}
                        onChange={this.handleChange}
                        name="weaponModelName"
                        items={this.state.weaponModelNames}/>
                    <TextField
                        id="date"
                        label={t("makeReservation.reservationDate")}
                        variant="outlined"
                        fullWidth
                        type="date"
                        margin="normal"
                        value={this.state.reservationDate}
                        onChange={this.handleChange}
                        name="reservationDate"
                        InputLabelProps={{
                            shrink: true,
                        }}
                    />
                    {this.state.conflictReservations === null || this.state.reservationDate === "" ?
                        <p>{t("makeReservation.selectDataInfo")}</p> :
                        <ReservationTimePickerComponent
                            alleyName={this.state.alleyName}
                            displaySnackbar={this.props.displaySnackbar}
                            t={this.props.t}
                            onChange={this.handleChangeDates}
                            conflictReservations={this.state.conflictReservations}
                            date={this.state.reservationDate}
                            startDate={this.state.startDate}
                            endDate={this.state.endDate}
                            handleDateError={this.handleDateError}
                            err={this.state.dateError}
                        />
                    }
                    <Grid container justify="center" spacing={3}>
                        <Grid item
                              xs={12}
                              sm={9}
                              md={6}
                              lg={5}
                              xl={4}>
                            <ButtonGroup fullWidth variant="contained" color="primary" className="submit-button">
                                <Button
                                    name="cancelButton"
                                    type="button"
                                    onClick={() => this.props.handleDisplayEdit(false)}
                                    fullWidth
                                >
                                    {this.state.loading ? <CircularProgress/> : t("editReservation.cancel")}
                                </Button>
                                <Button
                                    name="submitButton"
                                    type="submit"
                                    fullWidth
                                    disabled={this.isButtonDisabled()}>
                                    {this.state.loading ? <CircularProgress/> : t("editReservation.change")}
                                </Button>
                            </ButtonGroup>
                        </Grid>
                    </Grid>

                </form>

                <ConfirmDialog open={this.state.openDialog}
                               title={t("editReservation.confirmDialogTitle")}
                               content={t("editReservation.confirmDialogContent")}
                               handleDialogResponse={this.handleDialogResponse}/>
            </CardContent>
        </Card>
    );
}
}

export default withTranslation()(withRouter(EditOwnReservationComponent));
