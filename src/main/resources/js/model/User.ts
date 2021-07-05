import {VsolRecord} from "./VsolRecord.js";

export class User extends VsolRecord {
    private readonly username : string;
    private readonly firstName : string;
    private readonly lastName : string;
    private readonly email : string;

    constructor(id : string = null, username = "", firstName = "", lastName = "", email = "") {
        super(id);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public static from(src : any) : User {
        return src == null ? null : new User(src.id, src.username, src.firstName, src.lastName, src.email);
    }

    // Getters

    getUsername() { return this.username; }

    getFirstName() { return this.firstName; }

    getLastName() { return this.lastName; }

    getEmail() { return this.email; }

}