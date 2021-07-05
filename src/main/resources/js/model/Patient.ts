import {VsolRecord} from "./VsolRecord.js";
import {Client} from "./Client.js";
import {Sex} from "./Sex.js";

export class Patient extends VsolRecord {

    private readonly client : Client;
    private readonly name : string;
    private readonly birthdate : Date;
    private readonly chip : string;
    private readonly ueln : string;
    private readonly species : string;
    private readonly breed : string;
    private readonly sire : string;
    private readonly damsire : string;
    private readonly neutered : boolean;
    private readonly sex : Sex;
    private readonly color : string;

    constructor(id : string = null, client = new Client(), name = "", birthdate = new Date(), chip = "", ueln = "", species = "", breed = "", sire = "", damsire = "", neutered = false, sex = Sex.X, color = "") {
        super(id);
        this.client = client;
        this.name = name;
        this.birthdate = birthdate;
        this.chip = chip;
        this.ueln = ueln;
        this.species = species;
        this.breed = breed;
        this.sire = sire;
        this.damsire = damsire;
        this.neutered = neutered;
        this.sex = sex;
        this.color = color;
    }

    public static from(src : any) {
        return src == null ? null : new Patient(src.id, Client.from(src.client), src.name, src.birthdate, src.chip, src.ueln, src.species, src.breed, src.sire, src.damsire, src.neutered, src.sex, src.color);
    }

    getOrigin() {
        if (this.sire.trim() === "" && this.damsire.trim() === "") {
            return "";
        } else if (this.sire.trim() === "") {
            return this.damsire;
        } else if (this.damsire.trim() === "") {
            return this.sire;
        } else {
            return this.sire + " x " + this.damsire;
        }
    }

    getReference() {
        if (this.chip.trim() === "" && this.ueln.trim() === "") {
            return "";
        } else if (this.chip.trim() === "") {
            return this.ueln;
        } else if (this.ueln.trim() === "") {
            return this.chip;
        } else {
            return this.chip + " - " + this.ueln;
        }
    }

    getSexString() {
        if (this.sex === Sex.M) return "♂";
        else if (this.sex === Sex.F) return "♀";
        else return "?";
    }

    toString() {
        return this.name;
    }

    // Getters

    getClient() { return this.client; }
    getName() { return this.name; }
    getBirthdate() { return this.birthdate; }
    getChip() { return this.chip; }
    getUeln() { return this.ueln; }
    getSpecies() { return this.species; }
    getBreed() { return this.breed; }
    getSire() { return this.sire; }
    getDamsire() { return this.damsire; }
    isNeutered() { return this.neutered; }
    getSex() { return this.sex; }
    getColor() { return this.color; }

}