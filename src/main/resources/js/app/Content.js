export class Content {
    constructor(divId) {
        this.divId = divId;
    }
    show() {
        $(".div-base-content").css("display", "none");
        $("#" + this.divId).css("display", "block");
    }
}
