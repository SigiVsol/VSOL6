import {VsolRecord} from "./VsolRecord.js";

export class Organization extends VsolRecord {
    private readonly name : string;
    private readonly description : string;

    public constructor(id : string = null, name = "", description = "") {
        super(id);
        this.name = name;
        this.description = description;
    }

    public static from(src : any) {
        return src == null ? null : new Organization(src.id, src.name, src.description);
    }

    public toString() {
        return this.name;
    }

    // Getters

    public getName() { return this.name; }
    public getDescription() { return this.description; }

}