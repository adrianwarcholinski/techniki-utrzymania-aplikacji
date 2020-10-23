import React, {Component} from "react";
import {withTranslation} from "react-i18next";
import {withRouter} from "react-router-dom";
import {getFetch} from "../../../utils/fetchUtility";
import {SnackBarType} from "../templates/SnackBar";
import {Card} from "@material-ui/core";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import Grid from "@material-ui/core/Grid";
import ConfirmDialog from "../templates/ConfirmDialog";
import LinearProgress from "@material-ui/core/LinearProgress";
import {validateDescription} from "../../../utils/regexpUtils";
import TextField from "@material-ui/core/TextField";
import {urls} from "../../../const/Urls";


class CustomerAlleyDetailsComponent extends Component {
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

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
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
                                                        <TextField name="description"
                                                                   variant="outlined"
                                                                   disabled={(this.state.details) ? "disabled" : ""}
                                                                   value={t("alleyDifficultyLevel." + this.state.difficultyLevel)}
                                                                   label={t("alley.difficultyLevel")}
                                                                   inputProps={{
                                                                       maxLength: 400
                                                                   }}
                                                                   fullWidth/>
                                                    </Grid>
                                                </Grid>
                                                <ConfirmDialog open={this.state.openDialog}
                                                               title={t("alley.confirmDialogTitle")}
                                                               content={t("alley.confirmDialogContent")}/>
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



export default withTranslation()(withRouter(CustomerAlleyDetailsComponent));
