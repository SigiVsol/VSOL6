import { Content } from "./Content.js";
import { ExplorerTable } from "./ExplorerTable.js";
import { ExplorerFiche } from "./ExplorerFiche.js";
export class Explorer extends Content {
    constructor(app) {
        super(app, "divExplorer");
        this.table = new ExplorerTable(this);
        this.fiche = new ExplorerFiche(this);
        this.filterDelay = null;
        $("#divExplorer .btn-home").click(() => this.setTab(null));
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
        $("#divExplorer .div-explorer-breadcrumb-bar .clients").click(() => {
            this.app.setPatient(null);
            this.app.pushHistory();
        });
    }
    fill() {
        clearTimeout(this.filterDelay);
        super.show();
        $("#divExplorer .div-explorer-main-toggle-bar").css("display", this.app.getClient() == null && this.app.getPatient() == null ? "block" : "none");
        $("#divExplorer .div-explorer-add-bar button").css("display", "none");
        $("#divExplorer .div-explorer-main-toggle-bar button").prop("disabled", false);
        $("#divExplorer .div-explorer-breadcrumb-bar .clients").css("display", this.app.getClient() == null ? "none" : "inline-block");
        $("#divExplorer .div-explorer-breadcrumb-bar .patients").css("display", this.app.getPatient() == null ? "none" : "inline-block");
        if (this.app.getClient() != null)
            $("#divExplorer .span-client").text(this.app.getClient().getName());
        if (this.app.getPatient() != null)
            $("#divExplorer .span-patient").text(this.app.getPatient().getName());
        this.fiche.fill();
        this.table.fill();
    }
    resize() {
        this.fiche.resize();
        this.table.resize();
    }
    setTab(tab) {
        this.app.setTab(tab);
        this.app.setClient(null);
        this.app.setPatient(null);
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
        this.app.setTab(null);
        this.app.setPatient(null);
        this.app.setClient(client);
        this.app.pushHistory();
    }
    openPatient(patient) {
        this.app.setTab(null);
        this.app.setPatient(patient);
        this.app.setClient(patient.getClient());
        this.app.pushHistory();
    }
    openStudy(study) {
        console.log("TODO: open study " + study.toString()); // TODO
    }
    // Getters
    getApp() { return this.app; }
}
