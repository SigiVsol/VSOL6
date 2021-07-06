import { Content } from "./Content.js";
import { User } from "../model/User.js";
import { AddUser } from "../popup/AddUser.js";
export class UserManager extends Content {
    constructor(app) {
        super(app, "divUserManager");
        this.users = [];
        $("#btnAddUser").click(() => this.addUser());
        $("#btnEdit").click(() => this.editUsers());
        for (let i = 1; i <= 30; i++)
            this.users.push(new User(i.toString(), "user_" + i));
        this.fill();
    }
    fill() {
        this.clear();
        for (let user of this.users) {
            let tr = $("<tr></tr>");
            let tdUserName = "<td class='tdUserName'>" + user.getUsername() + "</td>";
            let tdRole = "<td class='tdRole'>" + "user" + "</td>";
            let tdButtons = "<td class='tdButtons'>" + "<button class=\"edit\" ><img src=\"icon/edit/16\"></button>" + "</td>";
            tr.append(tdUserName).append(tdRole).append(tdButtons);
            tr.click(() => console.log(user.getUsername()));
            $("#tbody-users").append(tr);
        }
    }
    clear() {
        $("#tbody-users").empty();
    }
    addUser() {
        AddUser.show((email, role) => console.log(email, role));
    }
    editUsers() {
    }
}