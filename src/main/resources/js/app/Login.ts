import {Content} from "./Content.js";

export class Login extends Content {

    private readonly chkRememberMe = $("#chkRememberMe");
    private readonly txtUsername = $("#txtUsername");
    private readonly txtPassword = $("#txtPassword");

    constructor() {
        super("divLogin");

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

    private attemptLogin() {
        let local = this.chkRememberMe.prop("checked");

        console.log("attempt login: " + local);
    }

}