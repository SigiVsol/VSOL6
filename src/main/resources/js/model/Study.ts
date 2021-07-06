import {VsolRecord} from "./VsolRecord.js";
import {Patient} from "./Patient.js";

export class Study extends VsolRecord {

    private readonly patient : Patient;
    private readonly dateTime : Date;
    private readonly description : string;
    private readonly seriesCount : number;

    public constructor(id : string = null, patient = new Patient(), dateTime = new Date(), description = "", seriesCount = 0) {
        super(id);
        this.patient = patient;
        this.dateTime = dateTime;
        this.description = description;
        this.seriesCount = seriesCount;
    }

    public static from(src : any) {
        return src == null ? null : new Study(src.id, Patient.from(src.patient), src.dateTime, src.description, src.seriesCount);
    }

    public static fromRows(rows) {
        let result : Study[] = [];
        for (let row of rows) {
            result.push(Study.from(row));
        }
        return result;
    }

    // Getters

    public getPatient() { return this.patient; }
    public getClient() { return this.patient == null ? null : this.patient.getClient(); }
    public getDateTime() { return this.dateTime; }
    public getDescription() { return this.description; }
    public getSeriesCount() { return this.seriesCount; }

}