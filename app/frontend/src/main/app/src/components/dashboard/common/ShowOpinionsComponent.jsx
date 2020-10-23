import React, {Component} from "react";
import {withTranslation} from "react-i18next";
import Rating from "@material-ui/lab/Rating";
import Box from "@material-ui/core/Box";
import GridList from '@material-ui/core/GridList';
import "../../../resources/styles/ShowOpinions.scss";


class ShowOpinionsComponent extends Component {

    constructor(props) {
        super(props);
    }


    render() {
        const {t} = this.props;
        if (this.props.opinions.length !== 0) {
            return (
                <GridList cols={1} spacing={1} style={{maxHeight: 650}} className={"opinion-container"}>
                    {this.props.opinions.map(item => {
                        return (
                            <Box className={"opinion"} style={{padding: 10}}>
                                <div className={"opinion-header"}>
                                    {item.customerLogin}
                                    <Rating precision={0.5} value={item.rate} style={{float: "right"}} readOnly/>
                                </div>
                                <div>
                                    <p className={"opinion-content"}>{item.content}</p>
                                </div>
                            </Box>
                        )
                    })}
                </GridList>
            );
        } else {
            return (
                <Box className={this.props.infoClass}>
                    <span className={"info-element"}>{t("opinion.lackOfOpinions")}</span>
                </Box>
            );
        }
    }

}

export default withTranslation()(ShowOpinionsComponent);
