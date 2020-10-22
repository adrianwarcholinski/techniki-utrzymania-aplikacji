import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import React from "react";
import {useTranslation, withTranslation} from "react-i18next";

function ConfirmDialog(props) {
    const {t} = useTranslation();
    return (
        <Dialog
            name="dialog"
            open={props.open}
            onClose={() => props.handleDialogResponse(false)}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
        >
            <DialogTitle id="alert-dialog-title">{props.title}</DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-description">
                    {props.content}
                </DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button name="cancelButton" onClick={() => props.handleDialogResponse(false)} color="primary">
                    {t("confirmDialog.disagree")}
                </Button>
                <Button name="confirmButton" onClick={() => props.handleDialogResponse(true)} color="primary" autoFocus>
                    {t("confirmDialog.agree")}
                </Button>
            </DialogActions>
        </Dialog>
    )
}

export default withTranslation()(ConfirmDialog);

