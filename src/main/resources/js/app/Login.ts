import {API} from "../tools/API.js";
import {User} from "../model/User.js";
import {Organization} from "../model/Organization.js";
import {App} from "../App.js";
import {Tools} from "../tools/Tools.js";

export class Login {

    private readonly app : App;
    private readonly chkRememberMe = $("#chkRememberMe");
    private readonly txtUsername = $("#txtUsername");
    private readonly txtPassword = $("#txtPassword");

    public constructor(app: App) {
        this.app = app;
        $("#btnLogin").click(() => this.attemptLogin());
        this.txtUsername.on("keyup", event => {
            if (event.key === "Enter") this.attemptLogin();
            else if (event.key === "Escape") this.txtUsername.val("");
        });
        this.txtPassword.on("keyup", event => {
            if (event.key === "Enter") this.attemptLogin();
            else if (event.key === "Escape") this.txtPassword.val("");
        });
    }

    public show() : void {
        $("#divLogin").css("display", "block");
        $("#divMain").css("display", "none");
    }

    public hide() : void {
        $("#divLogin").css("display", "none");
        $("#divMain").css("display", "block");
    }

    private attemptLogin() : void {
        let remember = this.chkRememberMe.prop("checked");

        let credentials = {
            username: this.txtUsername.val(),
            password: this.txtPassword.val()
        };

        API.postJson("api/authenticate", credentials, json => this.fromJson(json, remember));
    }

    public restore() {
        let userId = Tools.isFromVsol6App() ? Tools.getUrlParameter("userId") : Tools.readFromStorage("userId");
        let organizationId = Tools.isFromVsol6App() ? Tools.getUrlParameter("organizationId") : Tools.readFromStorage("organizationId");

        if (userId == null || organizationId == null) {
            this.app.fill();
        } else {
            let data = {
                userId: userId,
                organizationId: organizationId
            }

            API.postJson("api/restoreLogin", data, json => this.fromJson(json, false));
        }
    }

    private fromJson(json : any, remember : boolean) {
        let user = User.from(json.user);
        let organization = Organization.from(json.organization);

        Tools.saveInStorage("userId", user.getId(), remember);
        Tools.saveInStorage("organizationId", organization.getId(), remember);

        this.app.setUser(user);
        this.app.setOrganization(organization);
        this.app.fill();
    }



}
