import {Content} from "./Content.js";
import {App} from "../App.js";

export class Settings extends Content {

    constructor(app : App) {
        super(app, "divSettings");
    }

    public fill(): void {
        super.show();


    }

}