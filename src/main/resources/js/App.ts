import {User} from "./model/User.js";
import {Organization} from "./model/Organization.js";
import {Navbar} from "./app/Navbar.js";
import {Login} from "./app/Login.js";
import {Explorer} from "./app/Explorer.js";
import {Viewer} from "./app/Viewer.js";

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
