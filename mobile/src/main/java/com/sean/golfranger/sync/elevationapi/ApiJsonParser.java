package com.sean.golfranger.sync.elevationapi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON data extraction methods for return JSON string from API
 *
 * CURRENT API: United States Geological Survey http://ned.usgs.gov/epqs/
 */

public class ApiJsonParser {
    private static final String TOP_LEVEL_OBJECT_LABEL = "USGS_Elevation_Point_Query_Service";
    private static final String SECOND_LEVEL_OBJECT_LABEL = "Elevation_Query";
    private static final String ELEVATION_DATA_LABEL = "Elevation";

    public static String getElevation(String elevationJsonStr) {
        try {
            JSONObject outerJson = new JSONObject(elevationJsonStr);
            JSONObject headerJson = outerJson.getJSONObject(TOP_LEVEL_OBJECT_LABEL);
            JSONObject singleMovieJson = headerJson.getJSONObject(SECOND_LEVEL_OBJECT_LABEL);
            return singleMovieJson.getString(ELEVATION_DATA_LABEL);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
