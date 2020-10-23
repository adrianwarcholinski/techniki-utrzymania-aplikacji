import React, {Component} from 'react';
import {withTranslation} from "react-i18next";
import {withRouter} from "react-router-dom";
import SnackBar, {SnackBarType} from "../templates/SnackBar";
import {deleteFetch, getFetch, postFetch, putFetch} from "../../../utils/fetchUtility";
import "../../../resources/styles/TextField.scss";
import TextFieldComponent from "../templates/TextFieldComponent";
import Button from "@material-ui/core/Button";
import {validateOpinionContent} from "../../../utils/regexpUtils";
import Rating from "@material-ui/lab/Rating";
import ConfirmDialog from "../templates/ConfirmDialog";
import CircularProgress from "@material-ui/core/CircularProgress";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import Box from "@material-ui/core/Box";

const Mode = {
    LOADING: "Loading",
    ADD: "Add",
    EDIT: "Edit",
}

class AddEditOpinionComponent extends Component {

    constructor(props) {
        super(props);

        this.state = {
            id: "",
            version: "",
            opinionNumber: "",
            content: "",
            rate: 0,
            mode: Mode.LOADING,
            openDialog: false,
            openSnackBar: false,
            response: null,
            removing: false,
        }
    }

    componentDidMount() {
        this.sendLoadOwnOpinionRequest();
    }

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    handleOpenDialog = () => {
        this.setState({
            openDialog: true
        })
    };

    handleCloseDialog = () => {
        this.setState({
            openDialog: false
        })
    };

    handleDialogResponse = (response) => {
        this.handleCloseDialog();
        if (response === true) {
            if (this.state.removing)
                this.sendRemoveOpinionRequest();
            else if (this.state.mode === Mode.ADD) {
                this.sendAddOpinionRequest();
            } else {
                this.sendEditOpinionRequest();
            }
        }
    };

    sendLoadOwnOpinionRequest = () => {
        const {t} = this.props;

        getFetch("/app/opinion/own?name=" + this.props.name, null)
            .then(response => {
                if (response.ok) {
                    return response;
                } else if (response.status === 400) {
                    response.text().then((data) => this.props.displaySnackbar(SnackBarType.error, t(data)));
                }
            })
            .then(response => response.json())
            .then(data => {
                if (JSON.stringify(data) === "{}") {
                    this.setState({
                        mode: Mode.ADD
                    })
                } else {
                    this.setState({
                        id: data.id,
                        version: data.version,
                        opinionNumber: data.opinionNumber,
                        content: data.content,
                        rate: data.rate,
                        mode: Mode.EDIT
                    });
                }
            })
            .catch(e => {
                this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
            });
    };

    sendAddOpinionRequest = () => {
        const {t} = this.props;
        const body = {
            weaponModelName: this.props.name,
            content: this.state.content,
            rate: parseInt(this.state.rate)
        };

        this.setState({
            mode: Mode.LOADING
        });

        postFetch("/app/opinion/add", body)
            .then(response => {
                if (response.status === 200) {
                    this.handleOpenSnackbar(Mode.ADD, response.status, null);
                } else if (response.status === 400) {
                    response.text()
                        .then(data => {
                            this.handleOpenSnackbar(Mode.ADD, response.status, data);
                        });
                }
                this.setState({
                    mode: Mode.ADD
                });
                this.sendLoadOwnOpinionRequest();
                this.props.refreshOpinions();
            });
    };

    sendEditOpinionRequest = () => {
        const {t} = this.props;
        const body = {
            id: this.state.id,
            opinionNumber: this.state.opinionNumber,
            content: this.state.content,
            rate: parseInt(this.state.rate),
            version: this.state.version
        };

        this.setState({
            mode: Mode.LOADING
        });

        putFetch("/app/opinion/edit", body)
            .then(response => {
                if (response.status === 200) {
                    this.handleOpenSnackbar(Mode.EDIT, response.status, null);
                } else if (response.status === 400) {
                    response.text()
                        .then(data => {
                            this.handleOpenSnackbar(Mode.EDIT, response.status, data);
                        })
                }
                this.sendLoadOwnOpinionRequest();
                this.props.refreshOpinions();
            });
    }

