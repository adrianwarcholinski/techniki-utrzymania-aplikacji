import React, {Component} from "react";
import {withTranslation} from "react-i18next";
import {withRouter} from "react-router-dom";
import {getFetch, putFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import {Card} from "@material-ui/core";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import Grid from "@material-ui/core/Grid";
import {validateDescription,} from "../../../utils/regexpUtils";
import Button from "@material-ui/core/Button";
import ConfirmDialog from "../templates/ConfirmDialog";
import LinearProgress from "@material-ui/core/LinearProgress";
import MenuItem from "@material-ui/core/MenuItem";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import FormControl from "@material-ui/core/FormControl";
import TextField from "@material-ui/core/TextField";
import {urls} from "../../../const/Urls";


class EmployeeAlleyDetailsComponent extends Component {
    constructor(props) {
        super(props);
        const {t} = this.props;
        document.title = t("alley.details");

        this.state = {
            openDialog: false,
            loading: false,
            name: this.props.match.params.name,
            details: true,
            difficultyLevels: []
        };
    }

    componentDidMount() {
        this.sendGetRequest();
    }


    sendGetRequest = () => {
        let uri = '/app/alley/details?name=' + this.state.name;

        const {t} = this.props;
        const newState = {
            loading: true
        };
        this.setState(newState);
        getFetch(uri)
            .then(response => {
                if (response.ok) {
                    return response;
                } else if(response.status===400) {
                    response.text().then(
                        (data) => {
                            if(data === "error.alleyDoesNotExist" || data === "error.alleyDifficultyLevelDoesNotExist"){
                                this.props.history.push(urls.allAlleys)
                            }
                            this.props.displaySnackbar(SnackBarType.error, t(data));
                        });
                }

            })
            .then(response => response.json())
            .then(data => {
                const newState = {
                    id: data.id,
                    version: data.version,
                    name: data.name,
                    description: data.description,
                    difficultyLevel: data.difficultyLevel,
                    difficultyLevels: data.difficultyLevels,
                    loading: false
                };

                this.setState(newState);
            }).catch((error) => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        })
            .finally(() => {
            });
    };


    sendPutRequest = () => {
        const {t} = this.props;
        const body = {
            "id": this.state.id,
            "version": this.state.version,
            "name": this.state.name,
            "description": this.state.description,
            "difficultyLevel": this.state.difficultyLevel,
            "difficultyLevels": this.state.difficultyLevels
        };

        const header = {
            headers: {
                "Content-Type": "application/json; charset=utf-8"
            }
        };

        putFetch('/app/alley/edit', body, header)
            .then((response) => {
                if (response.ok) {
                    this.props.displaySnackbar(SnackBarType.success, t("alley.editionSuccess"));
                } else if(response.status===400) {
                    response.text().then(
                        (data) => {
                            this.props.displaySnackbar(SnackBarType.error, t(data));
                            if(data === "error.alleyDoesNotExist" || data === "error.alleyDifficultyLevelDoesNotExist"){
                                this.props.history.push(urls.allAlleys)
                            };
                        }
                    );
                }
            }).catch((e) => {
            this.props.displaySnackbar(SnackBarType.error, t("account.ERROR"));
        }).finally(() => {
            this.sendGetRequest();
            this.setState({details: !this.state.details});
        })
    };


    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            this.setState({loading: true});
            this.sendPutRequest();
        }
    };

    validateInputs = () => {
        const {t} = this.props;
        let err = "";
        let answer = true;
        if (this.state.description !== undefined) {
            answer = answer && validateDescription(this.state.description);
            if (!validateDescription(this.state.description)) {
                err += " " + t("alley.incorrectDescription");
            }
        }
        if (!answer) {
            this.props.displaySnackbar(SnackBarType.error, err);
        }
        return answer;
    };


    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    handleEditClick = () => {
        if (this.state.details === false) {
            this.sendGetRequest()
        }
        const newState = {
            details: !this.state.details
        };
        this.setState(newState);
    };

    handleCloseDialog = () => {
        this.setState({
            openDialog: false
        })
    };

    handleOpenDialog = () =>  {
        if (this.validateInputs()) {
            this.setState({
                openDialog: true
            })
        }
    };


    generateAlleyDetails = (t) => {
        return (
            <div>
                <Card>
                    <CardHeader
                        title={t("alley.details") + " " + this.state.name}
                        className="card-header"
                    />

                    <CardContent>
                        <div>
                            <Grid container
                                  direction="row"
                                  justify="center"
                                  alignItems="flex-start"
                                  item xs={12}
                                  style={{
                                      paddingLeft: 0,
                                      paddingRight: 0,
                                      paddingBottom: 0,
                                      paddingTop: 0
                                  }}>
                                <Grid container
                                      item
                                      justify="center"
                                      xs={12}
                                      sm={7}
                                      md={5}
                                      lg={4}
                                      xl={3}>
                                        <Grid container
                                              direction="row"
                                              justify="center"
                                              alignItems="flex-start"
                                              item xs={12}>
                                            <Grid item xs={12}>
                                                <Grid item xs={12} container justify='flex-start' alignItems="center"
                                                      style={{padding: 10}}>
                                                    <Grid item xs={12}>
                                                        <TextField name="description"
                                                                   variant="outlined"
                                                                   required
                                                                   multiline
                                                                   disabled={(this.state.details) ? "disabled" : ""}
                                                                   value={this.state.description}
                                                                   onChange={this.handleChange}
                                                                   error={!validateDescription(this.state.description)}
                                                                   label={t("alley.description")}
                                                                   inputProps={{
                                                                       maxLength: 400
                                                                   }}
                                                                   fullWidth/>
                                                    </Grid>
                                                </Grid>
                                                <Grid item xs={12} container justify='flex-start' alignItems="center"
                                                      style={{padding: 10}}>
                                                    <Grid item xs={12}>
                                                        <FormControl variant="outlined" required fullWidth >
                                                            <InputLabel id="demo-simple-select-outlined-label">{t("alley.difficultyLevel")}</InputLabel>
                                                            <Select required
                                                                disabled={(this.state.details) ? "disabled" : ""}
                                                                labelId="demo-simple-select-outlined-label"
                                                                id="demo-simple-select-outlined"
                                                                label={t("alley.difficultyLevel")}
                                                                value={this.state.difficultyLevel}
                                                                onChange={this.handleChange}
                                                                name="difficultyLevel"
                                                            >
                                                                {
                                                                    this.state.difficultyLevels.map((difficultyLevel) => {
                                                                        return <MenuItem value={difficultyLevel}>{t("alleyDifficultyLevel." + difficultyLevel)}</MenuItem>
                                                                    })
                                                                }
                                                            </Select>
                                                        </FormControl>
                                                    </Grid>
                                                </Grid>
                                                <Grid item xs={12} container justify='flex-start' alignItems="center">
                                                    {this.state.details === true &&
                                                    <Button
                                                        name="edit"
                                                        aria-controls="customized-menu"
                                                        aria-haspopup="true"
                                                        variant="contained"
                                                        color="primary"
                                                        onClick={this.handleEditClick}
                                                        fullWidth >
                                                        {t("account.edit")}
                                                    </Button>}
                                                    {this.state.details === false &&
                                                    <Button
                                                        name="submit"
                                                        aria-controls="customized-menu"
                                                        aria-haspopup="true"
                                                        variant="contained"
                                                        color="secondary"
                                                        onClick={() => {
                                                            this.handleOpenDialog()
                                                        }}
                                                        disabled={!validateDescription(this.state.description) || this.state.description.trim() === ""}
                                                        fullWidth>
                                                        {t("account.submit")}
                                                    </Button>}
                                                </Grid>
                                                <ConfirmDialog open={this.state.openDialog}
                                                               title={t("alley.confirmDialogTitle")}
                                                               content={t("alley.confirmDialogContent")}
                                                               handleDialogResponse={this.handleDialogResponse.bind(this)}/>
                                            </Grid>
                                        </Grid>
                                </Grid>
                            </Grid>
                        </div>
                    </CardContent>
                </Card>
            </div>);
    };


    render() {
        const {t} = this.props;
        if (this.state.loading === false) {
            return (
                this.generateAlleyDetails(t)
            )
        }
        if (this.state.loading === true) {
            return <LinearProgress/>
        }
    }
}



export default withTranslation()(withRouter(EmployeeAlleyDetailsComponent));
