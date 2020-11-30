import React, {Component} from 'react';
import {withTranslation} from 'react-i18next';
import {withRouter} from "react-router-dom";
import {Card} from "@material-ui/core";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import Grid from "@material-ui/core/Grid";
import TextFieldComponent from "../templates/TextFieldComponent";
import ConfirmDialog from "../templates/ConfirmDialog";
import {getFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import LinearProgress from "@material-ui/core/LinearProgress";
import Rating from "@material-ui/lab/Rating";
import ShowOpinionsComponent from "../common/ShowOpinionsComponent";
import AddEditOpinionComponent from "./AddEditOpinionComponent";
import {urls} from "../../../const/Urls";


class ShowWeaponModelDetailsComponent extends Component {

    constructor(props) {
        super(props);

        this.state = {
            name: "",
            description: "",
            caliberMm: "",
            magazineCapacity: "",
            numberOfOpinions: 0,
            numberOfWeapons: 0,
            averageRate: undefined,
            weaponCategories: [],
            loadingData: false,
            openDialog: false,
            opinions: [],
        };
    }

    componentDidMount() {
        this.loadData();
        this.getOpinions();
    }

    loadData = () => {
        const {t} = this.props;
        this.setState({
            loadingData: true
        });

        getFetch('/app/weapon-model/get-weapon-model?name=' + this.props.match.params.name)
            .then((response) => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    response.text().then((data) => {
                        this.props.displaySnackbar(SnackBarType.error, t(data));
                        if (data === "error.weaponModelDoesNotExist") {
                            this.props.history.push(urls.allWeaponModels);
                        }
                    });
                }
            })
            .then(response => response.json())
            .then(data => {
                this.setState({
                    name: data.name,
                    description: data.description,
                    caliberMm: data.caliberMm,
                    magazineCapacity: data.magazineCapacity,
                    weaponCategory: data.weaponCategory,
                    numberOfOpinions: data.numberOfOpinions,
                    numberOfWeapons: data.numberOfWeapons,
                    averageRate: data.averageRate
                });
            })
            .catch((e) => {
                this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
            })
            .finally(() => {
                this.setState({
                    loadingData: false
                });
            });
    };

    getOpinions = () => {
        const {t} = this.props;
        getFetch(`/app/opinion/get-opinions-for-weapon-model?weaponModelName=${this.props.match.params.name}`)
            .then(response => {
                if (response.ok) {
                    return response
                } else {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                this.setState({
                    opinions: data
                });
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        });
    };

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({loading: true});
            this.sendData();
        }
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

    refreshOpinions = () => {
        this.getOpinions();
        this.loadData();
    }

    render() {
        const {t} = this.props;
        document.title = t("editWeaponModel.pageTitle");
        if (this.state.loadingData === false) {
            return (
                <Card>
                    <CardHeader
                        title={t("editWeaponModel.detailsHeader")}
                        className="card-header"/>
                    <CardContent>
                        <Grid container
                              spacing={5}
                              style={{
                                  paddingLeft: 30,
                                  paddingRight: 30,
                                  paddingTop: 15,
                                  paddingBottom: 5
                              }}>
                            <Grid item xs={7}>
                                <form onSubmit={this.handleSubmit}>
                                    <TextFieldComponent label={t("editWeaponModel.name")}
                                                        name="name"
                                                        value={this.state.name}
                                                        disabled={true}
                                                        required={false}/>

                                    <TextFieldComponent label={t("editWeaponModel.description")}
                                                        name="description"
                                                        value={this.state.description}
                                                        multiline
                                                        rows={5}
                                                        disabled={true}
                                                        required={false}/>

                                    <TextFieldComponent label={t("editWeaponModel.caliberMm")}
                                                        name="caliberMm"
                                                        value={this.state.caliberMm}
                                                        disabled={true}
                                                        required={false}/>

                                    <TextFieldComponent label={t("editWeaponModel.magazineCapacity")}
                                                        name="magazineCapacity"
                                                        value={this.state.magazineCapacity}
                                                        disabled={true}
                                                        required={false}/>

                                    <TextFieldComponent label={t("editWeaponModel.weaponCategory")}
                                                        name="weaponCategory"
                                                        value={t(`editWeaponModel.${this.state.weaponCategory}`)}
                                                        disabled={true}
                                                        required={false}/>

                                    <TextFieldComponent label={t("editWeaponModel.numberOfOpinions")}
                                                        name="numberOfOpinions"
                                                        value={this.state.numberOfOpinions}
                                                        disabled={true}
                                                        required={false}/>

                                    <TextFieldComponent label={t("editWeaponModel.numberOfWeapons")}
                                                        name="numberOfWeapons"
                                                        value={this.state.numberOfWeapons}
                                                        disabled={true}
                                                        required={false}/>

                                    {this.state.averageRate !== undefined &&
                                    <Grid container
                                          justify="center">

                                        <Rating size="large"
                                                precision={0.5}
                                                value={this.state.averageRate}
                                                style={{padding: 10}}
                                                readOnly/>
                                    </Grid>}
                                </form>

                                <ConfirmDialog open={this.state.openDialog}
                                               title={t("editWeaponModel.confirmDialogTitle")}
                                               content={t("editWeaponModel.confirmDialogContent")}
                                               handleDialogResponse={this.handleDialogResponse}/>
                            </Grid>
                            <Grid item xs={5}>
                                <AddEditOpinionComponent {...this.props} name={this.props.match.params.name}
                                                         refreshOpinions={this.refreshOpinions}/>
                                <ShowOpinionsComponent
                                    weaponModelName={this.props.match.params.name}
                                    opinions={this.state.opinions}
                                    infoClass={"info-customer"}
                                    t={this.props.t}/>
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>
            );
        } else {
            return (<LinearProgress/>);
        }
    }
}

export default withTranslation()(withRouter(ShowWeaponModelDetailsComponent));
