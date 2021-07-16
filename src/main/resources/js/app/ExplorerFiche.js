export class ExplorerFiche {
    constructor(explorer) {
        this.explorer = explorer;
        this.app = explorer.getApp();
    }
    fill() {
        $("#divExplorerFiche").css("display", this.app.getClient() == null && this.app.getPatient() == null ? "none" : "block");
        $(".div-explorer-fiche").css("display", "none");
        if (this.app.getPatient() != null) {
            this.fillPatient();
            // this.fillClient();
        }
        else if (this.app.getClient() != null) {
            this.fillClient();
        }
    }
    resize() {
        let hidden = this.app.getClient() == null && this.app.getPatient() == null;
        $("#divExplorer .div-explorer-fiche-zone").width(hidden ? 0 : "30%").css("min-width", hidden ? 0 : 250);
    }
    fillClient() {
        $(".div-client-fiche").css("display", "block");
        let client = this.app.getClient();
        $("#divExplorerFiche .div-client-fiche .span-name").text(client.getName());
        $("#divExplorerFiche .div-client-fiche .span-company").text(client.getCompany());
        $("#divExplorerFiche .div-client-fiche .span-via").text(client.getVia());
        $("#divExplorerFiche .div-client-fiche .span-language").text(client.getLanguage());
        $("#divExplorerFiche .div-client-fiche .span-phone").text(client.getPhone());
        $("#divExplorerFiche .div-client-fiche .span-email").text(client.getEmail());
        $("#divExplorerFiche .div-client-fiche .span-address").text(client.getAddress());
        $("#divExplorerFiche .div-client-fiche .div-extra-info").text(client.getExtraInfo());
    }
    fillPatient() {
        $(".div-patient-fiche").css("display", "block");
        let patient = this.app.getPatient();
        $("#divExplorerFiche .div-patient-fiche .span-name").text(patient.getName());
        $("#divExplorerFiche .div-patient-fiche .span-birthdate").text(String(patient.getBirthdate()));
        $("#divExplorerFiche .div-patient-fiche .span-chip").text(patient.getChip());
        $("#divExplorerFiche .div-patient-fiche .span-ueln").text(patient.getUeln());
        $("#divExplorerFiche .div-patient-fiche .span-species").text(patient.getSpecies());
        $("#divExplorerFiche .div-patient-fiche .span-breed").text(patient.getBreed());
        $("#divExplorerFiche .div-patient-fiche .span-sire").text(patient.getSire());
        $("#divExplorerFiche .div-patient-fiche .span-damsire").text(patient.getDamsire());
        $("#divExplorerFiche .div-patient-fiche .span-neutered").text(patient.isNeutered());
        $("#divExplorerFiche .div-patient-fiche .span-sex").text(patient.getSex());
        $("#divExplorerFiche .div-patient-fiche .span-color").text(patient.getColor());
    }
}
