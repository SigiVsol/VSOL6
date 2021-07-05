import { VsolRecord } from "./VsolRecord.js";
export class User extends VsolRecord {
    constructor(id = null, username = "", firstName = "", lastName = "", email = "") {
        super(id);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    static from(src) {
        return src == null ? null : new User(src.id, src.username, src.firstName, src.lastName, src.email);
    }
    // Getters
    getUsername() { return this.username; }
    getFirstName() { return this.firstName; }
    getLastName() { return this.lastName; }
    getEmail() { return this.email; }
}
