export abstract class Content {
    private divId : string;

    constructor(divId : string) {
        this.divId = divId;
    }

    protected show() {
        $(".div-base-content").css("display", "none");
        $("#" + this.divId).css("display", "block");
    }

}