import { Navbar } from "./app/Navbar.js";
import { Login } from "./app/Login.js";
import { Explorer } from "./app/Explorer.js";
import { Viewer } from "./app/Viewer.js";
import { Tools } from "./tools/Tools.js";
import { Client } from "./model/Client.js";
import { Patient } from "./model/Patient.js";
import { Settings } from "./app/Settings.js";
import { API } from "./tools/API.js";
export class App {
    constructor() {
        this.navbar = new Navbar(this);
        this.login = new Login(this);
        this.explorer = new Explorer(this);
        this.viewer = new Viewer(this);
        this.settings = new Settings(this);
        $(window).on('load', () => this.login.restore()); // this will try to restore User and Organization (from cookie / URL), and call fill() either way
        $(window).on('popstate', e => this.popHistory(e.originalEvent["state"])); // Back button behaviour
        $(window).on('load', () => this.resize()); // call resize the first time
        $(window).on('resize', () => this.resize()); // call resize every time the window is resized
    }
    fill() {
        if (this.user == null || this.organization == null) {
            this.login.show();
        }
        else {
            this.login.hide();
            this.navbar.fill();
            this.page = Tools.getUrlPage();
            this.tab = Tools.getUrlParameter("tab");
            let clientId = Tools.getUrlParameter("clientId");
            let patientId = Tools.getUrlParameter("patientId");
            if (this.page == "") {
                this.page = "explorer";
                this.pushHistory(true); // this will call fill() again
            }
            else if (this.patient == null && patientId != null) {
                this.restorePatient(patientId); // this will call fill() again
            }
            else if (this.client == null && clientId != null) {
                this.restoreClient(clientId); // this will call fill() again
            }
            else {
                switch (this.page) {
                    case "explorer":
                        this.explorer.fill();
                        break;
                    case "viewer":
                        this.viewer.fill();
                        break;
                    case "settings":
                        this.settings.fill();
                        break;
                }
            }
        }
    }
    pushHistory(replace = false) {
        let data = {
            page: this.page,
            tab: this.tab,
            client: this.client,
            patient: this.patient
        };
        let title = "VSOL6";
        let url = "/" + this.page;
        let parameters = new Map();
        if (this.tab != null)
            parameters.set("tab", this.tab);
        if (this.patient != null)
            parameters.set("patientId", this.patient.getId());
        else if (this.client != null)
            parameters.set("clientId", this.client.getId());
        url += Tools.getParameterString(parameters);
        if (replace) {
            history.replaceState(data, title, url);
        }
        else {
            history.pushState(data, title, url);
        }
        this.fill();
    }
    popHistory(state) {
        if (state == null) {
            this.page = "explorer";
            this.tab = null;
            this.client = null;
            this.patient = null;
        }
        else {
            this.page = state.page;
            this.tab = state.tab;
            this.client = Client.from(state.client);
            this.patient = Patient.from(state.patient);
        }
        this.fill();
    }
    restorePatient(id) {
        API.getJson("api/organizations/" + this.organization.getId() + "/patients/" + id, json => {
            this.patient = Patient.from(json);
            this.client = this.patient.getClient();
            this.fill();
        });
    }
    restoreClient(id) {
        API.getJson("api/organizations/" + this.organization.getId() + "/clients/" + id, json => {
            this.client = Client.from(json);
            this.fill();
        });
    }
    resize() {
        console.log("resize");
        console.log($(window).width());
        console.log($(window).height());
    }
    // Getters
    getTab() { return this.tab; }
    getUser() { return this.user; }
    getOrganization() { return this.organization; }
    getClient() { return this.client; }
    getPatient() { return this.patient; }
    // Setters
    setPage(page) { this.page = page; }
    setTab(tab) { this.tab = tab; }
    setUser(user) { this.user = user; }
    setOrganization(organization) { this.organization = organization; }
    setClient(client) { this.client = client; }
    setPatient(patient) { this.patient = patient; this.client = patient == null ? null : patient.getClient(); }
}
new App();
