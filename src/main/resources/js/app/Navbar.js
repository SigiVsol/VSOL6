export class Navbar {
    constructor(app) {
        this.app = app;
        $("#btnBack").click(() => history.back());
        $("#btnHome").click(() => this.home());
        $("#btnLogout").click(() => this.logout());
        $("#btnOrganizations").click(() => this.changeOrganization());
        $("#btnSettings").click(() => this.settings());
    }
    home() {
        this.app.getLogin().show();
    }
    logout() {
        this.app.getExplorer().show();
    }
    changeOrganization() {
        this.app.getViewer().show();
    }
    settings() {
    }
}
