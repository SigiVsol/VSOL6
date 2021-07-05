import { Dialog } from "../popup/Dialog.js";
export class API {
    static getJson(url, success, always = null) {
        $.get({
            url: url,
            success: success
        }).always(always);
    }
    static postJson(url, data, success, always = null) {
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
