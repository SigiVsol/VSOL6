class Client extends VsolRecord {
    constructor(id = null, lastName = "", firstName = "", company = "", via = "", language = "en",
                phone = "", email = "", address = new Address(), extraInfo = "") {
        super(id);

        this.lastName = lastName;
        this.firstName = firstName;
        this.company = company;
        this.via = via;
        this.language = language;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.extraInfo = extraInfo;
    }

    loadJson(json) {
        this.id = json.id;
        this.lastName = json.lastName;
        this.firstName = json.firstName;
        this.company = json.company;
        this.via = json.via;
        this.language = json.language;
        this.phone = json.phone;
        this.email = json.email;
        this.address.loadJson(json.address);
        this.extraInfo = json.extraInfo;
    }

    getName() {
        return (this.firstName + " " + this.lastName).trim();
    }

    toString() {
        return this.getName();
    }

}