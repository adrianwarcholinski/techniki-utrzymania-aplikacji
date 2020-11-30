import React, {Component} from "react";

import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import {withTranslation} from "react-i18next";
import {Card} from "@material-ui/core";
import CardContent from "@material-ui/core/CardContent";
import CardHeader from "@material-ui/core/CardHeader";
import TextFieldComponent from "../templates/TextFieldComponent";

import '../../../resources/styles/ReservationDetails.scss'
import {getFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import EditOwnReservationComponent from "./EditOwnReservationComponent";

class ReservationDetailsComponent extends Component {

    constructor(props) {
        super(props);

        this.state = {
            reservation: null,
            status: null,
            disableEditButton: true,
            editEnabled: false
        };
    }

    componentDidMount() {
        this.loadData();
    }

    calculateState() {
        let startDate = new Date(this.state.reservation.startDate);
        let endDate = new Date(this.state.reservation.endDate);
        let currentDate = new Date();
        if (this.state.reservation.active === false) {
            this.setState({status: "reservationStatuses.canceled", disableEditButton: true});
        } else if (currentDate < startDate) {
            this.setState({status: "reservationStatuses.booked", disableEditButton: false});
        } else if (currentDate >= startDate && currentDate <= endDate) {
            this.setState({status: "reservationStatuses.inProgress", disableEditButton: true});
        } else if (currentDate > endDate) {
            this.setState({status: "reservationStatuses.finished", disableEditButton: true});
        }
    }

    loadData = () => {
        const {t} = this.props;
        getFetch('/app/reservation/get-own-reservation?reservationNumber=' + this.props.match.params.reservationNumber)
            .then((response) => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                this.setState({reservation: data}, () => this.calculateState());
            }).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        })
    };

    handleDisplayEdit = (display) => {
        this.setState({editEnabled: display})
        if(!display){
            this.loadData();
        }
    }

    render() {
        const {t} = this.props;
        document.title = t("reservationDetails.pageTitle");
        if (this.state.editEnabled) {
            return (<EditOwnReservationComponent handleDisplayEdit={this.handleDisplayEdit}
                                                 displaySnackbar={this.props.displaySnackbar} props={this.props}/>);
        } else {
            return (
                <Card>
                    <CardHeader
                        title={t("reservationDetails.header") + " " + this.props.match.params.reservationNumber}
                        className="card-header"
                    />
                    <CardContent>
                        <Grid container justify="center" spacing={3}>
                            <Grid item
                                  xs={12}
                                  sm={7}
                                  md={5}
                                  lg={4}
                                  xl={3}>
                                <TextFieldComponent onChange={this.handleChange}
                                                    label={t("reservationDetails.alleyNameAndDifficultyLevel")}
                                                    value={this.state.reservation ? this.state.reservation.alley.name + " | " +
                                                        t("alleyDifficultyLevel." + this.state.reservation.alley.difficultyLevelName) : t("common.loading")}
                                                    name="alleyNameAndDifficultyLevel"
                                                    disabled
                                />
                                <hr/>
                                <TextFieldComponent onChange={this.handleChange}
                                                    label={t("reservationDetails.weaponModel")}
                                                    value={this.state.reservation ? this.state.reservation.weaponModelName : t("common.loading")}
                                                    name="weaponModelAndSerialNumber"
                                                    disabled
                                />
                                <hr/>
                                <TextFieldComponent onChange={this.handleChange}
                                                    label={t("reservationDetails.startDate")}
                                                    value={this.state.reservation ? new Date(this.state.reservation.startDate).toLocaleString() : t("common.loading")}
                                                    name="startDate"
                                                    disabled
                                />
                                <TextFieldComponent onChange={this.handleChange}
                                                    label={t("reservationDetails.endDate")}
                                                    value={this.state.reservation ? new Date(this.state.reservation.endDate).toLocaleString() : t("common.loading")}
                                                    name="endDate"
                                                    disabled
                                />
                                <TextFieldComponent onChange={this.handleChange}
                                                    label={t("reservationDetails.status")}
                                                    value={this.state.status ? t(this.state.status) : t("common.loading")}
                                                    name="status"
                                                    disabled
                                />
                                <Button
                                    disabled={this.state.disableEditButton}
                                    onClick={() => this.handleDisplayEdit(true)}
                                    name="editButton"
                                    type="button"
                                    fullWidth
                                    variant="contained"
                                    color="primary"
                                    className="submit-button">
                                    {t("reservationDetails.editButton")}
                                </Button>
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>
            )
        }

    }
}

export default withTranslation()(ReservationDetailsComponent);
