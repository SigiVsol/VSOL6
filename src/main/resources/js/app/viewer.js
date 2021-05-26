class Viewer {
    show() {
        $("#divLogin").css("display", "none");
        $("#divExplorer").css("display", "none");
        $("#divViewer").css("display", "block");
        $("#divNavbar :button").prop("disabled", false);
    }
}