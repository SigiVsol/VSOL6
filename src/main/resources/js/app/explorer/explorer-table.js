class ExplorerTable {
    constructor(type) {
        this._type = type; // clients, patients or entries

        this._filter = "";
        this._sortField = "";
        this._sortAsc = true;
        this._part = 0;
        this._availableRows = 0;
        this._loadedRows = 0;
    }

    fill() {
        this.clear();
        this.add(0);
    }

    clear() {
        $("#tbody-" + this.type).html("");
        this.loadedRows = 0;
    }

    add(part) {} // to override

    scroll() {
        let scrollPane = $(".explorer-scroll-pane");

        if (this.loadedRows < this.availableRows && scrollPane.scrollTop() >= (this.loadedRows + 1) * 55 - scrollPane.height()) { // 55: row height | +1: header
            this.add(this.part + 1);
        }
    }

    applySort(field) {
        if (this.sortField === field) {
            if (this.sortAsc) {
                this.sortAsc = false;
            } else {
                this.sortField = "";
                this.sortAsc = true;
            }
        } else {
            this.sortField = field;
            this.sortAsc = true;
        }
        this.fill();
    }

    getUrl() {
        return "api/organizations/" + app.organization.id + "/" + this.type + "?filter=" + this.filter + "&sortField=" + this.sortField + "&sortAsc=" + this.sortAsc + "&part=" + this.part;
    }

    getTdText(text, action) {
        let td = $("<td class='hand'>" + text + "</td>");
        td.on("click", () => action());

        return td;
    }

    getTdNumber(text, action) {
        let td = $("<td class='hand number'>" + text + "</td>");
        td.on("click", () => action());

        return td;
    }

    getTdDate(text, action) {
        let td = $("<td class='hand date'>" + text.replace(" ", "<br>") + "</td>");
        td.on("click", () => action());

        return td;
    }

    getTdCheckbox() {
        let td = $("<td class='checkbox'></td>");
        let label = $("<label class='checkbox " + this.type + "'></label>");
        let input = $("<input type='checkbox'>");
        input.appendTo(label);
        $("<span class='checkbox'></span>").appendTo(label);
        label.appendTo(td);

        input.on("change", () => {
            if (input.prop("checked"))
                td.parent("tr").addClass("selected");
            else
                td.parent("tr").removeClass("selected");
            this.fillSelectionModel();
        });

        return td;
    }

    // getTdButton(buttonClass = "cancel", icon = "delete", record = new VsolRecord(), action = function() {}) {
    getTdButton(buttonClass = "cancel", icon = "delete", action = function() {}) {
        let td = $("<td class='button'></td>");
        let button = $("<button class='" + buttonClass + "'></button>");
        $("<img src='icon/" + icon + "/16'>").appendTo(button);
        button.appendTo(td);
        if (action != null) {
            button.on("click", () => action());
        }

        return td;
    }

    // getTdOpenButton(icon, action) {
    //     let td = $("<td class='button'></td>");
    //     let button = $("<button class='ok'></button>");
    //     $("<img src='icon/" + icon + "/16'>").appendTo(button);
    //     button.appendTo(td);
    //
    //     button.on("click", () => action());
    //
    //     return td;
    // }

    setSortIcon(field) {
        $("#ico-sort-" + this.type + "-" + field).attr("src", this.sortField === field ? "icon/sort-" + (this.sortAsc ? "ascending" : "descending") + "/16" : "icon/sort/16").css("opacity", this.sortField === field ? 1.0 : 0.3);
    }

    postFill() {
        $("#chk-all-" + this.type).prop("disabled", this.filter.trim() === "" || this.loadedRows < 2);
        $("#thead-" + this.type).add(".filter-area").css("visibility", this.availableRows === 0 && this.filter === "" ? "hidden" : "visible");
    }

    selectAll() {
        let selected = $("#chk-all-" + this.type).prop("checked");
        $("#tbody-" + this.type + " input").prop("checked", selected);

        $("#tbody-" + this.type + " tr").each((index, element) => {
            let tr = $(element);

            if (selected) {
                tr.addClass("selected");
            } else {
                tr.removeClass("selected");
            }
        });

        this.fillSelectionModel();
    }

    fillSelectionModel() {
        let selected = 0, total = 0;

        $("#tbody-" + this.type + " tr").each((index, element) => {
            if ($(element).hasClass("selected")) selected++;
            total++;
        });

        $("#chk-all-" + this.type).prop("checked", selected === total);

        $("#thead-" + this.type + " tr th button.multi").prop("disabled", selected < 2);
        $("#tbody-" + this.type + " tr td button").prop("disabled", selected >= 2);
    }

    deleteRecord(record = new VsolRecord()) {
        Dialog.confirm("%{Are_you_sure}?", () => {
            $.ajax({
                method: "delete",
                url: "api/organizations/" + app.organization.id + "/" + this.type,
                data: JSON.stringify(record),
                contentType: "application/json",
                success: () => {
                    app.show();
                }
            });
        })
    }

    deleteSelectedRecords() {
        Dialog.confirm("%{Are_you_sure}?", () => {
            let records = [];

            $("#tbody-" + this.type + " tr").each((index, element) => {
                let tr = $(element);
                if (tr.hasClass("selected")) records.push(tr.data("record"));
            });

            $.ajax({
                method: "delete",
                url: "api/organizations/" + app.organization.id + "/" + this.type + "/multi",
                data: JSON.stringify(records),
                contentType: "application/json",
                success: () => {
                    app.show();
                }
            });
        });
    }

    get type() { return this._type; }
    get filter() { return this._filter; }
    get sortField() { return this._sortField; }
    get sortAsc() { return this._sortAsc; }
    get part() { return this._part; }
    get availableRows() { return this._availableRows; }
    get loadedRows() { return this._loadedRows; }

    set filter(value) { this._filter = value; }
    set sortField(value) { this._sortField = value; }
    set sortAsc(value) { this._sortAsc = value; }
    set part(value) { this._part = value; }
    set availableRows(value) {
        this._availableRows = value;
        $("div.filter-area span.count").text("(" + this.availableRows + ")");
    }
    set loadedRows(value) { this._loadedRows = value; }
}

