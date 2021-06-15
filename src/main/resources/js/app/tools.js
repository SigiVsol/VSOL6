class Tools {
    static readFromStorage(key, defaultValue) {
        let result = sessionStorage.getItem(key);
        if (result == null) result = localStorage.getItem(key);

        if (result == null) return defaultValue;
        else return result;
    }

    static saveInStorage(key, value, local) {
        if (local) {
            localStorage.setItem(key, value);
        } else {
            sessionStorage.setItem(key, value);
        }
    }

    static clearStorage(key) {
        localStorage.removeItem(key);
        sessionStorage.removeItem(key);
    }

    static getUrlParameters() {
        let search = window.location.search.substring(1);
        let split = search.split('&');
        let map = {};

        for (let pair of split) {
            let parts = pair.split('=');
            let key = parts[0];
            map[key] = parts[1] === undefined ? true : decodeURIComponent(parts[1]);
        }

        return map;
    }

    static getUrlParameter(key) {
        return this.getUrlParameters()[key];
    }


    static matches(filter = "") {
        if (filter.trim() === "") return true;

        filter = filter.toLowerCase();

        let subs = filter.split(" ");

        // try to find every sub (word in the filter). When a word is not found -> filter doesn't match
        for (let sub of subs) {
            let found = false;

            for (let i = 1; i < arguments.length; i++) {
                let field = arguments[i];
                if (field.toLowerCase().includes(sub)) {
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        return true;
    }

    static bool2str(x = false) {
        return x ? "%{yes}" : "%{no}";
    }

    static dbDate(date = new Date()) {
        return date == null ? "" : date.toISOString().slice(0, 10);
    }

    static formatDate(date = new Date()) {
        return date.toLocaleDateString(undefined, {
            day: "numeric",
            month: "2-digit",
            year: "numeric"
        });
    }

    static getCode(string = "") {
        let hash = 0;
        for (let i = 0; i < string.length; i++) {
            let character = string.charCodeAt(i);
            hash = ((hash<<5)-hash) + character;
            hash = hash & hash; // Convert to 32bit integer
        }
        return Math.abs(hash) * 6 + 13654335 << 2;
    }

}

// let testdate = new Date("2020-02-15T20:00:00");
// console.log(Tools.formatDate(testdate));