    sendRemoveOpinionRequest = () => {
        const {t} = this.props;

        this.setState({
            mode: Mode.LOADING
        });

        deleteFetch("/app/opinion?number=" + this.state.opinionNumber)
            .then(response => {
                if (response.status === 200) {
                    this.props.displaySnackbar(SnackBarType.success, t("opinion.removeSuccess"));
                } else if (response.status === 400) {
                    response.text()
                        .then(data => {
                            this.handleOpenSnackbar(Mode.EDIT, response.status, data);
                        })
                }
                this.sendLoadOwnOpinionRequest();
                this.props.refreshOpinions();
            }).catch(() => {
            this.props.displaySnackbar(SnackBarType.error, t("common.requestError"));
        });
        this.setState({removing: false})
    };

    handleOpenSnackbar = (mode, result, reason) => {
        const {t} = this.props;
        const message = t(reason);
        this.setState({
            mode: mode
        });

        if (result === 200) {
            if (mode === Mode.ADD) {
                this.props.displaySnackbar(SnackBarType.success, t("opinion.addSuccess"));
            } else if (mode === Mode.EDIT) {
                this.props.displaySnackbar(SnackBarType.success, t("opinion.editSuccess"));
            }
        } else {
            this.props.displaySnackbar(SnackBarType.error, message);
        }
    };

    handleCloseSnackbar = () => {
        this.setState({
            openSnackbar: false
        })
    }

    handleSnackBarClose = () => {
        this.setState({
            openSnackBar: false
        });
    };

    validateContent = () => {
        return validateOpinionContent(this.state.content) && this.state.content.trim() !== "";
    }

    render() {
        const {t} = this.props;
        return (
            <div>
                <Grid container
                      direction="row"
                      justify="center"
                      alignItems="center"
                      item xs={12}>
                    {this.state.mode === Mode.EDIT &&
                    <Button name="remove" aria-controls="customized-menu" aria-haspopup={true} variant="contained"
                            color="secondary"
                            onClick={() => this.setState({removing: true}, this.handleOpenDialog)}>
                        {this.state.mode === Mode.LOADING ? <CircularProgress/> : t("opinion.removeButton")}
                    </Button>}
                    <TextFieldComponent name="content" value={this.state.content} onChange={this.handleChange}
                                        label={this.state.mode === Mode.EDIT ? "" : t("opinion.content")}
                                        disabled={this.state.mode === Mode.LOADING} multiline={true}
                                        error={!this.validateContent() && this.state.content !== ""}/>
                    <Box display="flex" mb={3} borderColor="transparent">
                        <Typography component="legend">
                            Ocena*
                        </Typography>
                        <Rating style={{marginLeft: "1%"}} value={parseInt(this.state.rate)} precision={1} name="rate" onChange={this.handleChange}/>
                    </Box>
                    <Button name="submit" aria-controls="customized-menu" aria-haspopup={true} variant="contained"
                            color="secondary"
                            onClick={() => this.handleOpenDialog()}
                            disabled={!this.validateContent() || this.state.content.trim() === "" || this.state.rate === 0}
                            style={{marginLeft: "20%"}}>
                        {this.state.mode === Mode.LOADING ? <CircularProgress/> : this.state.mode === Mode.ADD ?
                            t("opinion.addButton") : t("opinion.editButton")}
                    </Button>
                    <SnackBar open={this.state.openSnackbar} message={this.state.responseMessage}
                              type={this.state.response} handleSnackBarClose={this.handleCloseSnackbar}/>
                    <ConfirmDialog open={this.state.openDialog}
                                   title={this.state.removing ? t("opinion.confirmRemoveTitle") : this.state.mode === Mode.ADD ? t("opinion.confirmAddTitle") : t("opinion.confirmEditTitle")}
                                   content={this.state.removing ? t("opinion.confirmRemoveContent") : this.state.mode === Mode.ADD ? t("opinion.confirmAddContent") : t("opinion.confirmEditContent")}
                                   handleDialogResponse={this.handleDialogResponse}/>
                </Grid>
                <div style={{
                    marginTop: "1%",
                    border: 'solid #EEEEEE',
                    height: '50%'
                }}>
                </div>
            </div>
        );
    }
}

export default withTranslation()(withRouter(AddEditOpinionComponent));