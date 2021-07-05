import { VsolRecord } from "./VsolRecord.js";
export class Organization extends VsolRecord {
    constructor(id = null, name = "", description = "") {
        super(id);
        this.name = name;
        this.description = description;
    }
    static from(src) {
        return src == null ? null : new Organization(src.id, src.name, src.description);
    }
    toString() {
        return this.name;
    }
    // Getters
    getName() { return this.name; }
    getDescription() { return this.description; }
}
