class Address {
    constructor(street = "", postal = "", city = "", country = "") {
        this.street = street;
        this.postal = postal;
        this.city = city;
        this.country = country;
    }

    loadJson(json) {
        this.street = json.street;
        this.postal = json.postal;
        this.city = json.city;
        this.country = json.country;
    }

    getText() {
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
}