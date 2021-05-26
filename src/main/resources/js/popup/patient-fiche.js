class PatientFiche {

    static add(client = new Client(), callback = function() {}) {
        let patient = new Patient(); {
            patient.client = client;
        }

        this.show(patient, callback);
    }

    static edit(patient = new Patient(), callback = function() {}) {
        this.show(patient, callback);
    }

    static show(patient = new Patient(), callback = function() {}) {
        PatientFiche._patient = patient;
        PatientFiche._callback = callback;

        $("div.patient-fiche input").on("keyup", event => {
            if (event.key === "Enter") {
                PatientFiche.save();
            } else if (event.key === "Escape") {
                PatientFiche.close();
            }
        });

        $("#txtPatientBirthdate").val(patient.birthdate);
        $("#txtPatientChip").val(patient.chip);
        $("#txtPatientUeln").val(patient.ueln);
        $("#txtPatientSire").val(patient.sire);
        $("#txtPatientDamsire").val(patient.damsire);
        $("#txtPatientColor").val(patient.color);
        $("#txtPatientSpecies").val(patient.species);
        $("#txtPatientBreed").val(patient.breed);

        PatientFiche.setSex(patient.sex);
        $("#chkPatientNeutered").prop("checked", patient.neutered);

        $(".popup-patient-fiche").css("display", "block");
        $("#txtPatientName").val(patient.name).trigger("focus");
    }

    static save() {
        let patient = PatientFiche._patient;

        patient.name = $("#txtPatientName").val();
        patient.chip = $("#txtPatientChip").val();
        patient.ueln = $("#txtPatientUeln").val();
        patient.sire = $("#txtPatientSire").val();
        patient.damsire = $("#txtPatientDamsire").val();
        patient.color = $("#txtPatientColor").val();
        patient.species = $("#txtPatientSpecies").val();
        patient.breed = $("#txtPatientBreed").val();
        patient.sex = PatientFiche.getSex();
        patient.neutered = $("#chkPatientNeutered").prop("checked");
        patient.birthdate = $("#txtPatientBirthdate").val();

        if (PatientFiche._callback != null) {
            PatientFiche._callback(patient);
        }

        this.close();
    }

    static close() {
        $(".popup-patient-fiche").css("display", "none");
        $("div.patient-fiche input").off("keyup");
    }

    static setSex(sex) {
        if (sex === "M") sex = "male";
        else if (sex === "F") sex = "female";
        else sex = "unknown";

        $("div.patient-fiche button.sex").prop("disabled", false);
        $("div.patient-fiche button.sex." + sex).prop("disabled", true);
    }

    static getSex() {
        if ($("div.patient-fiche button.sex.male").prop("disabled")) return "M";
        else if ($("div.patient-fiche button.sex.female").prop("disabled")) return "F";
        else if ($("div.patient-fiche button.sex.unknown").prop("disabled")) return "X";
        else return "";
    }

}