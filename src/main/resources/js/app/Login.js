import { Content } from "./Content.js";
export class Login extends Content {
    constructor() {
        super("divLogin");
        this.chkRememberMe = $("#chkRememberMe");
        this.txtUsername = $("#txtUsername");
        this.txtPassword = $("#txtPassword");
        $("#btnLogin").click(() => this.attemptLogin());
        this.txtUsername.on("keyup", event => {
            if (event.key === "Enter")
                this.attemptLogin();
            else if (event.key === "Escape")
                this.txtUsername.val("");
        });
        this.txtPassword.on("keyup", event => {
            if (event.key === "Enter")
                this.attemptLogin();
            else if (event.key === "Escape")
                this.txtPassword.val("");
        });
    }
    attemptLogin() {
        let local = this.chkRememberMe.prop("checked");
        console.log("attempt login: " + local);
    }
}
