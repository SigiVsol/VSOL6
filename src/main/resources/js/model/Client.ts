import {VsolRecord} from "./VsolRecord.js";
import {Language} from "./Language.js";

export class Client extends VsolRecord {

    private readonly lastName : string;
    private readonly firstName : string;
    private readonly company : string;
    private readonly via : string;
    private readonly language : Language;
    private readonly phone : string;
    private readonly email : string;
    private readonly street : string;
    private readonly postal : string;
    private readonly city : string;
    private readonly country : string;
    private readonly extraInfo : string;

    constructor(id : string = null, lastName : string = "", firstName = "", company = "", via = "", language = Language.en, phone = "", email = "", street = "", postal = "", city = "", country = "", extraInfo = "") {
        super(id);

        this.lastName = lastName;
        this.firstName = firstName;
        this.company = company;
        this.via = via;
        this.language = language;
        this.phone = phone;
        this.email = email;
        this.street = street;
        this.postal = postal;
        this.city = city;
        this.country = country;
        this.extraInfo = extraInfo;
    }

    public static from(src : any) {
        return src == null ? null : new Client(src.id, src.lastName, src.firstName, src.company, src.via, src.language, src.phone, src.email, src.street, src.postal, src.city, src.country, src.extraInfo);
    }

    public static fromRows(rows) {
        let result : Client[] = [];
        for (let row of rows) {
            result.push(Client.from(row));
        }
        return result;
    }

    getName() {
        return (this.lastName + " " + this.firstName).trim();
    }

    getAddress() {
        let result = (this.postal + " " + this.city).trim();
        if (this.country.trim() !== "") {
            result = (result + " (" + this.country + ")").trim();
        }

        if (this.street !== "") {
            if (result === "") result = this.street;
            else result = this.street + ", " + result;
        }

        return result;
    }

    toString() {
        return this.getName();
    }

    // Getters

    getLastName() { return this.lastName; }
    getFirstName() { return this.firstName; }
    getCompany() { return this.company; }
    getVia() { return this.via; }
    getLanguage() { return this.language; }
    getPhone() { return this.phone; }
    getEmail() { return this.email; }
    getStreet() { return this.street; }
    getPostal() { return this.postal; }
    getCity() { return this.city; }
    getCountry() { return this.country; }
    getExtraInfo() { return this.extraInfo; }

}
