import React from "react";
import ReCAPTCHA from "react-google-recaptcha";
import serverConfig from "../../../properties.json";


export default function Captcha(props) {
    if (serverConfig.ENABLE_CAPTCHA) {
        return (
            <ReCAPTCHA
                ref={props.captchaRef}
                sitekey={serverConfig.CAPTCHA_API_KEY}
                onChange={props.filledCaptchaCallback}
                onExpired={props.expiredCaptchaCallback}
                hl={navigator.language}
            />
        )
    }
}

