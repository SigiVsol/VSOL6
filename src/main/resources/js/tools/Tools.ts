export class Tools {
    public static readFromStorage(key : string, defaultValue : string = null) {
        let result = sessionStorage.getItem(key);
        if (result == null) result = localStorage.getItem(key);

        if (result == null) return defaultValue;
        else return result;
    }

    public static saveInStorage(key, value, local) {
        if (local) {
            localStorage.setItem(key, value);
        } else {
            sessionStorage.setItem(key, value);
        }
    }

    public static clearStorage(key) {
        localStorage.removeItem(key);
        sessionStorage.removeItem(key);
    }

    public static getUrlPage() {
        return window.location.pathname.substring(1);
    }

    public static getUrlParameter(key) {
        return this.getUrlParameters()[key];
    }

    private static getUrlParameters() {
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

    public static matches(filter = "") {
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

    public static getParameterString(map : Map<string, string>) : string {
        let result = "";

        map.forEach((value, key) => {
            result += result == "" ? "?" : "&";
            result += key + "=" + value;
        });

        return result;
    }

    public static isFromVsol6App() {
        return navigator.userAgent.includes("VSOL6");
    }

}