import {Explorer} from "./Explorer.js";
import {App} from "../App.js";

export class ExplorerFiche {
    private explorer : Explorer;
    private app : App;

    constructor(explorer : Explorer) {
        this.explorer = explorer;
        this.app = explorer.getApp();
    }

    public fill() : void {
        $("#divExplorerFiche").css("display", this.app.getClient() == null && this.app.getPatient() == null ? "none" : "block");
        $(".div-explorer-fiche").css("display", "none");

        if (this.app.getPatient() != null) {
            $(".div-patient-fiche").css("display", "block");
        } else if (this.app.getClient() != null) {
            $(".div-client-fiche").css("display", "block");
        }
    }

    public resize() : void {

    }

}