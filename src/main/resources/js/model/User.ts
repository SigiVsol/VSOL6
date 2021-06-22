import {VsolRecord} from "./VsolRecord.js";

export class User extends VsolRecord {
    private username : String;
    private firstName : String;
    private lastName : String;
    private email : String;

    constructor(id : string = null, username = "", firstName = "", lastName = "", email = "") {
        super(id);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    loadJson(json) {
        this.id = json.id;
        this.username = json.username;
        this.firstName = json.firstName;
        this.lastName = json.lastName;
        this.email = json.email;
    }

    // Getters

    getUsername() { return this.username; }

    getFirstName() { return this.firstName; }

    getLastName() { return this.lastName; }

    getEmail() { return this.email; }

}