import { Content } from "./Content.js";
export class Settings extends Content {
    constructor(app) {
        super(app, "divSettings");
    }
    fill() {
        super.show();
    }
}
