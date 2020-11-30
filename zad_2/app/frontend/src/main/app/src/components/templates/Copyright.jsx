import React, {Component} from "react";
import Typography from "@material-ui/core/Typography";
import {withTranslation} from "react-i18next";

class Copyright extends Component {
    render() {
        const {t} = this.props;
        return (
            <Typography noWrap variant="body2" color="inherit" align="center">
                {'© '}
                {new Date().getFullYear()}
                {" " + t("common.copyright") + ': SSBD01'}
            </Typography>
        );
    }
}

export default withTranslation()(Copyright);
