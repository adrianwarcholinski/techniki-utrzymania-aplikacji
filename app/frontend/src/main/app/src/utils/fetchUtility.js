import Cookies from "js-cookie";
import {urls} from "../const/Urls";
import i18n from "../i18n";

const baseConfig = {
    mode: "cors",
    cache: "no-cache",
    credentials: "same-origin",
    headers: {
        "Content-Type": "application/json; charset=utf-8",
    },
    redirect: "follow",
    referrer: "no-referrer",
};


const send = (method, payload) => (
    async function (uri, config) {
        const t = i18n.t.bind(i18n);
        let sources = [config];
        if (method === "POST" || method === 'PUT') {
            sources.push({body: JSON.stringify(payload)});
        }
        config = Object.assign({
            method: method,
            ...baseConfig,
        }, ...sources);
        return await fetch(process.env.PUBLIC_URL+uri, config).then((response)=>{
           if(response.status===401 || response.status===403){
               localStorage.clear();
               Cookies.remove("JREMEMBERMEID", {path: process.env.PUBLIC_URL});
               Cookies.remove("JREMEMBERMEID");
               alert(t("app.sessionExpired"));
               window.location.href = urls.login;
           } else if(response.status===500){
               alert(t("app.somethingWentWrong"));
           } else if(response.status === 404)
               alert(t("app.resourceNotFound"));
            return response;
        });
    }
);

const getFetch = (uri, config = {}) => (
    send("GET")(uri, config)
)



const postFetch = (uri, data = undefined, config = {}) => (
    send("POST", data)(uri, config)
);

const putFetch = (uri, data, config = {}) => (
    send("PUT", data)(uri, config)
);

const deleteFetch = (uri, config = {}) => (
    send("DELETE")(uri, config)
);

export {getFetch, postFetch, putFetch, deleteFetch};
