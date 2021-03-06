export class Tools {
    static readFromStorage(key, defaultValue = null) {
        let result = sessionStorage.getItem(key);
        if (result == null)
            result = localStorage.getItem(key);
        if (result == null)
            return defaultValue;
        else
            return result;
    }
    static saveInStorage(key, value, local) {
        if (local) {
            localStorage.setItem(key, value);
        }
        else {
            sessionStorage.setItem(key, value);
        }
    }
    static clearStorage(key) {
        localStorage.removeItem(key);
        sessionStorage.removeItem(key);
    }
    static getUrlPage() {
        return window.location.pathname.substring(1);
    }
    static getUrlParameter(key) {
        return this.getUrlParameters()[key];
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
    static matches(filter = "") {
        if (filter.trim() === "")
            return true;
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
            if (!found)
                return false;
        }
        return true;
    }
    static getParameterString(map) {
        let result = "";
        map.forEach((value, key) => {
            result += result == "" ? "?" : "&";
            result += key + "=" + value;
        });
        return result;
    }
    static isFromVsol6App() {
        return navigator.userAgent.includes("VSOL6");
    }
}
