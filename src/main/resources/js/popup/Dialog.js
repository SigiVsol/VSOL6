export class Dialog {
    static inform(text) {
        Dialog.voidCallback = null;
        Dialog.stringCallback = null;
        Dialog.numberCallback = null;
        Dialog.cancelCallback = null;
        Dialog.show(text, null, false);
    }
    static confirm(text, yes, no = null) {
        Dialog.voidCallback = yes;
        Dialog.stringCallback = null;
        Dialog.numberCallback = null;
        Dialog.cancelCallback = no;
        Dialog.show(text);
    }
    static getString(text, initial = "", ok, cancel = null) {
        Dialog.voidCallback = null;
        Dialog.stringCallback = ok;
        Dialog.numberCallback = null;
        Dialog.cancelCallback = cancel;
        Dialog.show(text, initial);
    }
    static getNumber(text, initial = 0, ok, cancel = null) {
        Dialog.voidCallback = null;
        Dialog.stringCallback = null;
        Dialog.numberCallback = ok;
        Dialog.cancelCallback = cancel;
        Dialog.show(text, String(initial));
    }
    static show(text, initial = null, cancelable = true) {
        $("#divPopupLayer").css("display", "block");
        $(".div-popup").css("display", "none");
        $("#divPopupDialog").css("display", "inline-block");
        $("#divPopupDialog p.popup-dialog-title").text(text);
        $("#divPopupDialog .input-bar").css("display", initial == null ? "none" : "block");
        const input = $("#divPopupDialog input");
        if (initial != null) {
            input.val(initial).select();
            input.on("keyup", event => {
                if (event.key === "Enter") {
                    this.ok();
                }
                else if (event.key === "Escape") {
                    this.cancel();
                }
            });
        }
        $("#divPopupDialog button.confirm").click(() => Dialog.ok());
        $("#divPopupDialog button.close").css("display", cancelable ? "inline" : "none").click(() => Dialog.cancel());
    }
    static ok() {
        const input = $("#divPopupDialog input");
        if (Dialog.voidCallback != null)
            Dialog.voidCallback();
        else if (Dialog.stringCallback != null)
            Dialog.stringCallback(String(input.val()));
        else if (Dialog.numberCallback != null)
            Dialog.numberCallback(Number(input.val()));
        Dialog.hide();
    }
    static cancel() {
        if (Dialog.cancelCallback != null)
            Dialog.cancelCallback();
        Dialog.hide();
    }
    static hide() {
        $("#divPopupLayer").css("display", "none");
        Dialog.voidCallback = null;
        Dialog.stringCallback = null;
        Dialog.numberCallback = null;
        Dialog.cancelCallback = null;
    }
}
Dialog.voidCallback = null;
Dialog.stringCallback = null;
Dialog.numberCallback = null;
Dialog.cancelCallback = null;
