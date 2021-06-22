import {User} from "./model/User.js";
import {Organization} from "./model/Organization.js";

class App {

    private readonly user : User;
    private readonly organization : Organization;

    private page : string;
    private idList : string;

    constructor() {
        this.user = new User("", "Sigi");

        console.log(this.user.getUsername());

    }

    // Getters

    getUser() { return this.user; }
    getOrganization() { return this.organization; }

}

let app = new App();