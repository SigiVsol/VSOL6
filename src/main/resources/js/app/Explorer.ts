import {Content} from "./Content.js";
import {App} from "../App.js";
import {Client} from "../model/Client.js";
import {Patient} from "../model/Patient.js";

export class Explorer extends Content {

    constructor(app : App) {
        super(app, "divExplorer");

        $("#tglClients").click(() => {
            this.app.setTab(null);
            this.app.pushHistory();
        });

        $("#tglPatients").click(() => {
            this.app.setTab("patients");
            this.app.pushHistory();
        });

        $("#tglStudies").click(() => {
            this.app.setTab("studies");
            this.app.pushHistory();
        });

    }

    public fill() : void {
        super.show();

        let client = this.app.getClient();
        let patient = this.app.getPatient();


    }

}
