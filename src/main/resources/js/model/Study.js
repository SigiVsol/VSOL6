import { VsolRecord } from "./VsolRecord.js";
import { Patient } from "./Patient.js";
export class Study extends VsolRecord {
    constructor(id = null, patient = new Patient(), dateTime = new Date(), description = "", seriesCount = 0) {
        super(id);
        this.patient = patient;
        this.dateTime = dateTime;
        this.description = description;
        this.seriesCount = seriesCount;
    }
    static from(src) {
        return src == null ? null : new Study(src.id, Patient.from(src.patient), src.dateTime, src.description, src.seriesCount);
    }
    static fromRows(rows) {
        let result = [];
        for (let row of rows) {
            result.push(Study.from(row));
        }
        return result;
    }
    // Getters
    getPatient() { return this.patient; }
    getClient() { return this.patient == null ? null : this.patient.getClient(); }
    getDateTime() { return this.dateTime; }
    getDescription() { return this.description; }
    getSeriesCount() { return this.seriesCount; }
}
