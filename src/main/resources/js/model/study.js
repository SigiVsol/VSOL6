class Study extends Entry {
    constructor(id = null, patient = new Patient(), dateTime = "", description = "", seriesCount = 0) {
        super(id, patient, dateTime, description);
        this.seriesCount = seriesCount;

        this.uid = "";

        this.index = 0;
        this.hidden = false;
        this.color = "#fff";
        this.series = [];
    }

    loadJson(json) {
        super.loadJson(json);
        this.seriesCount = json.seriesCount;
        this.uid = json.uid;
    }
}