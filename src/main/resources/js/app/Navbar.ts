import {App} from "../App.js";
import {Tools} from "../tools/Tools.js";

export class Navbar {

    private app : App;

    private readonly lblUsername = $("#lblUsername");
    private readonly lblOrganization = $("#lblOrganization");

    constructor(app : App) {
        this.app = app;

        $("#btnBack").click(() => history.back());
        $("#btnHome").click(() => this.home());
        $("#btnLogout").click(() => this.logout());
        $("#btnOrganizations").click(() => this.changeOrganization());
        $("#btnSettings").click(() => this.settings());
    }

    public fill() : void {
        this.lblUsername.text(this.app.getUser().getUsername());
        this.lblOrganization.text(this.app.getOrganization().getName());
    }

    private home() {
        this.app.setPage("explorer");
        this.app.setClient(null);
        this.app.setPatient(null);
        this.app.pushHistory();
    }

    private logout() {
        // TODO are you sure?

        Tools.clearStorage("userId");
        Tools.clearStorage("organizationId");

        this.app.setUser(null);
        this.app.setOrganization(null);
        this.app.fill();
    }

    private changeOrganization() {

    }

    private settings() {
        this.app.setPage("settings");
        this.app.setClient(null);
        this.app.setPatient(null);
        this.app.pushHistory();
    }

}

