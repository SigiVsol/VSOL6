import {App} from "../App.js";
import {Tools} from "../tools/Tools.js";
import {Dialog} from "../popup/Dialog.js";

export class Navbar {

    private app : App;

    private visible = true;
    private width = 75;

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

    public resize() : void {
        this.visible = this.app.getWidth() > 700;

        $("#divNavbar").css("width", (this.visible ? this.width + "px" : "0"));
        $("#divContent").css("right", (this.visible ? (this.width + 1) + "px" : "0"));
    }

    private home() {
        this.app.setPage("explorer");
        this.app.setClient(null);
        this.app.setPatient(null);
        this.app.pushHistory();
    }

    private logout() {
        Dialog.confirm("%{Are_you_sure}?", () => {
            Tools.clearStorage("userId");
            Tools.clearStorage("organizationId");

            this.app.setUser(null);
            this.app.setOrganization(null);
            this.app.fill();
        });
    }

    private changeOrganization() {

    }

    private settings() {
        this.app.setPage("settings");
        this.app.setClient(null);
        this.app.setPatient(null);
        this.app.pushHistory();
    }

    // Getters

    public isVisible() { return this.visible; }

}