class ClientsTable extends ExplorerTable {
    constructor() {
        super("clients");
    }

    add(part) {
        this.part = part;

        $.get({
            url: this.getUrl(),
            success: json => {
                for (let jsonClient of json.rows) {
                    let client = new Client();
                    client.loadJson(jsonClient);

                    let tr = $("<tr></tr>"); {
                        this.getTdCheckbox().appendTo(tr);
                        this.getTdText(client.getName(), () => app.explorer.showClient(client)).appendTo(tr);
                        this.getTdText(client.via, () => app.explorer.showClient(client)).appendTo(tr);
                        this.getTdText(client.address.getText(), () => app.explorer.showClient(client)).appendTo(tr);
                        this.getTdButton("clients", "open", () => app.explorer.showClient(client)).appendTo(tr);
                        this.getTdButton("cancel", "delete", () => this.deleteRecord(client)).appendTo(tr);

                        tr.data("record", client);
                    }

                    tr.appendTo($("#tbody-clients"));

                    this.loadedRows++;
                }
                this.availableRows = json.availableRows;
                this.postFill();
                this.fillSelectionModel();

                this.setSortIcon("name");
                this.setSortIcon("via");
                this.setSortIcon("address");
            }
        });
    }
}

class PatientsTable extends ExplorerTable {
    constructor() {
        super("patients");
    }

