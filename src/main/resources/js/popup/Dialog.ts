export class Dialog {
    private static voidCallback : () => void = null;
    private static stringCallback : (result : string) => void = null;
    private static numberCallback: (result : number) => void = null;
    private static cancelCallback: () => void = null;

    public static inform(text : string) {
        Dialog.voidCallback = null;
        Dialog.stringCallback = null;
        Dialog.numberCallback = null;
        Dialog.cancelCallback = null;
        Dialog.show(text, null, false);
    }

    public static confirm(text : string, yes : () => void, no : () => void = null) {
        Dialog.voidCallback = yes;
        Dialog.stringCallback = null;
        Dialog.numberCallback = null;
        Dialog.cancelCallback = no;
        Dialog.show(text);
    }

    public static getString(text : string, initial : string = "", ok : (result : string) => void, cancel : () => void = null) {
        Dialog.voidCallback = null;
        Dialog.stringCallback = ok;
        Dialog.numberCallback = null;
        Dialog.cancelCallback = cancel;
        Dialog.show(text, initial);
    }

    public static getNumber(text : string, initial : number = 0, ok : (result : number) => void, cancel : () => void = null) {
        Dialog.voidCallback = null;
        Dialog.stringCallback = null;
        Dialog.numberCallback = ok;
        Dialog.cancelCallback = cancel;
        Dialog.show(text, String(initial));
    }

    private static show(text : string, initial : string = null, cancelable = true) : void {
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
                } else if (event.key === "Escape") {
                    this.cancel();
                }
            });
        }

        $("#divPopupDialog button.confirm").click(() => Dialog.ok());
        $("#divPopupDialog button.close").css("display", cancelable ? "inline" : "none").click(() => Dialog.cancel());
    }

    private static ok() {
        const input = $("#divPopupDialog input");

        if (Dialog.voidCallback != null) Dialog.voidCallback();
        else if (Dialog.stringCallback != null) Dialog.stringCallback(String(input.val()));
        else if (Dialog.numberCallback != null) Dialog.numberCallback(Number(input.val()));

        Dialog.hide();
    }

    private static cancel() {
        if (Dialog.cancelCallback != null) Dialog.cancelCallback();

        Dialog.hide();
    }

    private static hide() : void {
        $("#divPopupLayer").css("display", "none");

        Dialog.voidCallback = null;
        Dialog.stringCallback = null;
        Dialog.numberCallback = null;
        Dialog.cancelCallback = null;
    }
}
