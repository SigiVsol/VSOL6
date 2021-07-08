import {Content} from "./Content.js";
import {App} from "../App.js";
import {UserManager} from "./UserManager.js";

export class Settings extends Content {
    private userManager;

    constructor(app : App) {
        super(app, "divSettings");
        this.userManager = new UserManager(this.app);
    }

    public fill(): void {
        super.show();

        this.userManager.fill();
    }

}