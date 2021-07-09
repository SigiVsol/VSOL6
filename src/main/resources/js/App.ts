import {User} from "./model/User.js";
import {Organization} from "./model/Organization.js";
import {Navbar} from "./app/Navbar.js";
import {Login} from "./app/Login.js";
import {Explorer} from "./app/Explorer.js";
import {Viewer} from "./app/Viewer.js";
import {Tools} from "./tools/Tools.js";
import {Client} from "./model/Client.js";
import {Patient} from "./model/Patient.js";
import {Settings} from "./app/Settings.js";
import {API} from "./tools/API.js";

export class App {

    private width : number;
    private height : number;

    private readonly navbar = new Navbar(this);
    private readonly login = new Login(this);
    private readonly explorer = new Explorer(this);
    private readonly viewer = new Viewer(this);
    private readonly settings = new Settings(this);

    private user : User;
    private organization : Organization;

    private page : string;
    private tab : string;
    private client : Client;
    private patient : Patient;

    public constructor() {
        $(window).on('load', () => this.login.restore()); // this will try to restore User and Organization (from cookie / URL), and call fill() either way
        $(window).on('popstate', e => this.popHistory(e.originalEvent["state"])); // Back button behaviour

        $(window).on('resize', () => this.resize()); // call resize every time the window is resized
    }

    public fill() : void {
        if (this.user == null || this.organization == null) {
            this.login.show();
        } else {
            this.login.hide();

            this.navbar.fill();

            this.page = Tools.getUrlPage();
            this.tab = Tools.getUrlParameter("tab");
            let clientId = Tools.getUrlParameter("clientId");
            let patientId = Tools.getUrlParameter("patientId");

            if (this.page == "") {
                this.page = "explorer";
                this.pushHistory(true); // this will call fill() again
            } else if (this.patient == null && patientId != null) {
                this.restorePatient(patientId); // this will call fill() again
            } else if (this.client == null && clientId != null) {
                this.restoreClient(clientId); // this will call fill() again
            } else {
                switch (this.page) {
                    case "explorer": this.explorer.fill(); break;
                    case "viewer": this.viewer.fill(); break;
                    case "settings": this.settings.fill(); break;
                }
            }
        }

        this.resize();
    }

    public resize() : void {
        this.width = $(window).width();
        this.height = $(window).height();

        if (this.user == null || this.organization == null) {
            this.login.resize();
        } else {
            this.navbar.resize();

            switch (this.page) {
                case "explorer": this.explorer.resize(); break;
                case "viewer": this.viewer.resize(); break;
                case "settings": this.settings.resize(); break;
            }
        }
    }

    public pushHistory(replace = false) {
        let data = {
            page: this.page,
            tab: this.tab,
            client: this.client,
            patient: this.patient
        };

        let title = "VSOL6";

        let url = "/" + this.page;
        let parameters = new Map<string, string>();
        if (this.tab != null) parameters.set("tab", this.tab);
        if (this.patient != null) parameters.set("patientId", this.patient.getId());
        else if (this.client != null) parameters.set("clientId", this.client.getId());
        url += Tools.getParameterString(parameters);

        if (replace) {
            history.replaceState(data, title, url);
        } else {
            history.pushState(data, title, url);
        }

        this.fill();
    }

    public popHistory(state : any) : void {
        if (state == null) {
            this.page = "explorer";
            this.tab = null;
            this.client = null;
            this.patient = null;
        } else {
            this.page = state.page;
            this.tab = state.tab;
            this.client = Client.from(state.client);
            this.patient = Patient.from(state.patient);
        }

        this.fill();
    }

    private restorePatient(id : string) : void {
        API.getJson("api/organizations/" + this.organization.getId() + "/patients/" + id, json => {
            this.patient = Patient.from(json);
            this.client = this.patient.getClient();
            this.fill();
        });
    }

    private restoreClient(id : string) : void {
        API.getJson("api/organizations/" + this.organization.getId() + "/clients/" + id, json => {
            this.client = Client.from(json);
            this.fill();
        });
    }

    // Getters

    public getTab() { return this.tab; }
    public getUser() { return this.user; }
    public getOrganization() { return this.organization; }
    public getClient() { return this.client; }
    public getPatient() { return this.patient; }
    public getWidth() { return this.width; }
    public getHeight() { return this.height; }

    // Setters

    public setPage(page : string) { this.page = page; }
    public setTab(tab : string) { this.tab = tab; }
    public setUser(user : User) { this.user = user; }
    public setOrganization(organization : Organization) { this.organization = organization; }
    public setClient(client : Client) { this.client = client; }
    public setPatient(patient : Patient) { this.patient = patient; this.client = patient == null ? null : patient.getClient(); }

}

new App();
