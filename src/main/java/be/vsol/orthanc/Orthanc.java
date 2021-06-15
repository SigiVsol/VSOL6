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

    public StudyInstance getStudyInstance(String studyID) {
        StudyInstance studyInstance = new StudyInstance(studyID);

        HttpResponse response = getResponse("studies/" + studyID + "/metadata?expand");
        JSONObject jsonMeta = response.getBodyAsJSONObject();
        if (jsonMeta.has("mayHaveDeactivated") || jsonMeta.has("rawImageId")) studyInstance.setMayHaveDeactivated(true);

        JSONArray jsonSeries;
        if (jsonMeta.has("seriesDisplayOrder")) { // create custom series list with ID and MainDicomTags (containing Number as String)
            String temp = jsonMeta.getString("seriesDisplayOrder"); // this is a string, because of a faulty representation in the API
            temp = temp.replace("\n", "");
            temp = temp.replace("\\", "");
            JSONArray jsonArray = new JSONArray(temp);
            jsonSeries = new JSONArray();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonSerie = new JSONObject(); {
                    jsonSerie.put("ID", jsonArray.get(i));
                    JSONObject jsonMainDicomTags = new JSONObject(); {
                        jsonMainDicomTags.put("SeriesNumber", "" + (i+1));
                    }
                    jsonSerie.put("MainDicomTags", jsonMainDicomTags);
                }
            }
        } else {
            response = getResponse("studies/" + studyID + "/series");
            jsonSeries = response.getBodyAsJSONArray();
        }

        for (JSONObject jsonSerie : Json.iterate(jsonSeries)) {
            String seriesID = Json.getOrDefault(jsonSerie, "ID", "");
            JSONObject mainDicomTags = Json.getOrDefault(jsonSerie, "MainDicomTags", new JSONObject());
            String seriesNumber = Json.getOrDefault(mainDicomTags, "SeriesNumber", "");
            int number = Int.parse(seriesNumber, 0);
            if (seriesID != null) {
                SeriesInstance seriesInstance = new SeriesInstance(seriesID, studyInstance, number);
                JSONArray jsonInstances = Json.getOrDefault(jsonSerie, "Instances", new JSONArray());
                for (int i = 0; i < jsonInstances.length(); i++) {
                    String instanceID = jsonInstances.getString(i);
                    if (instanceID != null) {
                        SOPInstance sopInstance = new SOPInstance(instanceID, seriesInstance);
                        seriesInstance.getInstances().add(sopInstance);

                        if (studyInstance.isMayHaveDeactivated()) {
                            response = getResponse("instances/" + instanceID + "/metadata?expand");
                            jsonMeta = response.getBodyAsJSONObject();
                            if (Json.getOrDefault(jsonMeta, "isDeactivated", "").equals("true")) {
                                sopInstance.setActive(false);
                            }
                        }
                    }
                }
                studyInstance.getSeries().add(seriesInstance);
            }
        }

        studyInstance.sortSeries();

        return studyInstance;
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
