class Study extends Entry {
    constructor(id = null, patient = new Patient(), dateTime = "", description = "", seriesCount = 0) {
        super(id, patient, dateTime, description);
        this.seriesCount = seriesCount;
    }

    loadJson(json) {
        super.loadJson(json);
        this.seriesCount = json.seriesCount;
    }
}