class Organization extends VsolRecord {
    constructor(id = null, name = "", description = "") {
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