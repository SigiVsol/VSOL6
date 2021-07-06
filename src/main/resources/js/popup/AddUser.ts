export class AddUser {
    private static callback: (email: String, role: String) => void = null;

    static show(callback: (email: String, role: String) => void = null) {
        AddUser.callback = callback;

        $("#divPopupLayer").css("display", "block");
        $(".div-popup").css("display", "none");
        $("#divPopupAddUser").css("display", "inline-block");
        $("#rdUser").prop("checked", true);
        const email = $("#txtEmail");
        email.val("").select();
        email.on("keyup", event => {
            if (event.key === "Enter") {
                this.addUser();
            } else if (event.key === "Escape") {
                this.close();
            }
        });

        $("#divPopupAddUser .confirm").click(() => AddUser.addUser());
        $("#divPopupAddUser .close").click(() => AddUser.close());
    }

    static addUser() {
        const email = String($("#txtEmail").val());
        const role = String($("input[name=userRole]:checked").val());
        if (AddUser.callback != null) AddUser.callback(email, role);

        AddUser.close();
    }

    static close() {
        AddUser.callback = null;
        $("#divPopupLayer").css("display", "none");
        $("#divPopupAddUser").css("display", "none");
    }
}