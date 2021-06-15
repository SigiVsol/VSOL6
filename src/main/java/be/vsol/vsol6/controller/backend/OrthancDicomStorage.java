package be.vsol.vsol6.controller.backend;

import be.vsol.dicom.Dicom;
import be.vsol.dicom.model.SOPInstance;
import be.vsol.dicom.model.SeriesInstance;
import be.vsol.dicom.model.StudyInstance;
import be.vsol.http.HttpResponse;
import be.vsol.util.Int;
import be.vsol.util.Json;
import be.vsol.vsol6.controller.Ctrl;
import org.json.JSONArray;
import org.json.JSONObject;

public class OrthancDicomStorage extends DicomStorage {

    // Constructors

    public OrthancDicomStorage(Ctrl ctrl) {
        super(ctrl);
    }

    // Methods

    @Override public StudyInstance getStudyInstance(String studyID) {
        StudyInstance studyInstance = new StudyInstance(studyID);

        HttpResponse response = ctrl.getOrthanc().getResponse("studies/" + studyID + "/metadata?expand");
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
            response = ctrl.getOrthanc().getResponse("studies/" + studyID + "/series");
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
                            response = ctrl.getOrthanc().getResponse("instances/" + instanceID + "/metadata?expand");
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

    @Override public Dicom getFirstDicomOf(StudyInstance studyInstance) {
        if (!studyInstance.getSeries().isEmpty()) {
            SeriesInstance seriesInstance = studyInstance.getSeries().firstElement();
            if (!seriesInstance.getActiveInstances().isEmpty()) {
                SOPInstance sopInstance = seriesInstance.getActiveInstances().firstElement();
                return ctrl.getOrthanc().getDicom(sopInstance.getUid());
            }
        }

        return null;
    }

}
