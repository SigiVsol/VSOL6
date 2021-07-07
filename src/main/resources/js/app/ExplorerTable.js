import { API } from "../tools/API.js";
import { Client } from "../model/Client.js";
import { Patient } from "../model/Patient.js";
import { Study } from "../model/Study.js";
export class ExplorerTable {
    constructor(explorer) {
        this.sortField = "";
        this.sortAsc = true;
        this.explorer = explorer;
        this.app = explorer.getApp();
        $("#divExplorerTable .th-clients-name").click(() => this.setSort("name"));
        $("#divExplorerTable .th-clients-via").click(() => this.setSort("via"));
        $("#divExplorerTable .th-clients-address").click(() => this.setSort("address"));
        $("#divExplorerTable .th-patients-name").click(() => this.setSort("name"));
        $("#divExplorerTable .th-patients-origin").click(() => this.setSort("origin"));
        $("#divExplorerTable .th-patients-reference").click(() => this.setSort("reference"));
    }
    fill() {
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
    setSort(field) {
        if (this.sortField == field) {
            if (this.sortAsc)
                this.sortAsc = false;
            else {
                this.sortField = "";
                this.sortAsc = true;
            }
        }
        else {
            this.sortField = field;
            this.sortAsc = true;
        }
        this.explorer.fill();
    }
    createUrl(request) {
        let filter = $("#divExplorer .txt-filter").val();
        return "api/organizations/" + this.app.getOrganization().getId() + "/" + request + "?filter=" + filter + "&sortField=" + this.sortField + "&sortAsc=" + this.sortAsc;
    }
    fillClients() {
        $("#divExplorerTable .explorer-table").css("display", "none");
        let tbody = $("<tbody></tbody>");
        API.getJson(this.createUrl("clients"), json => {
            this.explorer.setNumRows(json.availableRows);
            for (let client of Client.fromRows(json.rows)) {
                let tr = $("<tr></tr>");
                $("<td><label><input type='checkbox'></label>").appendTo(tr);
                $("<td>" + client.getName() + "</td>").appendTo(tr);
                $("<td>" + client.getVia() + "</td>").appendTo(tr);
                $("<td>" + client.getAddress() + "</td>").appendTo(tr);
                $("<td>" + this.getClientActionButtons() + "</td>").appendTo(tr);
                tr.appendTo(tbody);
            }
            $("#divExplorer .table-clients tbody").replaceWith(tbody);
            $("#divExplorer .table-clients").css("display", "block");
        });
    }
    getClientActionButtons() {
        let result = "";
        result += "<button><img src='icon/open/16'></button>";
        result += "<button><img src='icon/delete/16'></button>";
        return result;
    }
    fillPatients() {
        let tbody = "";
        $("#divExplorer .explorer-table").css("display", "none");
        API.getJson(this.createUrl("patients"), json => {
            this.explorer.setNumRows(json.availableRows);
            for (let patient of Patient.fromRows(json.rows)) {
                tbody += "<tr>";
                tbody += "<td><label><input type='checkbox'></label>";
                tbody += "<td>" + patient.getName() + "</td>";
                tbody += "<td>" + patient.getOrigin() + "</td>";
                tbody += "<td>" + patient.getReference() + "</td>";
                tbody += "<td>" + this.getPatientsActionButtons() + "</td>";
                tbody += "</tr>\n";
            }
            $("#divExplorer .table-patients").css("display", "block");
            $("#divExplorer .table-patients tbody").html(tbody);
        });
    }
    getPatientsActionButtons() {
        let result = "";
        result += "<button><img src='icon/edit/16'></button>";
        result += "<button><img src='icon/delete/16'></button>";
        return result;
    }
    fillStudies() {
        let tbody = "";
        $("#divExplorer .explorer-table").css("display", "none");
        API.getJson(this.createUrl("studies"), json => {
            this.explorer.setNumRows(json.availableRows);
            for (let study of Study.fromRows(json.rows)) {
                tbody += "<tr>";
                tbody += "<td><label><input type='checkbox'></label>";
                tbody += "<td>" + study.getClient().getName() + "</td>";
                tbody += "<td>" + study.getPatient().getName() + "</td>";
                tbody += "<td>" + study.getDateTime() + "</td>";
                tbody += "<td>" + study.getDescription() + "</td>";
                tbody += "<td>" + study.getSeriesCount() + "</td>";
                tbody += "<td>" + this.getStudiesActionButtons() + "</td>";
                tbody += "</tr>\n";
            }
            $("#divExplorer .table-studies").css("display", "block");
            $("#divExplorer .table-studies tbody").html(tbody);
        });
    }
    getStudiesActionButtons() {
        let result = "";
        result += "<button><img src='icon/eye/16'></button>";
        result += "<button><img src='icon/delete/16'></button>";
        return result;
    }
}