import { Content } from "./Content.js";
export class Explorer extends Content {
    constructor(app) {
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
    fill() {
        super.show();
        let client = this.app.getClient();
        let patient = this.app.getPatient();
    }
}
