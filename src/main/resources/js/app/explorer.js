class Explorer {
    constructor() {
        this.txtFilter = $("#txtFilter");
        this.btnPatientName = $("#btnPatientName");

        this._clientsTable = new ClientsTable();
        this._patientsTable = new PatientsTable();
        this._entriesTable = new EntriesTable();

        this.txtFilter.on("keyup", event => {
            $(".clear-filter").prop("disabled", this.txtFilter.val().trim() === "");

            if (event.key === "Enter") this.applyFilter();
            else if (event.key === "Escape") this.clearFilter();
            else if (event.key !== "Shift" && event.key !== "CapsLock" && event.key !== "Control" && event.key !== "Alt" && event.key !== "AltGraph"
                && event.key !== "ArrowUp" && event.key !== "ArrowDown" && event.key !== "ArrowLeft" && event.key !== "ArrowRight") {
                clearTimeout(this.filterDelay);
                this.filterDelay = setTimeout(() => this.applyFilter(), 500);
            }
        });

        $(".explorer-scroll-pane").on("scroll", () => {
            if (app.page === "clients") {
                this.clientsTable.scroll();
            } else if (app.page === "patients" || app.page === "client") {
                this.patientsTable.scroll();
            } else if (app.page === "entries" || app.page === "patient") {
                this.entriesTable.scroll();
            }
        });
    }

    show() {
        $("#divLogin").css("display", "none");
        $("#divExplorer").css("display", "block");
        $("#divViewer").css("display", "none");
        $("#divNavbar :button").prop("disabled", false);

        $("#btnClients").prop("disabled", app.page === "clients");
        $("#btnPatients").prop("disabled", app.page === "patients");
        $("#btnEntries").prop("disabled", app.page === "entries");

        $(".main-toggle-bar").css("display", app.id == null ? "block" : "none");
        $(".breadcrumb-bar").css("display", app.id != null ? "block" : "none");

        $(".clients-summary").css("display", app.page === "client" ? "block" : "none");
        $(".patients-summary").css("display", app.page === "patient" ? "block" : "none");

        if (app.id == null) {
            $(".explorer-filter-zone").addClass("explorer-full-width");
            $(".explorer-table-zone").addClass("explorer-full-width");
        } else {
            $(".explorer-filter-zone").removeClass("explorer-full-width");
            $(".explorer-table-zone").removeClass("explorer-full-width");
        }

        $("#table-clients").css("display", app.page === "clients" ? "block" : "none");
        $("#table-patients").css("display", app.page === "patients" || app.page === "client" ? "block" : "none");
        $("#table-entries").css("display", app.page === "entries" || app.page === "patient" ? "block" : "none");

        this.btnPatientName.css("display", app.page === "patient" ? "inline-block" : "none" );

        $("#btnAddClient").css("display", app.page === "clients" ? "block" : "none");
        $("#btnAddPatient").css("display", app.page === "client" ? "block" : "none");
        $("#btnAddStudy").css("display", app.page === "patient" ? "block" : "none");

        $(".hide-if-patient").css("display", app.page === "patient" ? "none" : "table-cell");
        $(".resize-if-patient").css("width", app.page === "patient" ? "100%" : "35%");

        if (app.page === "clients") {
            this.clientsTable.fill();
            $('html').removeClass("patients-scrollbar").removeClass("entries-scrollbar").addClass('clients-scrollbar');
        } else if (app.page === "patients") {
            this.patientsTable.fill();
            $('html').removeClass("clients-scrollbar").removeClass("entries-scrollbar").addClass('patients-scrollbar');
        } else if (app.page === "entries") {
            this.entriesTable.fill();
            $('html').removeClass("clients-scrollbar").removeClass("patients-scrollbar").addClass('entries-scrollbar');
        } else if (app.page === "client") {
            this._patientsTable.fill();
            $("#btnClientName").text(app.client.getName());

            $("#lblClientCompany").text(app.client.company);
            $("#lblClientVia").text(app.client.via);
            $("#lblClientLanguage").text(app.client.language);
            $("#lblClientEmail").text(app.client.email);
            $("#lblClientPhone").text(app.client.phone);
            $("#lblClientAddress").text(app.client.address.getText());
            $("#lblClientExtraInfo").text(app.client.extraInfo);

            $('html').removeClass("clients-scrollbar").removeClass("entries-scrollbar").addClass('patients-scrollbar');
        } else if (app.page === "patient") {
            this.entriesTable.fill();
            $("#btnClientName").text(app.patient.client.getName());
            this.btnPatientName.text(app.patient.name);

            $("#lblPatientBirthdate").text(app.patient.birthdate);
            $("#lblPatientChip").text(app.patient.chip);
            $("#lblPatientUeln").text(app.patient.ueln);
            $("#lblPatientSpecies").text(app.patient.species);
            $("#lblPatientBreed").text(app.patient.breed);
            $("#lblPatientSire").text(app.patient.sire);
            $("#lblPatientDamsire").text(app.patient.damsire);
            $("#lblPatientNeutered").text(Tools.bool2str(app.patient.neutered));
            $("#lblPatientSex").text(app.patient.getSexString());
            $("#lblPatientColor").text(app.patient.color);

            $('html').removeClass("clients-scrollbar").removeClass("patients-scrollbar").addClass('entries-scrollbar');
        }
    }

    applyFilter() {
        $(".clear-filter").prop("disabled", this.txtFilter.val().trim() === "");

        let filter = this.txtFilter.val().trim();
        if (app.page === "clients") {
            if (this.clientsTable.filter.trim() !== filter) {
                this.clientsTable.filter = filter;
                this.clientsTable.fill();
            }
        } else if (app.page === "patients" || app.page === "client") {
            if (this.patientsTable.filter.trim() !== filter) {
                this.patientsTable.filter = filter;
                this.patientsTable.fill();
            }
        } else if (app.page === "entries" || app.page === "patient") {
            if (this.entriesTable.filter.trim() !== filter) {
                this.entriesTable.filter = filter;
                this.entriesTable.fill();
            }
        }
    }

    clearFilter() {
        this.txtFilter.val("");
        this.applyFilter();
        this.txtFilter.trigger("focus");
    }

    showClients() {
        this.txtFilter.val(this.clientsTable.filter);
        app.pushHistory("clients");
        app.show();
    }

    showPatients() {
        this.txtFilter.val(this.patientsTable.filter);
        app.pushHistory("patients");
        app.show();
    }

    showEntries() {
        this.txtFilter.val(this.entriesTable.filter);
        app.pushHistory("entries");
        app.show();
    }

    showClient(client = new Client()) {
        if (client.id == null) return;

        this.txtFilter.val(this.patientsTable.filter);
        app.client = client;
        app.pushHistory("client", client.id);
        app.show();
    }

    showPatient(patient = new Patient()) {
        if (patient.id == null) return;

        this.txtFilter.val(this.entriesTable.filter);
        app.patient = patient;
        app.pushHistory("patient", patient.id);
        app.show();
    }

    backToClient() {
        if (app.page === "patient") {
            this.showClient(app.patient.client)
        }
    }

    addClient() {
        ClientFiche.add(client => {
            $.ajax({
                method: "put",
                url: "api/organizations/" + app.organization.id + "/clients",
                contentType: "application/json",
                data: JSON.stringify(client),
                success: json => {
                    client.id = json.id;
                    this.showClient(client)
                }
            });
        });
    }

    editClient() {
        ClientFiche.edit(app.client, client => {
            $.ajax({
                method: "put",
                url: "api/organizations/" + app.organization.id + "/clients",
                contentType: "application/json",
                data: JSON.stringify(client),
                success: () => {
                    this.show();
                }
            });
        });
    }

    addPatient() {
        PatientFiche.add(app.client, patient => {
            $.ajax({
                method: "put",
                url: "api/organizations/" + app.organization.id + "/patients",
                contentType: "application/json",
                data: JSON.stringify(patient),
                success: json => {
                    patient.id = json.id;
                    this.showPatient(patient);
                }
            });
        });
    }

    editPatient() {
        PatientFiche.edit(app.patient, patient => {
            $.ajax({
                method: "put",
                url: "api/organizations/" + app.organization.id + "/patients",
                contentType: "application/json",
                data: JSON.stringify(patient),
                success: () => {
                    this.show();
                }
            });
        });
    }

    addStudy() {

    }

    // Getters

    get clientsTable() { return this._clientsTable; }
    get patientsTable() { return this._patientsTable; }
    get entriesTable() { return this._entriesTable; }

}
