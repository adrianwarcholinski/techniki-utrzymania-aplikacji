import React from "react";
import Snackbar from "@material-ui/core/Snackbar";
import Alert from '@material-ui/lab/Alert';

export const SnackBarType = {
    success: "success",
    error: "error",
    warning: "warning",
    info: "info"
};

export default function SnackBar(props) {
    return (
        <Snackbar name={props.type + 'Snackbar'} open={props.open} onClose={props.handleSnackBarClose}
                  anchorOrigin={props.anchorOrigin}>
            <Alert onClose={props.handleSnackBarClose} severity={props.type}>
                {props.message}
            </Alert>
        </Snackbar>
    )
}

