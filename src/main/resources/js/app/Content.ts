import {App} from "../App.js";

export abstract class Content {
    protected app : App;
    private divId : string;

    protected constructor(app : App, divId : string) {
        this.app = app;
        this.divId = divId;
    }

    public show() : void {
        $(".div-base-content").css("display", "none");
        $("#" + this.divId).css("display", "block");
    }

    public abstract fill() : void;

    public abstract resize() ; void;

}
