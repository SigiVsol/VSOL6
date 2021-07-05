export class AddUser {
    static show() {
        $("#divPopupLayer").css("display", "block");
        $("#divPopupAddUser").css("display", "inline-block");
        $(".addUser .confirm").click(() => this.addUser());
        $(".addUser .close").click(() => this.close());
    }
    static addUser() {
    }
    static close() {
        $("#divPopupLayer").css("display", "none");
        $("#divPopupAddUser").css("display", "none");
    }
}
