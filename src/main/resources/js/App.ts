import {User} from "./model/User.js";
import {Organization} from "./model/Organization.js";
import {Navbar} from "./app/Navbar.js";
import {Login} from "./app/Login.js";
import {Explorer} from "./app/Explorer.js";
import {Viewer} from "./app/Viewer.js";
import {Tools} from "./app/Tools.js";

export class App {

    private readonly navbar = new Navbar(this);
    private readonly login = new Login();
    private readonly explorer = new Explorer();
    private readonly viewer = new Viewer();

    private readonly user : User;
    private readonly organization : Organization;

    private page : string;
    private idList : string;

    constructor() {
        let userId = Tools.getUrlParameter("user.id");
        if (userId == null) userId = Tools.readFromStorage("user.id");
        if (userId != null) {
            this.user = new User(userId);
        }

        let organizationId = Tools.getUrlParameter("organization.id");
        if (organizationId == null) organizationId = Tools.readFromStorage("organization.id");
        if (organizationId != null) {
            this.organization = new Organization(organizationId);
        }

        console.log(userId);
        console.log(userId == null);

        this.login.show();
    }

    // Getters

    public getNavbar() { return this.navbar; }
    public getLogin() { return this.login; }
    public getExplorer() { return this.explorer; }
    public getViewer() { return this.viewer; }

    public getUser() { return this.user; }
    public getOrganization() { return this.organization; }

}

new App();
