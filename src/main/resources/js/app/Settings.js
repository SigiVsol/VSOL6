import { Content } from "./Content.js";
import { UserManager } from "./UserManager.js";
export class Settings extends Content {
    constructor(app) {
        super(app, "divSettings");
        this.userManager = new UserManager(this.app);
    }
    fill() {
        super.show();
        this.userManager.fill();
    }
}
