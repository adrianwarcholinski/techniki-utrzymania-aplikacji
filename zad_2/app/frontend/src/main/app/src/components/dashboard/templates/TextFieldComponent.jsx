import React, {Component} from "react";

import {withTranslation} from "react-i18next";
import TextField from "@material-ui/core/TextField";

class TextFieldComponent extends Component {
    render() {
        return (
            <TextField onChange={this.props.onChange !== undefined ? this.props.onChange : null}
                       onInput={this.props.onInput !== undefined ? this.props.onInput : null}
                       error={this.props.error !== undefined ? this.props.error : null}
                       name={this.props.name !== undefined ? this.props.name : null}
                       label={this.props.label !== undefined ? this.props.label : null}
                       type={this.props.type !== undefined ? this.props.type : null}
                       value={this.props.value !== undefined ? this.props.value : null}
                       helperText={this.props.helperText !== undefined ? this.props.helperText : null}
                       inputProps={this.props.inputProps !== null ? this.props.inputProps : null}
                       disabled={this.props.disabled !== undefined ? this.props.disabled : false}
                       rows={this.props.rows !== undefined ? this.props.rows : null}
                       multiline={this.props.multiline !== undefined ? this.props.multiline : false}
                       className="text-field"
                       variant="outlined"
                       margin="normal"
                       required={this.props.required !== undefined ? this.props.required : true}
                       id={this.props.id !== null ? this.props.id : null}
                       defaultValue={this.props.defaultValue !== null ? this.props.defaultValue : null}
                       fullWidth/>)
    }
}

export default withTranslation()(TextFieldComponent);
