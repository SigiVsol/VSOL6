import {App} from "../App.js";
import {Dialog} from "../popup/Dialog.js";

export class Navbar {

    private app : App;

    constructor(app : App) {
        this.app = app;

        $("#btnBack").click(() => history.back());
        $("#btnHome").click(() => this.home());
        $("#btnLogout").click(() => this.logout());
        $("#btnOrganizations").click(() => this.changeOrganization());
        $("#btnSettings").click(() => this.settings());
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

