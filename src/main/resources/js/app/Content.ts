export abstract class Content {
    private divId : string;

    protected constructor(divId : string) {
        this.divId = divId;
    }

    public show() {
        $(".div-base-content").css("display", "none");
        $("#" + this.divId).css("display", "block");
    }

}