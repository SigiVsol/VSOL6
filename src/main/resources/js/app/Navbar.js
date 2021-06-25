export class Navbar {
    constructor(app) {
        this.btnBack = $("#btnBack");
        this.btnHome = $("#btnHome");
        this.btnLogout = $("#btnLogout");
        this.btnOrganizations = $("#btnOrganizations");
        this.btnSettings = $("#btnSettings");
        this.app = app;
        this.btnBack.click(() => history.back());
        this.btnHome.click(() => this.home());
        this.btnLogout.click(() => this.logout());
        this.btnOrganizations.click(() => this.changeOrganization());
        this.btnSettings.click(() => this.settings());
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
