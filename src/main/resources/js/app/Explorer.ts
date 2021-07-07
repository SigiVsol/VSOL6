import {Content} from "./Content.js";
import {App} from "../App.js";
import {ExplorerTable} from "./ExplorerTable.js";

export class Explorer extends Content {

    private table = new ExplorerTable(this);
    // private fiche = new ExplorerFiche(this); // TODO
    private filterDelay = null;

    constructor(app : App) {
        super(app, "divExplorer");

        $("#divExplorer .tgl-clients").click(() => this.setTab(null));
        $("#divExplorer .tgl-patients").click(() => this.setTab("patients"));
        $("#divExplorer .tgl-studies").click(() => this.setTab("studies"));

        $("#divExplorer .btn-search").click(() => this.fill());
        $("#divExplorer .btn-clear").click(() => this.clearFilter());
        $("#divExplorer .txt-filter").on("keyup", event => {
            if (event.key == "Enter") this.fill();
            else if (event.key == "Escape") this.clearFilter();
            else if (event.key !== "Shift" && event.key !== "CapsLock" && event.key !== "Control" && event.key !== "Alt" && event.key !== "AltGraph" && event.key !== "ArrowUp" && event.key !== "ArrowDown" && event.key !== "ArrowLeft" && event.key !== "ArrowRight") {
                clearTimeout(this.filterDelay);
                this.filterDelay = setTimeout(() => this.fill(), 500);
            }
        });
    }

    public fill() : void {
        clearTimeout(this.filterDelay);
        super.show();

        this.table.fill();
    }

    private setTab(tab : string) : void {
        this.app.setTab(tab);
        this.app.pushHistory();
    }

    public setNumRows(numRows : number) {
        $("#divExplorer .lbl-num-rows").text(numRows);
    }

    private clearFilter() {
        $("#divExplorer .txt-filter").val("").select();
        this.fill();
    }

    // Getters

    public getApp() { return this.app; }



}
