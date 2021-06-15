package be.vsol.orthanc;

import be.vsol.dicom.Dicom;
import be.vsol.dicom.model.SOPInstance;
import be.vsol.dicom.model.SeriesInstance;
import be.vsol.dicom.model.StudyInstance;
import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.util.Int;
import be.vsol.util.Json;
import be.vsol.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

public class Orthanc {

    private String host;
    private int port, timeout;

    // Constructors

    public Orthanc() { }

    // Methods

    public void start(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public void loadDicomInto(SOPInstance sopInstance) {
        HttpResponse response = getResponse("instances/" + sopInstance.getUid() + "/file");
        sopInstance.setDicom(new Dicom(response.getBody()));
    }

    public void loadDicomsInto(SeriesInstance seriesInstance) {
        for (SOPInstance sopInstance : seriesInstance.getInstances()) {
            loadDicomInto(sopInstance);
        }
    }

    public void loadDicomsInto(StudyInstance studyInstance) {
        for (SeriesInstance seriesInstance : studyInstance.getSeries()) {
            loadDicomsInto(seriesInstance);
        }
    }

    public Dicom getDicom(String instanceID) {
        HttpResponse response = getResponse("instances/" + instanceID + "/file");
        return new Dicom(response.getBody());
    }

    public HttpResponse getResponse(String request) {
        return getResponse(HttpRequest.Method.GET, request, null);
    }

    private HttpResponse getResponse(HttpRequest.Method method, String request, Object body) {
        HttpRequest httpRequest = new HttpRequest(method, request, body);

        Log.debug("Orthanc < " + httpRequest);
        HttpResponse response = Curl.get(host, port, timeout, httpRequest);

        if (!isSuccess(response)) {
            if (response == null) Log.err("Orthanc: empty response");
            else Log.err(response.getBodyAsString());
        }

        return response;
    }

    private boolean isSuccess(HttpResponse response) {
        return response != null && response.isValid() && response.getStatusCode() >= 200 && response.getStatusCode() < 300;
    }


}
