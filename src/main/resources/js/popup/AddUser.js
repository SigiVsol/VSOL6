export class AddUser {
    static show(callback = null) {
        AddUser.callback = callback;
        $("#divPopupLayer").css("display", "block");
        $(".div-popup").css("display", "none");
        $("#divPopupAddUser").css("display", "inline-block");
        $("#txtEmail").val("").select();
        $("#rdUser").prop("checked", true);
        $("#divPopupAddUser .confirm").click(() => AddUser.addUser());
        $("#divPopupAddUser .close").click(() => AddUser.close());
    }
    static addUser() {
        const email = String($("#txtEmail").val());
        const role = String($("input[name=userRole]:checked").val());
        if (AddUser.callback != null)
            AddUser.callback(email, role);
        AddUser.close();
    }
    static close() {
        AddUser.callback = null;
        $("#divPopupLayer").css("display", "none");
        $("#divPopupAddUser").css("display", "none");
    }
}
AddUser.callback = null;
