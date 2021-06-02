package be.vsol.vsol4.model;

import be.vsol.tools.json;
import be.vsol.vsol6.model.enums.Language;
import org.json.JSONObject;

public class Vsol4Configuration extends Vsol4Record {

    @json private String http_request_viewer_url, mail_sender_name, mail_max_attachment_size_MB, report_cmd, sync_download_studies, mode, printer_dymo_cd_cmd, hibernate_counter, create_study_button_enabled, sync_enabled, acquisition_deactivate_on_retake, report_enabled, create_study_button_name, jpeg_label_bottom_left, navigation_toolbar, pms_name, default_alias, acquisition_enabled, create_study_alt_button_enabled, version, compression_cmd, disks_mail, printer_dymo_usb_cmd, accession_number_prefix, create_study_alt_button_name, viewer_custom_command_enabled, viewer_custom_command_icon_label, publication_app, viewer_custom_command_icon_class, viewer_type, jpeg_label_bottom_right, cloud_server_url, hibernate_enabled, ui_zoom, printer_dymo_cd_default_enabled, generator_cmd, viewer_configuration, jpeg_label_top_right, acquisition_configuration, mail_sender_email, end_study_lua_code, mail_reply_to, sync_delay_sec, viewer_url, printer_dymo_usb_default_enabled, mail_default_bcc, viewer_custom_command_lua_code, printer_dymo_enabled, jpeg_label_top_left, acquisition_generator_enabled, create_study_alt_button_url, cloud_server_token, management_server_url;
    @json private Language language;
    @json private JSONObject mailTemplates, reportTemplates, reportBodyTemplates;
    @json private boolean server, admin;
    // arrays: remainingTemplate, modalities, disks_status

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

    public String getMail_max_attachment_size_MB() { return mail_max_attachment_size_MB; }

    public String getReport_cmd() { return report_cmd; }

    public String getSync_download_studies() { return sync_download_studies; }

    public String getMode() { return mode; }

    public String getPrinter_dymo_cd_cmd() { return printer_dymo_cd_cmd; }

    public String getHibernate_counter() { return hibernate_counter; }

    public String getCreate_study_button_enabled() { return create_study_button_enabled; }

    public String getSync_enabled() { return sync_enabled; }

    public String getAcquisition_deactivate_on_retake() { return acquisition_deactivate_on_retake; }

    public String getReport_enabled() { return report_enabled; }

    public String getCreate_study_button_name() { return create_study_button_name; }

    public String getJpeg_label_bottom_left() { return jpeg_label_bottom_left; }

    public String getNavigation_toolbar() { return navigation_toolbar; }

    public String getPms_name() { return pms_name; }

    public String getDefault_alias() { return default_alias; }

    public String getAcquisition_enabled() { return acquisition_enabled; }

    public String getCreate_study_alt_button_enabled() { return create_study_alt_button_enabled; }

    public String getVersion() { return version; }

    public String getCompression_cmd() { return compression_cmd; }

    public String getDisks_mail() { return disks_mail; }

    public String getPrinter_dymo_usb_cmd() { return printer_dymo_usb_cmd; }

    public String getAccession_number_prefix() { return accession_number_prefix; }

    public String getCreate_study_alt_button_name() { return create_study_alt_button_name; }

    public String getViewer_custom_command_enabled() { return viewer_custom_command_enabled; }

    public String getViewer_custom_command_icon_label() { return viewer_custom_command_icon_label; }

    public String getPublication_app() { return publication_app; }

    public String getViewer_custom_command_icon_class() { return viewer_custom_command_icon_class; }

    public String getViewer_type() { return viewer_type; }

    public String getJpeg_label_bottom_right() { return jpeg_label_bottom_right; }

    public String getCloud_server_url() { return cloud_server_url; }

    public String getHibernate_enabled() { return hibernate_enabled; }

    public String getUi_zoom() { return ui_zoom; }

    public String getPrinter_dymo_cd_default_enabled() { return printer_dymo_cd_default_enabled; }

    public String getGenerator_cmd() { return generator_cmd; }

    public String getViewer_configuration() { return viewer_configuration; }

    public String getJpeg_label_top_right() { return jpeg_label_top_right; }

    public String getAcquisition_configuration() { return acquisition_configuration; }

    public String getMail_sender_email() { return mail_sender_email; }

    public String getEnd_study_lua_code() { return end_study_lua_code; }

    public String getMail_reply_to() { return mail_reply_to; }

    public String getSync_delay_sec() { return sync_delay_sec; }

    public String getViewer_url() { return viewer_url; }

    public String getPrinter_dymo_usb_default_enabled() { return printer_dymo_usb_default_enabled; }

    public String getMail_default_bcc() { return mail_default_bcc; }

    public String getViewer_custom_command_lua_code() { return viewer_custom_command_lua_code; }

    public String getPrinter_dymo_enabled() { return printer_dymo_enabled; }

    public String getJpeg_label_top_left() { return jpeg_label_top_left; }

    public String getAcquisition_generator_enabled() { return acquisition_generator_enabled; }

    public String getCreate_study_alt_button_url() { return create_study_alt_button_url; }

    public String getCloud_server_token() { return cloud_server_token; }

    public String getManagement_server_url() { return management_server_url; }

    public Language getLanguage() { return language; }

    public JSONObject getMailTemplates() { return mailTemplates; }

    public JSONObject getReportTemplates() { return reportTemplates; }

    public JSONObject getReportBodyTemplates() { return reportBodyTemplates; }

    public boolean isServer() { return server; }

    public boolean isAdmin() { return admin; }
}
