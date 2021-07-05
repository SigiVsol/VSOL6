import {User} from "../model/User.js";

export class UserFiche {
    static show(user: User) {
        $("#divPopupLayer").css("display", "block");
        $("#divPopupUserFiche").css("display", "inline-block");

        $("#userFiche .confirm").click(() => this.saveUser());
        $("#userFiche .close").click(() => this.close());

        $("#txtUserNameUserFiche").val(user.getUsername().toString());
        $("#txtFirstName").val(user.getFirstName().toString());
        $("#txtLastName").val(user.getLastName().toString());
        $("#txtEmail").val(user.getEmail().toString());
    }

    static saveUser() {

    }

    static close() {
        $("#divPopupLayer").css("display", "none");
        $("#divPopupUserFiche").css("display", "none");
    }
}