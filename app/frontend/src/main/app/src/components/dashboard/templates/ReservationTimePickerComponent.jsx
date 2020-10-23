import * as React from "react";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import TableRow from "@material-ui/core/TableRow";
import TableContainer from "@material-ui/core/TableContainer";
import {reservationConstants} from "../../../const/Reservations";
import {addMinutes, addZeroToDateElement, getDateToRequest} from "../../../utils/dateUtils";
import {withTranslation} from "react-i18next";
import '../../../resources/styles/MakeReservation.scss'
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import IconButton from "@material-ui/core/IconButton";
import RadioButtonUncheckedIcon from "@material-ui/icons/RadioButtonUnchecked";
import CheckCircleOutlineIcon from "@material-ui/icons/CheckCircleOutline";
import Box from "@material-ui/core/Box";

const defaultProps = {
    bgcolor: 'background.paper',
    border: 2,
    radius: 5,
    padding: 1
};

class ReservationTimePickerComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            content: this.getContent(),
            reservations: [],
            lowestClickedButton: null,
            highestClickedButton: null
        }
    }

    componentDidMount() {
        this.setState({content: this.getContent()}, () => {
            this.getButtons();
        });
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.alleyName !== this.props.alleyName || prevProps.conflictReservations !== this.props.conflictReservations) {
            this.setState({content: this.getContent()}, () => this.getButtons());
            this.resetStartAndEndDate();

        }
        if (prevProps.date !== this.props.date) {
            this.resetStartAndEndDate();
        }
    }

    resetStartAndEndDate = () => {
        this.props.onChange("", "");
    }

    selectChoosedTimes = (content) => {
        let startDate = new Date(this.props.startDate);
        const endDate = new Date(this.props.endDate);
        while (startDate.getTime() !== endDate.getTime()) {
            const labelString = `${addZeroToDateElement(startDate.getHours())}:${addZeroToDateElement(startDate.getMinutes())}`;
            content.forEach(item => {
                if (item.label === labelString) {
                    item.selected = true;
                }
            });
            startDate = addMinutes(startDate, 30);
        }
        return content;
    }

    getButtons = () => {
        const content = this.state.content;
        if (this.props.startDate && this.props.endDate) {
            this.selectChoosedTimes(content);
        }

        this.props.conflictReservations.forEach(reservation => {
            let startDate = new Date(reservation.startDate);
            const endDate = new Date(reservation.endDate);
            while (startDate.getTime() !== endDate.getTime()) {
                const labelString = `${addZeroToDateElement(startDate.getHours())}:${addZeroToDateElement(startDate.getMinutes())}`;
                content.forEach(item => {
                    if (item.label === labelString) {
                        item.selected = null;
                    }
                });
                startDate = addMinutes(startDate, 30);
            }
        });
        const indexes = this.getLowestAndHighestClickedButton(content);
        let err = this.validate(content, indexes.min, indexes.max)
        this.props.handleDateError(err);
        this.setState({
            content: content
        });


    };

    getContent = () => {
        const columns = [];
        let index = 0;
        for (let i = reservationConstants.openingHour; i < reservationConstants.closingHour; i++) {
            columns.push({label: addZeroToDateElement(i) + ":00", index: index, selected: false});
            index++;
            columns.push({label: addZeroToDateElement(i) + ":30", index: index, selected: false});
            index++;
        }
        return columns;
    };

    handleClickButton = (index) => {
        let content = this.state.content.slice();
        const item = content[index];
        item.selected = !item.selected;
        content[index] = item;
        const indexes = this.getLowestAndHighestClickedButton(content);
        const err = this.validate(content, indexes.min, indexes.max);

        if (indexes.min !== null && indexes.max !== null && !err) {
            const startDate = getDateToRequest(new Date(this.props.date + "T" + content[indexes.min].label));
            const endDate = getDateToRequest(addMinutes(new Date(this.props.date + "T" + content[indexes.max].label), 30));
            this.props.onChange(startDate, endDate);
        }
        else {
            this.props.onChange("", "")
        }
        this.props.handleDateError(err);
        this.setState({
            lowestClickedButton: indexes.min,
            highestClickedButton: indexes.max,
            content: content
        });
    };

    getLowestAndHighestClickedButton = (content) => {
        let minIndex = null;
        let maxIndex = null;
        let findFlag = false;
        content.forEach(item => {
            if (item.selected && !findFlag) {
                findFlag = true;
                minIndex = item.index;
            }
        });
        findFlag = false;
        content.slice().reverse().forEach(item => {
            if (item.selected && !findFlag) {
                findFlag = true;
                maxIndex = item.index;
            }
        });
        return {
            min: minIndex,
            max: maxIndex
        };
    };

    getActiveButton = (index) => {
        return <IconButton onClick={() => this.handleClickButton(index)}><RadioButtonUncheckedIcon/></IconButton>
    };

    getSelectedButton = (index) => {
        return <IconButton color="primary"
                           onClick={() => this.handleClickButton(index)}><CheckCircleOutlineIcon/></IconButton>
    };

    validate = (content, minIndex, maxIndex) => {
        let findFlag = false;
        let err;
        if (minIndex !== null && maxIndex !== null) {
            for (let i = minIndex; i <= maxIndex; i++) {
                if (content[i].selected === null || content[i].selected === false) {
                    err = true;
                    findFlag = true;
                }
            }
            const now = new Date();
            const reserveStartDate = new Date(this.props.date + "T" + content[minIndex].label);
            if (now.getTime() >= reserveStartDate.getTime()) {
                err = true;
                findFlag = true;
            }

        }
        if (!findFlag) {
            err = false;
        }
        if (maxIndex - minIndex >= 8) {
            err = true;
        }
        return err;
    };

    render() {
        const {t} = this.props;
        return (
            <Box borderColor={this.props.err ? "red" : "#c4c4c4"} borderRadius={5} {...defaultProps} >
                <div>
                    <TableContainer>
                        <Table>
                            <TableHead>
                                {this.state.content.map((column) => (
                                    <TableCell align="center">
                                        {column.label}
                                    </TableCell>
                                ))}
                            </TableHead>
                            <TableBody>
                                <TableRow>
                                    {this.state.content.map((item) =>
                                        <TableCell>
                                            {item.selected === null ? <IconButton
                                                    disabled style={{color: "red"}}><HighlightOffIcon/></IconButton> :
                                                item.selected ? this.getSelectedButton(item.index) : this.getActiveButton(item.index)}
                                        </TableCell>
                                    )}
                                </TableRow>
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <p className={this.props.err ? "error" : "normal"}>{t("makeReservation.validation")}</p>
                </div>
            </Box>
        );
    }
}

export default withTranslation()(ReservationTimePickerComponent);
