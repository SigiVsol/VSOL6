import { Content } from "./Content.js";
import { ExplorerTable } from "./ExplorerTable.js";
export class Explorer extends Content {
    constructor(app) {
        super(app, "divExplorer");
        this.table = new ExplorerTable(this);
        // private fiche = new ExplorerFiche(this); // TODO
        this.filterDelay = null;
        $("#divExplorer .tgl-clients").click(() => this.setTab(null));
        $("#divExplorer .tgl-patients").click(() => this.setTab("patients"));
        $("#divExplorer .tgl-studies").click(() => this.setTab("studies"));
        $("#divExplorer .btn-search").click(() => this.fill());
        $("#divExplorer .btn-clear").click(() => this.clearFilter());
        $("#divExplorer .txt-filter").on("keyup", event => {
            if (event.key == "Enter")
                this.fill();
            else if (event.key == "Escape")
                this.clearFilter();
            else if (event.key !== "Shift" && event.key !== "CapsLock" && event.key !== "Control" && event.key !== "Alt" && event.key !== "AltGraph" && event.key !== "ArrowUp" && event.key !== "ArrowDown" && event.key !== "ArrowLeft" && event.key !== "ArrowRight") {
                clearTimeout(this.filterDelay);
                this.filterDelay = setTimeout(() => this.fill(), 500);
            }
        });
    }
    fill() {
        clearTimeout(this.filterDelay);
        super.show();
        this.table.fill();
    }
    setTab(tab) {
        this.app.setTab(tab);
        this.app.pushHistory();
    }
    setNumRows(numRows) {
        $("#divExplorer .lbl-num-rows").text(numRows);
    }
    clearFilter() {
        $("#divExplorer .txt-filter").val("").select();
        this.fill();
    }
    openClient(client) {
        console.log("TODO: open client " + client.toString()); // TODO
    }
    openPatient(patient) {
        console.log("TODO: open patient " + patient.toString()); // TODO
    }
    openStudy(study) {
        console.log("TODO: open study " + study.toString()); // TODO
    }
    // Getters
    getApp() { return this.app; }
}
