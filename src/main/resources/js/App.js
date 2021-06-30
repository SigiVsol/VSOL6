import { User } from "./model/User.js";
import { Organization } from "./model/Organization.js";
import { Navbar } from "./app/Navbar.js";
import { Login } from "./app/Login.js";
import { Explorer } from "./app/Explorer.js";
import { Viewer } from "./app/Viewer.js";
import { Tools } from "./app/Tools.js";
export class App {
    constructor() {
        this.navbar = new Navbar(this);
        this.login = new Login();
        this.explorer = new Explorer();
        this.viewer = new Viewer();
        let userId = Tools.getUrlParameter("user.id");
        if (userId == null)
            userId = Tools.readFromStorage("user.id");
        if (userId != null) {
            this.user = new User(userId);
        }
        let organizationId = Tools.getUrlParameter("organization.id");
        if (organizationId == null)
            organizationId = Tools.readFromStorage("organization.id");
        if (organizationId != null) {
            this.organization = new Organization(organizationId);
        }
        console.log(userId);
        console.log(userId == null);
        this.login.show();
    }
    // Getters
    getNavbar() { return this.navbar; }
    getLogin() { return this.login; }
    getExplorer() { return this.explorer; }
    getViewer() { return this.viewer; }
    getUser() { return this.user; }
    getOrganization() { return this.organization; }
}
new App();
