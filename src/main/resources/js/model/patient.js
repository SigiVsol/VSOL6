class Patient extends VsolRecord {
    constructor(id = null, client = new Client(), name = "", birthdate = "", chip = "", ueln = "", species = "",
                breed = "", sire = "", damsire = "", neutered = false, sex = "X", color = "") {
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

    loadJson(json) {
        this.id = json.id;
        this.client.loadJson(json.client);
        this.name = json.name;
        this.birthdate = json.birthdate;
        this.chip = json.chip;
        this.ueln = json.ueln;
        this.species = json.species;
        this.breed = json.breed;
        this.sire = json.sire;
        this.damsire = json.damsire;
        this.neutered = json.neutered;
        this.sex = json.sex;
        this.color = json.color;
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
        if (this.sex === "M") return "♂";
        else if (this.sex === "F") return "♀";
        else return "?";
    }

    toString() {
        return this.name;
    }
}