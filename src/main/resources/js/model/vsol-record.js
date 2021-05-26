class VsolRecord {
    constructor(id = null) {
        this.id = id;
    }

    toString() {
        return this.id == null ? "" : this.id;
    }

    loadJson(json) {

    }
}