import React from 'react';
import Button from '@material-ui/core/Button';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import {useTranslation} from "react-i18next";
import GroupWorkIcon from '@material-ui/icons/GroupWork';

import '../../../resources/styles/Header.scss';
import Typography from "@material-ui/core/Typography";

export default function RoleComponent(props) {
    const [anchorEl, setAnchorEl] = React.useState(null);
    const {t} = useTranslation();

    const AvailableRoles = () => (
        props.roles.map(role => {
            return <MenuItem name={role} aria-controls="customized-menu"
                             onClick={() => {
                                 props.changeRoleHandler(role);
                                 handleClose()
                             }}
                             style={{
                                 width: '200px',
                             }}>
                <Typography className='account-menu-item-typography' variant='body1' align='center'>
                    {t(`dashboard.${role}`)}
                </Typography>
            </MenuItem>
        }));

    const handleClick = (event) => {
        if (props.roles.length >= 1) {
            setAnchorEl(event.currentTarget);
        }
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    return (
        <div>
            <Button
                name="activeRoleButton"
                className={props.buttonColorClass + ' role-button'}
                variant="contained"
                color="primary"
                size="large"
                endIcon={<GroupWorkIcon style={{fontSize: "32px", marginLeft: "-3px"}}/>}
                aria-controls="customized-menu"
                aria-haspopup="true"
                onClick={handleClick}>
                {t("dashboard." + props.activeRole)}
            </Button>
            <Menu
                id="customized-menu"
                anchorEl={anchorEl}
                keepMounted
                open={Boolean(anchorEl)}
                onClose={handleClose}
                anchorOrigin={{vertical: "bottom", horizontal: "center"}}
                transformOrigin={{vertical: "top", horizontal: "center"}}>
                <AvailableRoles/>
            </Menu>
        </div>
    );
}