    add(part) {
        this.part = part;

        let success = json => {
            for (let jsonPatient of json.rows) {
                let patient = new Patient();
                patient.loadJson(jsonPatient);

                let tr = $("<tr></tr>"); {
                    this.getTdCheckbox().appendTo(tr);
                    this.getTdText(patient.name, () => app.explorer.showPatient(patient)).appendTo(tr);
                    this.getTdText(patient.getOrigin(), () => app.explorer.showPatient(patient)).appendTo(tr);
                    this.getTdText(patient.getReference(), () => app.explorer.showPatient(patient)).appendTo(tr);
                    this.getTdButton("patients", "open", () => app.explorer.showPatient(patient)).appendTo(tr);
                    this.getTdButton("clients", "switch-user", () => this.changeClient(patient)).appendTo(tr);
                    this.getTdButton("cancel", "delete", () => this.deleteRecord(patient)).appendTo(tr);

                    tr.data("record", patient);
                }

                tr.appendTo($("#tbody-patients"));

                this.loadedRows++;
            }
            this.availableRows = json.availableRows;
            this.postFill();

            this.setSortIcon("name");
            this.setSortIcon("origin");
            this.setSortIcon("reference");
        };

        if (app.page === "patients") {
            $.get({
                url: this.getUrl(),
                success: success
            });
        } else if (app.page === "client") {
            $.post({
                url: this.getUrl(),
                data: JSON.stringify(app.client),
                contentType: "application/json",
                success: success
            });
        }
    }

    changeClient(patient = new Patient()) {
        RecordDialog.getClient(patient.client, client => {
            patient.client = client;

            $.ajax({
                method: "put",
                url: "api/organizations/" + app.organization.id + "/patients",
                contentType: "application/json",
                data: JSON.stringify(patient),
                success: () => this.fill()
            });
        });
    }

    multiChangeClient() {

    }
}

class EntriesTable extends ExplorerTable {
    constructor() {
        super("entries");
    }

    add(part) {
        this.part = part;

        let success = json => {
            for (let jsonStudy of json.rows) {
                let study = new Study();
                study.loadJson(jsonStudy);

                let tr = $("<tr></tr>"); {
                    this.getTdCheckbox().appendTo(tr);

                    if (app.page === "entries") {
                        this.getTdText(study.patient.client.getName(), () => app.explorer.showClient(study.patient.client)).appendTo(tr);
                        this.getTdText(study.patient.name, () => app.explorer.showPatient(study.patient)).appendTo(tr);
                    }

                    this.getTdDate(study.dateTime, () => { /* TODO */ }).appendTo(tr);
                    this.getTdText(study.description, () => { /* TODO */ }).appendTo(tr);
                    this.getTdNumber(study.seriesCount, () => { /* TODO */ }).appendTo(tr);

                    this.getTdButton("ok", "eye", () => { /* TODO */ }).appendTo(tr);
                    this.getTdEmailButton().appendTo(tr);
                    this.getTdDownloadButton().appendTo(tr);
                    this.getTdEditButton().appendTo(tr);

                    this.getTdButton("cancel", "delete", () => this.deleteRecord(study)).appendTo(tr);

                    tr.data("record", study);
                }

                tr.appendTo($("#tbody-entries"));

                this.loadedRows++;
            }
            this.availableRows = json.availableRows;
            this.postFill();

            this.setSortIcon("client");
            this.setSortIcon("patient");
            this.setSortIcon("date");
            this.setSortIcon("description");
            this.setSortIcon("seriesCount");
        }

        if (app.page === "entries") {
            $.get({
                url: this.getUrl(),
                success: success
            });
        } else if (app.page === "patient") {
            $.post({
                url: this.getUrl(),
                data: JSON.stringify(app.patient),
                contentType: "application/json",
                success: success
            });
        }
    }

    getTdEmailButton() {
        let td = $("<td class='button'></td>");
        let button = $("<button class='entries'></button>");
        $("<img src='icon/email/16'>").appendTo(button);
        button.appendTo(td);

        return td;
    }

    getTdDownloadButton() {
        let td = $("<td class='button'></td>");
        let button = $("<button class='entries'></button>");
        $("<img src='icon/download/16'>").appendTo(button);
        button.appendTo(td);

        return td;
    }

    getTdEditButton() {
        let td = $("<td class='button'></td>");
        let button = $("<button class='edit'></button>");
        $("<img src='icon/edit/16'>").appendTo(button);
        button.appendTo(td);

        return td;
    }
}

