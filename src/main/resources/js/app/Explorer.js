import { Content } from "./Content.js";
import { Dialog } from "../popup/Dialog.js";
export class Explorer extends Content {
    constructor(app) {
        super(app, "divExplorer");
        $("#tglClients").click(() => {
            this.app.setTab("clients");
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
        $("#btnTestInform").click(() => {
            Dialog.inform("Information.");
        });
        $("#btnTestConfirm").click(() => {
            Dialog.confirm("Are you sure?", () => {
                console.log("I am sure");
            }, () => {
                console.log("I am not sure");
            });
        });
        $("#btnTestGetString").click(() => {
            Dialog.getString("What's your name?", "", (result) => {
                console.log("Your name is " + result + ".");
            });
        });
        $("#btnTestGetNumber").click(() => {
            Dialog.getNumber("What's your birth year?", 2021, (result) => {
                console.log("Your age is " + (2021 - result) + ".");
            });
        });
    }
    fill() {
        super.show();
        let client = this.app.getClient();
        let patient = this.app.getPatient();
    }
}
