export class ExplorerFiche {
    constructor(explorer) {
        this.explorer = explorer;
        this.app = explorer.getApp();
    }
    fill() {
        $("#divExplorerFiche").css("display", this.app.getClient() == null && this.app.getPatient() == null ? "none" : "block");
        $(".div-explorer-fiche").css("display", "none");
        if (this.app.getPatient() != null) {
            $(".div-patient-fiche").css("display", "block");
        }
        else if (this.app.getClient() != null) {
            $(".div-client-fiche").css("display", "block");
        }
    }
    resize() {
    }
}
