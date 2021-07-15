import {API} from "../tools/API.js";
import {Client} from "../model/Client.js";
import {Patient} from "../model/Patient.js";
import {Study} from "../model/Study.js";
import {Explorer} from "./Explorer.js";
import {App} from "../App.js";
import {Dialog} from "../popup/Dialog.js";

export class ExplorerTable {

    private explorer : Explorer;
    private app : App;

    private sortField = "";
    private sortAsc = true;

    constructor(explorer : Explorer) {
        this.explorer = explorer;
        this.app = explorer.getApp();

        $("#divExplorerTable .th-clients-name").click(() => this.setSort("name"));
        $("#divExplorerTable .th-clients-via").click(() => this.setSort("via"));
        $("#divExplorerTable .th-clients-address").click(() => this.setSort("address"));

        $("#divExplorerTable .th-patients-name").click(() => this.setSort("name"));
        $("#divExplorerTable .th-patients-origin").click(() => this.setSort("origin"));
        $("#divExplorerTable .th-patients-reference").click(() => this.setSort("reference"));
    }

    public fill() : void {
        if (this.app.getPatient() != null) {
            this.fillStudies();
        } else if (this.app.getClient() != null) {
            this.fillPatients();
        } else {
            switch (this.app.getTab()) {
                case "patients":
                    this.fillPatients();
                    break;
                case "studies":
                    this.fillStudies();
                    break;
                default:
                    this.fillClients();
            }
        }
    }

    public resize() : void {
        let height = this.app.getHeight() - $(".div-explorer-above-table").height();

        $("#divExplorerTable").css("height", height + "px");
    }

    private setSort(field : string) {
        if (this.sortField == field) {
            if (this.sortAsc) this.sortAsc = false;
            else {
                this.sortField = "";
                this.sortAsc = true;
            }
        } else {
            this.sortField = field;
            this.sortAsc = true;
        }
        this.explorer.fill();
    }

    private fillClients() : void {
        $("#divExplorerTable .explorer-table").css("display", "none");
        $(".btn-add-client").css("display", "inline-block");
        $(".tgl-clients").prop("disabled", true);

        let tbody = $("<tbody></tbody>");

        API.getJson(this.url("clients"), json => {
            this.explorer.setNumRows(json.availableRows);

            for (let client of Client.fromRows(json.rows)) {
                let tr = $("<tr></tr>");

                tr.append(this.tdCheckbox());
                tr.append(this.tdText(client.getName(), () => this.explorer.openClient(client)));
                tr.append(this.tdText(client.getVia()));
                tr.append(this.tdText(client.getAddress()));
                tr.append(this.tdClientActionButtons(client));

                tbody.append(tr);
            }

            $("#divExplorer .table-clients tbody").replaceWith(tbody);
            $("#divExplorer .table-clients").css("display", "table");
        });
    }

    private fillPatients() : void {
        $("#divExplorerTable .explorer-table").css("display", "none");
        if (this.app.getClient() != null) $(".btn-add-patient").css("display", "inline-block");
        $(".tgl-patients").prop("disabled", true);

        let tbody = $("<tbody></tbody>");

        let request = this.app.getClient() == null ? "patients" : "clients/" + this.app.getClient().getId() + "/patients";

        API.getJson(this.url(request), json => {
            this.explorer.setNumRows(json.availableRows);

            for (let patient of Patient.fromRows(json.rows)) {
                let tr = $("<tr></tr>");

                tr.append(this.tdCheckbox());
                tr.append(this.tdText(patient.getName(), () => this.explorer.openPatient(patient)));
                tr.append(this.tdText(patient.getOrigin()));
                tr.append(this.tdText(patient.getReference()));
                tr.append(this.tdPatientsActionButtons(patient));

                tbody.append(tr);
            }

            $("#divExplorer .table-patients tbody").replaceWith(tbody);
            $("#divExplorer .table-patients").css("display", "table");
        });
    }

    private fillStudies() : void {
        $("#divExplorerTable .explorer-table").css("display", "none");
        if (this.app.getPatient() != null) $(".btn-add-study").css("display", "inline-block");
        $(".tgl-studies").prop("disabled", true);

        let tbody = $("<tbody></tbody>");

        API.getJson(this.url("studies"), json => {
            this.explorer.setNumRows(json.availableRows);

            for (let study of Study.fromRows(json.rows)) {
                let tr = $("<tr></tr>");

                tr.append(this.tdCheckbox());
                tr.append(this.tdText(study.getClient().getName()));
                tr.append(this.tdText(study.getPatient().getName()));
                tr.append(this.tdText(String(study.getDateTime())));
                tr.append(this.tdText(study.getDescription()));
                tr.append(this.tdText(String(study.getSeriesCount())));
                tr.append(this.tdStudiesActionButtons(study));

                tbody.append(tr);
            }

            $("#divExplorer .table-studies tbody").replaceWith(tbody);
            $("#divExplorer .table-studies").css("display", "table");
        });
    }

    private url(request : string) {
        let filter = $("#divExplorer .txt-filter").val();
        return "api/organizations/" + this.app.getOrganization().getId() + "/" + request + "?filter=" + filter + "&sortField=" + this.sortField + "&sortAsc=" + this.sortAsc
    }

    private tdText(text : string, callback : () => void = null) : JQuery {
        let td = $("<td>" + text + "</td>");
        if (callback != null) {
            td.addClass("pointer");
            td.click(callback);
        }
        return td;
    }

    private tdCheckbox() : JQuery {
        return $("<td><label><input type='checkbox'></label>");
    }

    private tdClientActionButtons(client : Client) : JQuery {
        let td = $("<td></td>");
        let div = $("<div class='nowrap'></div>");

        div.append($("<button><img src='icon/open/16'></button>").click(() => this.explorer.openClient(client)));
        div.append("&nbsp;");
        div.append($("<button><img src='icon/delete/16'></button>").click(() => {
            Dialog.confirm("Are you sure?", () => {
                console.log("delete client " + client.getName()); // TODO
            });
        }));

        td.append(div);
        return td;
    }

    private tdPatientsActionButtons(patient : Patient) : JQuery {
        let td = $("<td></td>");
        let div = $("<div class='nowrap'></div>");

        div.append($("<button><img src='icon/open/16'></button>").click(() => this.explorer.openPatient(patient)));
        div.append("&nbsp;");
        div.append($("<button><img src='icon/delete/16'></button>").click(() => {
            Dialog.confirm("Are you sure?", () => {
                console.log("delete patient " + patient.getName()); // TODO
            });
        }));

        td.append(div);
        return td;
    }

    private tdStudiesActionButtons(study : Study) : JQuery {
        let td = $("<td></td>");
        let div = $("<div class='nowrap'></div>");

        div.append($("<button><img src='icon/eye/16'></button>").click(() => this.explorer.openStudy(study)));
        div.append("&nbsp;");
        div.append($("<button><img src='icon/delete/16'></button>").click(() => {
            Dialog.confirm("Are you sure?", () => {
                console.log("delete study " + study.getDescription()); // TODO
            });
        }));

        td.append(div);
        return td;
    }

}