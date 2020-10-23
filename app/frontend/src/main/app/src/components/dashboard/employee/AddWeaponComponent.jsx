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
import ConfirmDialog from "../templates/ConfirmDialog";
import '../../../resources/styles/CreateNewAccountPage.scss'
import CircularProgress from "@material-ui/core/CircularProgress";
import InputMask from "react-input-mask";
import Grid from "@material-ui/core/Grid";

class AddWeaponComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            serialNumber: "",
            weaponModelName: "",
            weaponModelNames: [],
            openDialog: false,
            loading: false,
            disableWeaponNameSelect: false
        }
    }

    componentDidMount() {
        this.getWeaponModelNames();
    }

    sendRequest = () => {
        const {t} = this.props;
        const body = {
            "serialNumber": this.state.serialNumber.toUpperCase(),
            "weaponModelName": this.state.weaponModelName
        };
        postFetch(
            '/app/weapon/create', body)
            .then((res) => {
                    if (res.ok) {
                        this.props.displaySnackbar(SnackBarType.success, t("addNewWeapon.success"));
                    } else if (res.status === 400) {
                        res.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                    }
                }
            ).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        }).finally(() => {
            this.setState({
                loading: false,
                serialNumber: "",
                weaponModelName: ""});
            this.getWeaponModelNames();
        });
    };

    getWeaponModelNames = () => {
        const {t} = this.props;
        getFetch("/app/weapon-model/get-active-weapon-models")
            .then(response => {
                if (response.ok) {
                    return response;
                } else {
                    response.text().then(
                        (data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                const names = data.map(item => {
                    return {
                        value: item.name,
                        content: item.name
                    }
                });
                if(names.length === 0){
                    this.props.displaySnackbar(SnackBarType.error, t("addNewWeapon.emptyWeaponModelListError"));
                    this.setState({
                        disableWeaponNameSelect: true
                    });
                } else {
                    this.setState({
                        weaponModelNames: names
                    });
                }
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("account.ERROR"));
        });
    };


    handleSubmit = (event) => {
        if (!this.validate()) {
            this.handleOpenDialog();
        }
        event.preventDefault();
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
        });
    };


    validate = () => {
        return this.state.serialNumber.trim() === ""
            || this.state.serialNumber.includes("_")
            || this.state.weaponModelName.trim() === "";
    };

    isButtonDisabled = () => {
        return this.validate()
            || this.state.loading;
    };
    isWeaponNameSelectDisabled = () => {
        return this.state.disableWeaponNameSelect
    };

    render() {
        const {t} = this.props;
        document.title = t("breadcrumbs.addWeapon");
        return (
            <Card>
                <CardHeader
                    name={"addNewWeapon"}
                    title={t("addNewWeapon.header")}
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
                              md={5}>
                            <form onSubmit={this.handleSubmit}>
                                <InputMask
                                    onChange={this.handleChange}
                                    mask="**-*-*****-**-**-********"
                                    value={this.state.serialNumber}
                                    disabled={false}
                                    maskChar="_"
                                >
                                    {() => <TextField
                                        inputProps={{style: {textTransform: 'uppercase'}}}
                                        name="serialNumber"
                                        variant="outlined"
                                        margin="normal"
                                        fullWidth
                                        required
                                        label={t("addNewWeapon.serialNumber")}/>}
                                </InputMask>
                                <SelectFieldComponent
                                    labelId="select-weapon-model-name"
                                    label={t("addNewWeapon.weaponModelName")}
                                    value={this.state.weaponModelName}
                                    onChange={this.handleChange}
                                    name="weaponModelName"
                                    items={this.state.weaponModelNames}
                                    disabled={this.isWeaponNameSelectDisabled()}/>
                                <Button
                                    name="submitButton"
                                    type="submit"
                                    fullWidth
                                    variant="contained"
                                    color="primary"
                                    style={{minHeight: 60}}
                                    className="submit-button"
                                    disabled={this.isButtonDisabled()}>
                                    {this.state.loading ? <CircularProgress/> : t("addNewWeapon.addButton")}
                                </Button>
                            </form>

                            <ConfirmDialog open={this.state.openDialog}
                                           title={t("addNewWeapon.confirmDialogTitle")}
                                           content={t("addNewWeapon.confirmDialogContent")}
                                           handleDialogResponse={this.handleDialogResponse}/>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>
        );
    }
}

export default withTranslation()(withRouter(AddWeaponComponent));
