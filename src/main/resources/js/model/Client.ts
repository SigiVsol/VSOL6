import {VsolRecord} from "./VsolRecord.js";
import {Language} from "./Language.js";

export class Client extends VsolRecord {

    private lastName : string;
    private firstName : string;
    private company : string;
    private via : string;
    private language : Language;
    private phone : string;
    private email : string;
    private street : string;
    private postal : string;
    private city : string;
    private country : string;
    private extraInfo : string;

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

    loadJson(json) {
        this.id = json.id;
        this.lastName = json.lastName;
        this.firstName = json.firstName;
        this.company = json.company;
        this.via = json.via;
        this.language = json.language;
        this.phone = json.phone;
        this.email = json.email;
        this.street = json.street;
        this.postal = json.postal;
        this.city = json.city;
        this.country = json.country;
        this.extraInfo = json.extraInfo;
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
