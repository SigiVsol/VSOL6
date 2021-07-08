import { User } from "../model/User.js";
import { AddUser } from "../popup/AddUser.js";
import { API } from "../tools/API.js";
export class UserManager {
    constructor(app) {
        this.app = app;
        $(".btn-add-user").click(() => this.addUser());
    }
    fill() {
        let tbody = $("<tbody></tbody>");
        let urlUsers = "/api/organizations/" + this.app.getOrganization().getId() + "/users";
        API.getJson(urlUsers, json => {
            for (let user of User.fromRows(json.rows)) {
                let tr = $("<tr></tr>");
                $("<td><label><input type='checkbox'></label>").appendTo(tr);
                $("<td>" + user.getUsername() + "</td>").appendTo(tr);
                $("<td>" + user.getEmail() + "</td>").appendTo(tr);
                $("<td>" + "" + "</td>").appendTo(tr);
                $("<td>" + this.getUserActionButtons() + "</td>").appendTo(tr);
                tr.appendTo(tbody);
            }
            $("#divSettings .table-users tbody").replaceWith(tbody);
        });
    }
    addUser() {
        AddUser.show((email, role) => {
            this.fill();
        });
    }
    getUserActionButtons() {
        let result = "";
        result += "<button class='edit'><img src='icon/edit/16'></button>";
        result += "<button class='cancel'><img src='icon/delete/16'></button>";
        return result;
    }
}
