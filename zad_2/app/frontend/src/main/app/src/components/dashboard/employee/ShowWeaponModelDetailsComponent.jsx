import React, {Component} from 'react';
import {withTranslation} from 'react-i18next';
import {withRouter} from "react-router-dom";
import {Card} from "@material-ui/core";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import Grid from "@material-ui/core/Grid";
import TextFieldComponent from "../templates/TextFieldComponent";
import {
    validateCaliber,
    validateMagazineCapacity,
    validateWeaponModelDescription,
    validateWeaponModelName
} from "../../../utils/regexpUtils";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress";
import ConfirmDialog from "../templates/ConfirmDialog";
import {getFetch, putFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import LinearProgress from "@material-ui/core/LinearProgress";
import Rating from "@material-ui/lab/Rating";
import ShowOpinionsComponent from "../common/ShowOpinionsComponent";
import {urls} from "../../../const/Urls";


class ShowWeaponModelDetailsComponent extends Component {

    constructor(props) {
        super(props);

        this.state = {
            name: "",
            description: "",
            caliberMm: "",
            magazineCapacity: "",
            selectedWeaponCategory: "",
            numberOfOpinions: 0,
            numberOfWeapons: 0,
            id: "",
            version: "",
            averageRate: undefined,
            weaponCategories: [],
            loading: false,
            loadingData: false,
            loadingCategory: false,
            openDialog: false,
            edit: false,
            opinions: [],
        };
    }

    componentDidMount() {
        this.loadReadData();
        this.getOpinions();
    }

    loadWeaponModel = () => {
        const {t} = this.props;
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
                    selectedWeaponCategory: data.weaponCategory,
                    numberOfOpinions: data.numberOfOpinions,
                    numberOfWeapons: data.numberOfWeapons,
                    averageRate: data.averageRate,
                    id: data.id,
                    version: data.version
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

    loadReadData = () => {
        const {t} = this.props;
        this.setState({
            loadingData: true,
            edit: false
        });

        this.loadWeaponModel();
    };

    loadEditData = () => {
        const {t} = this.props;
        this.setState({
            loadingData: true,
            loadingCategory: true,
        });

        getFetch('/app/weapon-category/get-all-weapon-categories')
            .then((response) => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                    this.loadReadData();
                }
            })
            .then(response => response.json())
            .then(data => {
                this.setState({
                    weaponCategories: data
                });
            })
            .catch((e) => {
                this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
            })
            .finally(() => {
                this.setState({
                    loadingCategory: false
                });
            });

        this.loadWeaponModel();
    };

    sendData = () => {
        this.setState({loading: true});
        const {t} = this.props;
        const data = {
            "name": this.state.name,
            "description": this.state.description,
            "caliberMm": this.state.caliberMm,
            "magazineCapacity": this.state.magazineCapacity,
            "weaponCategory": this.state.selectedWeaponCategory,
            "id": this.state.id,
            "version": this.state.version
        };

        putFetch('/app/weapon-model/edit-weapon-model', data)
            .then((response) => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("editWeaponModel.success"));
                } else if (response.status === 400) {
                    response.text().then((data) => {
                        this.props.displaySnackbar(SnackBarType.error, t(data));
                        if (data === "error.weaponModelDoesNotExist") {
                            this.props.history.push(urls.allWeaponModels);
                        }
                    });
                }
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        }).finally(
            () => {
                this.setState({
                    loading: false
                });
                this.loadReadData();
                this.getOpinions();
            }
        )
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

    handleSubmit = (event) => {
        event.preventDefault();
        if (this.validateInputs()) {
            this.handleOpenDialog();
        }
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

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    handleChangeCategory = (event) => {
        this.setState({
            selectedWeaponCategory: event.target.value
        });
    };

    handleEdit = () => {
        this.loadEditData();
        this.setState({
            edit: true
        });
    };

    validateNineCharsPhoneNumberLimit = (event) => {
        if (!this.validateOnlyDigits(event)) {
            const lastCharIndex = event.target.value.length - 1;
            event.target.value = event.target.value.slice(0, lastCharIndex);
        }
    };

    validateOnlyDigitsMagazineCapacity = (event) => {
        if (!/^\d+$/i.test(event.target.value)) {
            const lastCharIndex = event.target.value.length - 1;
            event.target.value = event.target.value.slice(0, lastCharIndex);
        }
    };

    validateOnlyDigitsCaliber = (event) => {
        if (!/^[0-9]+\.?[0-9]*$/i.test(event.target.value)) {
            const lastCharIndex = event.target.value.length - 1;
            event.target.value = event.target.value.slice(0, lastCharIndex);
        }
    };

    validateInputs = () => {
        return !(!validateWeaponModelName(this.state.name) || this.state.name.trim() === ""
            || !validateWeaponModelDescription(this.state.description) || this.state.description.trim() === ""
            || !validateCaliber(this.state.caliberMm) || this.state.caliberMm === ""
            || !validateMagazineCapacity(this.state.magazineCapacity) || this.state.magazineCapacity === ""
            || this.state.selectedWeaponCategory.trim() === "");
    };

    isButtonDisabled = () => {
        if (!this.validateInputs()) {
            return true;
        }
        return this.state.loading;
    };

    render() {
        const {t} = this.props;
        document.title = t("editWeaponModel.pageTitle");
        if (this.state.loadingData === false && this.state.loadingCategory === false) {
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
                                                        value={this.state.name}
                                                        name="name"
                                                        required={false}
                                                        disabled/>

                                    <TextFieldComponent onChange={this.handleChange}
                                                        label={t("editWeaponModel.description")}
                                                        name="description"
                                                        error={!validateWeaponModelDescription(this.state.description)}
                                                        value={this.state.description}
                                                        required={this.state.edit}
                                                        multiline
                                                        rows={5}
                                                        inputProps={{
                                                            maxLength: 400
                                                        }}
                                                        disabled={!this.state.edit}/>

                                    <TextFieldComponent onChange={this.handleChange}
                                                        onInput={this.validateOnlyDigitsCaliber}
                                                        label={t("editWeaponModel.caliberMm")}
                                                        name="caliberMm"
                                                        error={!validateCaliber(this.state.caliberMm)}
                                                        value={this.state.caliberMm}
                                                        required={this.state.edit}
                                                        inputProps={{
                                                            maxLength: 6
                                                        }}
                                                        disabled={!this.state.edit}/>

                                    <TextFieldComponent onChange={this.handleChange}
                                                        onInput={this.validateOnlyDigitsMagazineCapacity}
                                                        label={t("editWeaponModel.magazineCapacity")}
                                                        name="magazineCapacity"
                                                        error={!validateMagazineCapacity(this.state.magazineCapacity)}
                                                        value={this.state.magazineCapacity}
                                                        required={this.state.edit}
                                                        inputProps={{
                                                            maxLength: 4
                                                        }}
                                                        disabled={!this.state.edit}/>

                                    {this.state.edit === false ?
                                        <TextFieldComponent label={t("editWeaponModel.weaponCategory")}
                                                            name="weaponCategory"
                                                            value={t(`editWeaponModel.${this.state.selectedWeaponCategory}`)}
                                                            disabled={true}
                                                            required={false}/>
                                        :
                                        <FormControl fullWidth
                                                     variant="outlined"
                                                     margin="normal"
                                                     required
                                                     disabled={!this.state.edit}>
                                            <InputLabel
                                                id="weaponCategory">{t("editWeaponModel.weaponCategory")}</InputLabel>
                                            <Select
                                                labelId="weaponCategory"
                                                label={t("editWeaponModel.weaponCategory")}
                                                value={this.state.selectedWeaponCategory}
                                                onChange={this.handleChangeCategory}>
                                                {this.state.weaponCategories.map(weaponCategory => {
                                                    return <MenuItem value={weaponCategory.name}>
                                                        {t(`editWeaponModel.${weaponCategory.name}`)}
                                                    </MenuItem>
                                                })}
                                            </Select>
                                        </FormControl>
                                    }

                                    {this.state.edit === false &&
                                    <div>
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
                                    </div>}

                                    {this.state.edit === false ?
                                        <Button
                                            name="editButton"
                                            fullWidth
                                            variant="contained"
                                            color="primary"
                                            className="submit-button"
                                            onClick={this.handleEdit}>
                                            {t("editWeaponModel.enableEditButton")}
                                        </Button>
                                        :
                                        <Grid container>
                                            <Grid item xs={6}
                                                  style={{
                                                      paddingLeft: 10,
                                                      paddingRight: 10
                                                  }}>
                                                <Button onClick={this.loadReadData}
                                                        name="backButton"
                                                        fullWidth
                                                        variant="contained"
                                                        color="secondary"
                                                        className="submit-button">
                                                    {t("editWeaponModel.backButton")}
                                                </Button>
                                            </Grid>
                                            <Grid item xs={6}
                                                  style={{
                                                      paddingLeft: 10,
                                                      paddingRight: 10
                                                  }}>
                                                <Button
                                                    type="submit"
                                                    name="submitButton"
                                                    fullWidth
                                                    variant="contained"
                                                    color="primary"
                                                    className="submit-button"
                                                    disabled={this.isButtonDisabled()}>
                                                    {this.state.loading ?
                                                        <CircularProgress/> : t("editWeaponModel.editButton")}
                                                </Button>
                                            </Grid>
                                        </Grid>
                                    }
                                </form>

                                <ConfirmDialog open={this.state.openDialog}
                                               title={t("editWeaponModel.confirmDialogTitle")}
                                               content={t("editWeaponModel.confirmDialogContent")}
                                               handleDialogResponse={this.handleDialogResponse}/>
                            </Grid>
                            <Grid item xs={5}>
                                <ShowOpinionsComponent
                                    weaponModelName={this.props.match.params.name}
                                    opinions={this.state.opinions}
                                    infoClass={"info-employee"}
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
