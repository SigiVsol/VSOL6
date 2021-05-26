class Navbar {

    show() {
        let logged_in = app.user.id != null;

        $("#lblUsername").text(logged_in ? app.user.username : "");
        $("#lblOrganization").text(logged_in ? app.organization.name : "");
    }

    back() {
        history.back();
    }

    home() {
        app.pushHistory("clients");
        app.show();
    }

    logout() {
        Dialog.confirm("%{Are_you_sure}?", () => {
            app.user = new User();
            app.organization = new Organization();

            Tools.clearStorage("user.id");
            Tools.clearStorage("organization.id");

            app.pushHistory("clients");
            app.show();
        });
    }

    organization() {
        RecordDialog.getOrganization(app.organization, organization => {
            app.organization = organization;
            Tools.saveInStorage("organization.id", organization.id, false);
            app.pushHistory("clients");
            app.show();
        });
    }

    settings() {

        Dialog.getString("Geef naam", "Sigi", string => {
            console.log(string);
        })

        // RecordDialog.getClient(app.client, client => {
        //     console.log(client);
        // });
    }

}