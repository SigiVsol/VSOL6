import {VsolRecord} from "./VsolRecord.js";

export class Organization extends VsolRecord {
    private name : string;
    private description : string;

    constructor(id : string = null, name = "", description = "") {
        super(id);
        this.name = name;
        this.description = description;
    }

    loadJson(json) {
        this.id = json.id;
        this.name = json.name;
        this.description = json.description;
    }

    toString() {
        return this.name;
    }

}