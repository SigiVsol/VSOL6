import {Content} from "./Content.js";
import {User} from "../model/User.js";
import {App} from "../App.js";
import {UserFiche} from "../popup/UserFiche.js";
import {AddUser} from "../popup/AddUser.js";

export class UserManager extends Content {
    private app: App;
    private users: User[] = [];

    constructor(app: App) {
        super("divUserManager");
        this.app = app;

        $("#btnAddUser").click(() => this.addUser());
        $("#btnEdit").click(() => this.editUsers());

        for (let i = 1; i <= 10; i++)
            this.users.push(new User(i.toString(), "user_" + i));

        this.fillTable();
    }

    fillTable() {
        this.clear();

        for (let user of this.users) {
            let tr = $("<tr></tr>");
            let tdUserName = "<td>" + user.getUsername() + "</td>";
            let tdRole = "<td>" + "" + "</td>"
            let tdOther = "<td>" + "" + "</td>"
            tr.append(tdUserName).append(tdRole).append(tdOther);
            tr.click(() => UserFiche.show(user));
            $("#tbody-users").append(tr);
        }
    }

    clear() {
        $("#tbody-users").empty();
    }

    addUser() {
        AddUser.show();
    }

    editUsers() {

    }
}