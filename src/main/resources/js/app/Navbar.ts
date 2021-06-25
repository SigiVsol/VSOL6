import {App} from "../App.js";

export class Navbar {

    private app : App;

    private readonly btnBack = $("#btnBack");
    private readonly btnHome = $("#btnHome");
    private readonly btnLogout = $("#btnLogout");
    private readonly btnOrganizations = $("#btnOrganizations");
    private readonly btnSettings = $("#btnSettings");

    constructor(app : App) {
        this.app = app;

        this.btnBack.click(() => history.back());
        this.btnHome.click(() => this.home());
        this.btnLogout.click(() => this.logout());
        this.btnOrganizations.click(() => this.changeOrganization());
        this.btnSettings.click(() => this.settings());
    }

    private home() {
        this.app.getLogin().show();
    }

    private logout() {
        this.app.getExplorer().show();
    }

    private changeOrganization() {
        this.app.getViewer().show();
    }

    private settings() {

    }

}

