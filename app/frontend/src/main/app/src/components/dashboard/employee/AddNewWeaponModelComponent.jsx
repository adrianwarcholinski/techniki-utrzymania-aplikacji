import React, {Component} from "react";
import {getFetch, postFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import {
    validateCaliber,
    validateMagazineCapacity,
    validateWeaponModelDescription,
    validateWeaponModelName
} from "../../../utils/regexpUtils";
import {Card} from "@material-ui/core";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import Grid from "@material-ui/core/Grid";
import ConfirmDialog from "../templates/ConfirmDialog";
import {withTranslation} from "react-i18next";
import TextFieldComponent from "../templates/TextFieldComponent";
import MenuItem from "@material-ui/core/MenuItem";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import CircularProgress from "@material-ui/core/CircularProgress";
import Button from "@material-ui/core/Button";
import LinearProgress from "@material-ui/core/LinearProgress";

class AddNewWeaponModelComponent extends Component {

    constructor(props) {
        super(props);

        this.state = {
            name: "",
            description: "",
            caliberMm: "",
            magazineCapacity: "",
            selectedWeaponCategory: "",
            weaponCategories: [],
            loading: false,
            loadingData: false,
            openDialog: false,
        };
    }

    componentDidMount() {
        this.loadData();
    }

    loadData = () => {
        const {t} = this.props;
        this.setState({
            loadingData: true
        });
        getFetch('/app/weapon-category/get-all-weapon-categories')
            .then((response) => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                this.setState({
                    weaponCategories: data
                });
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        }).finally(() =>
            this.setState({
                loadingData: false
            })
        );
    };

    sendData = () => {
        this.setState({loading: true});
        const {t} = this.props;
        const data = {
            "name": this.state.name,
            "description": this.state.description,
            "caliberMm": this.state.caliberMm,
            "magazineCapacity": this.state.magazineCapacity,
            "weaponCategory": this.state.selectedWeaponCategory
        };

        postFetch('/app/weapon-model/add-weapon-model', data)
            .then((response) => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("addNewWeaponModel.success"));
                } else if (response.status === 400) {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        }).finally(
            () => this.setState({loading: false})
        )
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
            || !validateCaliber(this.state.caliberMm) || this.state.caliberMm.trim() === ""
            || !validateMagazineCapacity(this.state.magazineCapacity) || this.state.magazineCapacity.trim() === ""
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
        document.title = t("addNewWeaponModel.pageTitle");

        if (this.state.loadingData === false) {
            return (
                <Card>
                    <CardHeader
                        title={t("addNewWeaponModel.addHeader")}
                        className="card-header"/>
                    <CardContent>
                        <Grid container
                              direction="column"
                              justify="center"
                              alignItems="center">
                            <Grid container
                                  item
                                  justify="center"
                                  xs={12}
                                  sm={7}
                                  md={5}
                                  lg={4}
                                  xl={3}>
                                <form onSubmit={this.handleSubmit}>
                                    <TextFieldComponent onChange={this.handleChange}
                                                        label={t("addNewWeaponModel.name")}
                                                        name="name"
                                                        error={!validateWeaponModelName(this.state.name)}
                                                        inputProps={{
                                                            maxLength: 20
                                                        }}/>

                                    <TextFieldComponent onChange={this.handleChange}
                                                        label={t("addNewWeaponModel.description")}
                                                        name="description"
                                                        error={!validateWeaponModelDescription(this.state.description)}
                                                        multiline
                                                        rows={5}
                                                        inputProps={{
                                                            maxLength: 400
                                                        }}/>

                                    <TextFieldComponent onChange={this.handleChange}
                                                        onInput={this.validateOnlyDigitsCaliber}
                                                        label={t("addNewWeaponModel.caliberMm")}
                                                        name="caliberMm"
                                                        error={!validateCaliber(this.state.caliberMm)}
                                                        inputProps={{
                                                            maxLength: 6
                                                        }}/>

                                    <TextFieldComponent onChange={this.handleChange}
                                                        onInput={this.validateOnlyDigitsMagazineCapacity}
                                                        label={t("addNewWeaponModel.magazineCapacity")}
                                                        name="magazineCapacity"
                                                        error={!validateMagazineCapacity(this.state.magazineCapacity)}
                                                        inputProps={{
                                                            maxLength: 4
                                                        }}/>

                                    <FormControl fullWidth
                                                 variant="outlined"
                                                 margin="normal"
                                                 required>
                                        <InputLabel id="weaponCategory">{t("addNewWeaponModel.weaponCategory")}</InputLabel>
                                        <Select
                                            labelId="weaponCategory"
                                            label={t("addNewWeaponModel.weaponCategory")}
                                            value={this.state.selectedWeaponCategory}
                                            onChange={this.handleChangeCategory}>
                                            {this.state.weaponCategories.map(weaponCategory => {
                                                return <MenuItem value={weaponCategory.name}>
                                                    {t(`addNewWeaponModel.${weaponCategory.name}`)}
                                                </MenuItem>
                                            })}
                                        </Select>
                                    </FormControl>

                                    <Button
                                        name="submitButton"
                                        type="submit"
                                        fullWidth
                                        variant="contained"
                                        color="primary"
                                        className="submit-button"
                                        disabled={this.isButtonDisabled()}>
                                        {this.state.loading ? <CircularProgress/> : t("addNewAccount.addButton")}
                                    </Button>
                                </form>

                                <ConfirmDialog open={this.state.openDialog}
                                               title={t("addNewWeaponModel.confirmDialogTitle")}
                                               content={t("addNewWeaponModel.confirmDialogContent")}
                                               handleDialogResponse={this.handleDialogResponse}/>
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

export default withTranslation()(AddNewWeaponModelComponent);
