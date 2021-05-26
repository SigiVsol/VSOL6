class Login {
    constructor() {
        $("#txtUsername").on("keyup", event => {
            if (event.key === "Enter") this.attemptLogin();
            else if (event.key === "Escape") $("#txtUsername").val("");
        });

        $("#txtPassword").on("keyup", event => {
            if (event.key === "Enter") this.attemptLogin();
            else if (event.key === "Escape") $("#txtPassword").val("");
        });
    }

    show() {
        $("#divLogin").css("display", "block");
        $("#divExplorer").css("display", "none");
        $("#divViewer").css("display", "none");
        $("#divNavbar :button").prop("disabled", true);

        $("#txtUsername").trigger('focus');
        $("#chkRememberMe").prop("checked", false);
    }

    attemptLogin() {
        let local = $("#chkRememberMe").prop("checked");

        $("#divLogin").css("opacity", 0.3);

        $.post({
            url: "api/authenticate",
            data: JSON.stringify({
                username: $("#txtUsername").val(),
                password: $("#txtPassword").val()
            }),
            contentType: "application/json",
            success: json => {
                app.user.loadJson(json.user);
                app.organization.loadJson(json.organization);

                Tools.saveInStorage("user.id", app.user.id, local);
                Tools.saveInStorage("organization.id", app.organization.id, local);

                app.show();
            }
        }).fail(response => {
            Dialog.inform(response.responseText);
        }).always(() => {
            $("#txtUsername").val("");
            $("#txtPassword").val("");
            $("#divLogin").css("opacity", 1.0);
        });
    }

}