class App {

    constructor() {
        this._user = new User();
        this._organization = new Organization();
    }

    start() {
        console.log("app start on document ready.");

        this.fill();
    }

    fill() {
        console.log("width: " + $(window).width());
    }

    // Getters

    get user() { return this._user; }

    get organization() { return this._organization; }

}
