import { Tools } from "../tools/Tools.js";
import { Dialog } from "../popup/Dialog.js";
export class Navbar {
    constructor(app) {
        this.visible = true;
        this.width = 75;
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
    resize() {
        this.visible = this.app.getWidth() > 700;
        $("#divNavbar").css("width", (this.visible ? this.width + "px" : "0"));
        $("#divContent").css("right", (this.visible ? (this.width + 1) + "px" : "0"));
    }
    home() {
        this.app.setPage("explorer");
        this.app.setTab(null);
        this.app.setClient(null);
        this.app.setPatient(null);
        this.app.pushHistory();
    }
    logout() {
        Dialog.confirm("%{Are_you_sure}?", () => {
            Tools.clearStorage("userId");
            Tools.clearStorage("organizationId");
            this.app.setUser(null);
            this.app.setOrganization(null);
            this.app.fill();
        });
    }
    changeOrganization() {
    }
    settings() {
        this.app.setPage("settings");
        this.app.setClient(null);
        this.app.setPatient(null);
        this.app.pushHistory();
    }
    // Getters
    isVisible() { return this.visible; }
}
