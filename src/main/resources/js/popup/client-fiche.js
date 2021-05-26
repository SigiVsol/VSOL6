class ClientFiche {

    static add(callback = function() {}) {
        this.show(new Client(), callback);
    }

    static edit(client = new Client(), callback = function() {}) {
        this.show(client, callback);
    }

    static save() {
        let client = ClientFiche._client;

        client.lastName = $("#txtClientLastName").val();
        client.firstName = $("#txtClientFirstName").val();
        client.company = $("#txtClientCompany").val();
        client.via = $("#txtClientVia").val();

        client.language = ClientFiche.getLanguage();

        client.phone = $("#txtClientPhone").val();
        client.email = $("#txtClientEmail").val();

        client.address.street = $("#txtClientStreet").val();
        client.address.postal = $("#txtClientPostal").val();
        client.address.city = $("#txtClientCity").val();
        client.address.country = $("#txtClientCountry").val();

        client.extraInfo = $("#txaClientExtraInfo").val();

        if (ClientFiche._callback != null) {
            ClientFiche._callback(client);
        }

        this.close();
    }

    static show(client = new Client(), callback = function() {}) {
        ClientFiche._client = client;
        ClientFiche._callback = callback;

        $("div.client-fiche input").on("keyup", event => {
            if (event.key === "Enter") {
                ClientFiche.save();
            } else if (event.key === "Escape") {
                ClientFiche.close();
            }
        });

        $("#txtClientFirstName").val(client.firstName);
        $("#txtClientCompany").val(client.company);
        $("#txtClientVia").val(client.via);

        ClientFiche.setLanguage(client.language);

        $("#txtClientPhone").val(client.phone);
        $("#txtClientEmail").val(client.email);

        $("#txtClientStreet").val(client.address.street);
        $("#txtClientPostal").val(client.address.postal);
        $("#txtClientCity").val(client.address.city);
        $("#txtClientCountry").val(client.address.country);

        $("#txaClientExtraInfo").val(client.extraInfo);

        $(".popup-client-fiche").css("display", "block");
        $("#txtClientLastName").val(client.lastName).trigger("focus");
    }

    static close() {
        $(".popup-client-fiche").css("display", "none");
        $("div.client-fiche input").off("keyup");
    }

    static setLanguage(language) {
        $("div.client-fiche button.language").prop("disabled", false);
        $("div.client-fiche button.language." + language).prop("disabled", true);
    }

    static getLanguage() {
        if ($("div.client-fiche button.language.en").prop("disabled")) return "en";
        else if ($("div.client-fiche button.language.nl").prop("disabled")) return "nl";
        else if ($("div.client-fiche button.language.fr").prop("disabled")) return "fr";
        else if ($("div.client-fiche button.language.de").prop("disabled")) return "de";
        else return "";
    }

}