import { Tools } from "../tools/Tools.js";
export class Navbar {
    constructor(app) {
        this.lblUsername = $("#lblUsername");
        this.lblOrganization = $("#lblOrganization");
        this.app = app;
        $("#btnBack").click(() => history.back());
        $("#btnHome").click(() => this.home());
        $("#btnLogout").click(() => this.logout());
        $("#btnOrganizations").click(() => this.changeOrganization());
        $("#btnSettings").click(() => this.settings());
    }
    fill() {
        this.lblUsername.text(this.app.getUser().getUsername());
        this.lblOrganization.text(this.app.getOrganization().getName());
    }
    home() {
        this.app.setPage("explorer");
        this.app.setClient(null);
        this.app.setPatient(null);
        this.app.pushHistory();
    }
    logout() {
        // TODO are you sure?
        Tools.clearStorage("userId");
        Tools.clearStorage("organizationId");
        this.app.setUser(null);
        this.app.setOrganization(null);
        this.app.fill();
    }
    changeOrganization() {
    }
    settings() {
        this.app.setPage("settings");
        this.app.setClient(null);
        this.app.setPatient(null);
        this.app.pushHistory();
    }
}
