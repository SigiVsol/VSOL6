class Dialog {

    static inform(text = "") {
        this.show(text, false, "void", null);
    }

    static confirm(text = "", callback = function() {}) {
        this.show(text, true, "void", callback);
    }

    static getString(text = "", initial = "", callback = function() {}) {
        this.show(text, true, "string", callback);
        $(".popup-dialog div.input-bar input").val(initial).select();
    }

    static getInt(text = "", initial = 0, callback = function() {}) {
        this.show(text, true, "int", callback);
        $(".popup-dialog div.input-bar input").val(initial).select();
    }

    static show(text, cancelable, returnType, callback) {
        Dialog._callback = callback;
        Dialog._returnType = returnType;

        $(".popup-dialog div.input-bar input").on("keyup", event => {
            if (event.key === "Enter") {
                this.ok();
            } else if (event.key === "Escape") {
                this.close();
            }
        });

        $(".popup-dialog span").text(text);
        $(".popup-dialog").css("display", "block");
        $(".popup-dialog div.input-bar").css("display", (returnType !== "void") ? "block" : "none");
        $(".popup-dialog button.close").css("display", cancelable ? "inline" : "none");
    }

    static ok() {
        if (Dialog._callback != null) {
            if (Dialog._returnType === "string") {
                Dialog._callback($(".popup-dialog div.input-bar input").val());
            } else if (Dialog._returnType === "int") {
                Dialog._callback(parseInt($(".popup-dialog div.input-bar input").val()));
            } else {
                Dialog._callback();
            }
        }

        this.close();
    }

    static close() {
        $(".popup-dialog").css("display", "none");
        $(".popup-dialog div.input-bar input").off("keyup");
        Dialog._callback = null;
    }

}