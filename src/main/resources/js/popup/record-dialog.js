class RecordDialog {

    static getOrganization(initial = new Organization(), callback = function() {}) {
        RecordDialog.show("organizations", "%{Select_an_organization}.", "organizations", initial, callback);
    }

    static getClient(initial = new Client(), callback = function() {}) {
        RecordDialog.show("clients", "%{Select_a_client}.", "user", initial, callback);
    }

    static getPatient(initial = new Patient(), callback = function() {}) {
        RecordDialog.show("patients", "%{Select_a_client}.", "open", initial, callback);
    }

    static show(type = "", text = "", icon = "open", initial = new VsolRecord(), callback = function() {}) {
        RecordDialog.type = type;
        RecordDialog.callback = callback;
        RecordDialog.initial = initial;
        RecordDialog.icon = icon;
        RecordDialog.filter = "";

        $(".popup-record-dialog span").text(text);
        $(".popup-record-dialog").css("display", "block");
        $(".popup-record-dialog input").trigger("focus").on("keyup", event => {
            if (event.key === "Enter") RecordDialog.applyFilter();
            else if (event.key === "Escape") RecordDialog.close();
            else if (event.key !== "Shift" && event.key !== "CapsLock" && event.key !== "Control" && event.key !== "Alt" && event.key !== "AltGraph"
                && event.key !== "ArrowUp" && event.key !== "ArrowDown" && event.key !== "ArrowLeft" && event.key !== "ArrowRight") {
                clearTimeout(this.filterDelay);
                this.filterDelay = setTimeout(() => RecordDialog.applyFilter(), 500);
            }
        });

        RecordDialog.fill();
    }

    static applyFilter() {
        let filter = $(".popup-record-dialog input").val().trim();

        if (RecordDialog.filter.trim() !== filter) {
            RecordDialog.filter = filter;
            RecordDialog.fill();
        }
    }

    static fill() {
        let table = $(".popup-record-dialog table");
        table.html("");

        let url = "";
        if (RecordDialog.type === "organizations") {
            url = "api/organizations?username=" + app.user.username + "&filter=" + RecordDialog.filter;
        } else if (RecordDialog.type === "clients") {
            url = "api/organizations/" + app.organization.id + "/clients?filter=" + RecordDialog.filter + "&sortField=name";
        } else if (RecordDialog.type === "patients") {
            url = "api/organizations/" + app.organization.id + "/patients?filter=" + RecordDialog.filter + "&sortField=name";
        }

        $.get({
            url: url,
            success: json => {
                for (let jsonRecord of json.rows) {
                    let record;
                    if (RecordDialog.type === "organizations") {
                        record = new Organization();
                    } else if (RecordDialog.type === "clients") {
                        record = new Client();
                    } else if (RecordDialog.type === "patients") {
                        record = new Patient();
                    }
                    record.loadJson(jsonRecord);

                    let string = record.toString();

                    let tr = $("<tr></tr>");
                    let td = $("<td style='width: 1px'></td>").appendTo(tr);
                    let button = $("<button class='ok'><img src='icon/" + RecordDialog.icon + "/16'></button>");

                    button.appendTo(td);
                    button.on("click", () => this.ok(record));
                    td.appendTo(tr);

                    if (record.id === this.initial.id) {
                        button.prop("disabled", true);

                        td = $("<td>" + string + "</td>");
                        td.appendTo(tr);
                    } else {
                        td = $("<td class='hand'>" + string + "</td>");
                        td.on("click", () => this.ok(record));
                        td.appendTo(tr);
                    }

                    tr.appendTo(table);
                }
            }
        });
    }

    static ok(record = new VsolRecord()) {
        if (RecordDialog.callback != null) {
            RecordDialog.callback(record);
        }
        this.close();
    }

    static close() {
        $(".popup-record-dialog input").val("").off("keyup");
        $(".popup-record-dialog").css("display", "none");
        RecordDialog._callback = null;
    }

}