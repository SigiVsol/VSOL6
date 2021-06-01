class Entry extends VsolRecord {
    constructor(id = null, patient = new Patient(), dateTime = "", description = "") {
        super(id);
        this.patient = patient;
        this.dateTime = dateTime;
        this.description = description;
    }

    loadJson(json) {
        this.id = json.id;
        this.patient.loadJson(json.patient);
        this.dateTime = json.dateTime;
        this.description = json.description;
    }

    toString() {
        return this.description;
    }

    getDateTime() {
        return this.dateTime == null ? "" : this.dateTime.replace("T", " ");
    }
}