package be.vsol.orthanc;

import be.vsol.dicom.model.SOPInstance;
import be.vsol.dicom.model.SeriesInstance;
import be.vsol.dicom.model.StudyInstance;
import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
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

    public StudyInstance getStudyInstance(String studyID) {
        StudyInstance studyInstance = new StudyInstance(studyID);

        HttpResponse response = getResponse("studies/" + studyID + "/series");
        JSONArray jsonSeries = response.getBodyAsJSONArray();

        for (JSONObject jsonSerie : Json.iterate(jsonSeries)) {
            String seriesID = Json.getOrDefault(jsonSerie, "ID", "");
            if (seriesID != null) {
                SeriesInstance seriesInstance = new SeriesInstance(seriesID);
                JSONArray jsonInstances = Json.getOrDefault(jsonSerie, "Instances", new JSONArray());
                for (int i = 0; i < jsonInstances.length(); i++) {
                    String instanceID = jsonInstances.getString(i);
                    if (instanceID != null) {
                        SOPInstance sopInstance = new SOPInstance(instanceID);
                        seriesInstance.getInstances().add(sopInstance);
                    }
                }
                studyInstance.getSeries().add(seriesInstance);
            }
        }

        return studyInstance;
    }

    private HttpResponse getResponse(String request) {
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
