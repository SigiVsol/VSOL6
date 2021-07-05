import {Dialog} from "../popup/Dialog.js";

export class API {

    static getJson(url : string, success : (json : any) => void, always : () => void = null) {
        $.get({
            url: url,
            success: success
        }).always(always);
    }

    static postJson(url : string, data : any, success : (json : any) => void, always : () => void = null) {
        $.post({
            url: url,
            data: JSON.stringify(data),
            contentType: "application/json",
            success: success
        }).fail(response => {
            Dialog.inform(String(response));
        }).always(always);
    }

    static put() {
        console.log("PUT");
    }

    static delete() {
        console.log("DELETE");
    }

}