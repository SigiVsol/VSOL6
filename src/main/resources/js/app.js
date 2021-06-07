class App {
    constructor() {
        this._navbar = new Navbar();
        this._login = new Login();
        this._explorer = new Explorer();
        this._viewer = new Viewer();
        this._page = "";
        this._id = null;
        this._vsolBrowser = navigator.userAgent.includes("VSOL6");
        this._internalCode = "";

        this._user = new User(Tools.readFromStorage("user.id", null));
        this._organization = new Organization(Tools.readFromStorage("organization.id", null));
        this._client = new Client();
        this._patient = new Patient();

        $(window).on('popstate', e => {
            this.popHistory(e.originalEvent["state"]);
            this.show();
        });

        $(window).on('load', () => this.start());
    }

    start() {
        console.log("App started.");
        if (this._vsolBrowser) {
            $("div.navbar-zone").css("display", "none");
            $("div.content-zone").css("right", 0).css("left", 0);
        }

        this._page = window.location.pathname.substr(1);
        if (this.page === "") {
            this.replaceHistory("clients");
        }

        this._id = Tools.getUrlParameter("id");

        if (this.user.id == null) {
            this.show();
        } else {
            $.post({
                url: "api/restoreLogin",
                data: JSON.stringify({
                    userId: this.user.id,
                    organizationId: this.organization.id
                }),
                contentType: "application/json",
                success: json => {
                    this.user.loadJson(json.user);
                    this.organization.loadJson(json.organization);

                    if (this.page === "client") {
                        $.get("api/organizations/" + this.organization.id + "/clients/" + this.id, json => {
                            this.client.loadJson(json);
                            this.show();
                        });
                    } else if (this.page === "patient") {
                        $.get("api/organizations/" + this.organization.id + "/patients/" + this.id, json => {
                            this.patient.loadJson(json);
                            this.show();
                        });
                    } else {
                        this.show();
                    }
                }
            });
        }
    }

    show() {
        this.navbar.show();

        if (this.user.id == null) {
            this.login.show();
        } else if (this.page === "clients" || this.page === "patients" || this.page === "entries" || this.page === "client" || this.page === "patient") {
            this.explorer.show();
        } else if (this.page === "viewer") {
            this.viewer.show();
        }
    }

    pushHistory(page, id = null) { this._pushHistory(page, id, false); }

    replaceHistory(page, id = null) { this._pushHistory(page, id, true); }

    _pushHistory(page, id, replace = false) {
        if (id === "") id = null;

        this._page = page;
        this._id = id;

        let data = {
            page: page,
            id: id // TODO? add client/patient
        }
        let title = "VSOL5";

        let url = "/" + page;
        if (id != null) url += "?id=" + id;

        if (replace)
            history.replaceState(data, title, url);
        else {
            history.pushState(data, title, url);
        }
    }

    popHistory(state) {
        if (state == null) {
            this._page = "clients";
            this._id = null;
        } else {
            this._page = state.page;
            this._id = state.id;
        }
    }

    autoStartDownload(url) {
        let a = document.createElement("a");
        a.setAttribute('href', url);
        a.setAttribute('download', '');
        a.setAttribute('target', '_blank');
        a.click();
    }

    // Getters

    get navbar() { return this._navbar; }
    get login() { return this._login; }
    get explorer() { return this._explorer; }
    get viewer() { return this._viewer; }
    get page() { return this._page; }
    get id() { return this._id; }
    get user() { return this._user; }
    get organization() { return this._organization; }
    get client() { return this._client; }
    get patient() { return this._patient; }
    get internalCode() { return this._internalCode; }

    // Setters

    set id(value) { this._id = value; }
    set user(value) { this._user = value; }
    set organization(value) { this._organization = value; }
    set client(value) { this._client = value; }
    set patient(value) { this._patient = value; }

}