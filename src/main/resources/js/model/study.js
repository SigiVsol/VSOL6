class Study extends VsolRecord {
    constructor(id = null, patient = new Patient(), dateTime = "", description = "", seriesCount = 0) {
        super(id);
        this.patient = patient;
        this.dateTime = dateTime;
        this.description = description;
        this.seriesCount = seriesCount;
    }

    loadJson(json) {
        this.id = json.id;
        this.patient.loadJson(json.patient);
        this.dateTime = json.dateTime;
        this.description = json.description;
        this.seriesCount = json.seriesCount;
    }

    toString() {
        return this.description;
    }
}