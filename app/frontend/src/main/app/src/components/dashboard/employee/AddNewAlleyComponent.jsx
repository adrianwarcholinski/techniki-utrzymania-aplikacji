import React, {Component} from "react";

import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import CircularProgress from "@material-ui/core/CircularProgress";
import {withTranslation} from "react-i18next";
import ConfirmDialog from "../templates/ConfirmDialog";
import {Card} from "@material-ui/core";
import CardContent from "@material-ui/core/CardContent";
import CardHeader from "@material-ui/core/CardHeader";
import TextFieldComponent from "../templates/TextFieldComponent";

import '../../../resources/styles/AddNewAlley.scss'
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import {getFetch, postFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import {validateAlleyDescription, validateAlleyName} from "../../../utils/regexpUtils";

class AddNewAlleyComponent extends Component {

    constructor(props) {
        super(props);

        this.state = {
            alleyName: "",
            alleyDescription: "",
            alleyDifficultyLevels: "",
            selectedAlleyDifficultyLevel: "",
            loading: false,
            openDialog: false,
            response: null,
        };
    }

    componentDidMount() {
        this.loadData();
    }

    loadData = () => {
        const {t} = this.props;
        getFetch('/app/alley-difficulty-level/get-alley-difficulty-levels')
            .then((response) => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).then(response => response.json())
            .then(data => {
                this.setState({alleyDifficultyLevels: data});
            }).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        })
    };

    sendData = () => {
        this.setState({loading:true});
        const {t} = this.props;
        const data={
            "name": this.state.alleyName,
            "description": this.state.alleyDescription,
            "alleyDifficultyLevelName":this.state.selectedAlleyDifficultyLevel
        }
        postFetch('/app/alley/add-alley', data)
            .then((response) => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("addNewAlley.success"));
                } else if (response.status === 400) {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            }).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        }).finally(
            () => this.setState({loading:false})
        )
    }


    handleSubmit = (event) => {
        event.preventDefault();
        if (this.validateForm()) {
            this.handleOpenDialog();
        }
    }

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({loading: true});
            this.sendData();
        }
    }

    handleCloseDialog = () => {
        this.setState({
            openDialog: false
        })
    }

    handleOpenDialog = () => {
        this.setState({
            openDialog: true
        })
    }


    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    validateForm = () => {
        if (this.state.alleyName.trim() !== "" &&
            this.state.alleyDescription.trim() !== "" &&
            this.state.selectedAlleyDifficultyLevel.trim() !== "") {
            return validateAlleyName(this.state.alleyName) && this.state.alleyName.trim() !== ""
                && validateAlleyDescription(this.state.alleyDescription) && this.state.alleyDescription.trim() !== "";
        }
        return false;
    }


    render() {
        const {t} = this.props;
        document.title = t("addNewAlley.pageTitle");

        return (
            <Card>
                <CardHeader
                    title={t("addNewAlley.addHeader")}
                    className="card-header"
                />
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
                            <form onSubmit={this.handleSubmit}
                                  method="post">

                                <TextFieldComponent onChange={this.handleChange}
                                                    label={t("addNewAlley.AlleyNameField")}
                                                    value={this.state.alleyName}
                                                    name="alleyName"
                                                    error={!validateAlleyName(this.state.alleyName)}
                                                    inputProps={{
                                                        maxLength: 50
                                                    }}/>

                                <TextFieldComponent onChange={this.handleChange}
                                                    value={this.state.alleyDescription}
                                                    label={t("addNewAlley.alleyDescriptionField")}
                                                    name="alleyDescription"
                                                    multiline
                                                    error={!validateAlleyDescription(this.state.alleyDescription)}
                                                    inputProps={{
                                                        maxLength: 400
                                                    }}/>

                                <FormControl variant="outlined" fullWidth required className="select">
                                    <InputLabel>{t("addNewAlley.alleyDifficultyLevelNameField")}</InputLabel>
                                    <Select
                                        name="selectedAlleyDifficultyLevel"
                                        value={this.state.selectedAlleyDifficultyLevel}
                                        onChange={this.handleChange}
                                        label={t("addNewAlley.alleyDifficultyLevelNameField")}
                                    >
                                        {this.state.alleyDifficultyLevels ?
                                            this.state.alleyDifficultyLevels.map(levelName => <MenuItem
                                                value={levelName}>{t("alleyDifficultyLevel." + levelName)}</MenuItem>)
                                            : null}
                                    </Select>
                                </FormControl>
                                <Button
                                    name="submitButton"
                                    type="submit"
                                    fullWidth
                                    variant="contained"
                                    color="primary"
                                    className="submit-button"
                                    disabled={!this.validateForm()}>
                                    {this.state.loading ? <CircularProgress className="circularProgress"/> : t("addNewAlley.addButton")}
                                </Button>
                            </form>

                            <ConfirmDialog open={this.state.openDialog}
                                           title={t("addNewAlley.confirmDialogTitle")}
                                           content={t("addNewAlley.confirmDialogContent")}
                                           handleDialogResponse={this.handleDialogResponse}/>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>
        )
    }
}

export default withTranslation()(AddNewAlleyComponent);
