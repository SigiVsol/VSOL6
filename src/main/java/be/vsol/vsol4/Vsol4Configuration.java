package be.vsol.vsol4;

import be.vsol.tools.json;
import be.vsol.vsol6.model.enums.Language;

public class Vsol4Configuration extends Vsol4Record {

    @json private String http_request_viewer_url, mail_sender_name;
    @json private Language language;

    // Constructors

    public Vsol4Configuration() {
        super("configurations");
    }

    // Methods

    @Override public String[] getFilterFields() {
        return new String[0];
    }

    // Getters

    public String getHttp_request_viewer_url() { return http_request_viewer_url; }

    public String getMail_sender_name() { return mail_sender_name; }

}
