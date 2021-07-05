export class Content {
    constructor(app, divId) {
        this.app = app;
        this.divId = divId;
    }
    show() {
        $(".div-base-content").css("display", "none");
        $("#" + this.divId).css("display", "block");
    }
}
