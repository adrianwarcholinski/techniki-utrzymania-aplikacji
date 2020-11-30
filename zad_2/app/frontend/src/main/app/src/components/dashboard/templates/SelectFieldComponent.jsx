import * as React from "react";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import {withTranslation} from "react-i18next";

class SelectFieldComponent extends React.Component {

    render() {
        return (
            <FormControl required variant="outlined" fullWidth margin="normal">
                <InputLabel id={this.props.labelId}>{this.props.label}</InputLabel>
                <Select
                    labelId={this.props.labelId}
                    label={this.props.label}
                    value={this.props.value}
                    disabled={this.props.disabled || false}
                    onChange={this.props.onChange !== undefined ? this.props.onChange : null}
                    name={this.props.name !== undefined ? this.props.name : null}>
                    {this.props.items.map(
                        item => <MenuItem value={item.value}>{item.content}</MenuItem>
                    )}
                </Select>
            </FormControl>
        );
    }
}

export default withTranslation()(SelectFieldComponent);