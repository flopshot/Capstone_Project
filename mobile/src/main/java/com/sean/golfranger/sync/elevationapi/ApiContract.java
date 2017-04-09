package com.sean.golfranger.sync.elevationapi;

import android.net.Uri;

/**
 * URL Parameter Constants defined by the API Documentation.
 * Describes the request URL parts. If the server, api,
 * or data source changes, we can update this single source
 * to reflect the change in the URL with robustness.
 *
 * CURRENT API: United States Geological Survey http://ned.usgs.gov/epqs/
 */

public class ApiContract {
    private static final String SCHEME = "http";
    private static final String AUTHORITY = "ned.usgs.gov";
    private static final String[] PATH = {"epqs","pqs.php"};
    private static final String[] PARAM = {"x", "y", "units", "output"};
    private static final String DEFAULT_UNITS = "Feet";
    private static final String DEFAULT_OUTPUT_TYPE = "json";

    /**
     * Method used to build the Request Elevation String for our api
     */
    public static String buildRequestUrl(Double[] latlon, String units, String outputType) {

        // Build up the request URL piece by piece
        // "http://ned.usgs.gov"
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
              .authority(AUTHORITY);
        // "http://ned.usgs.gov/epqs/pqs.php"
        for (String path : PATH) {
            builder.appendPath(path);
        }

        // "?x=<latitude>"
        builder.appendQueryParameter(PARAM[0], String.valueOf(latlon[1]));
        // "?y=<longitude>"
        builder.appendQueryParameter(PARAM[1], String.valueOf(latlon[0]));
        // "?units=<FT/METERS>"
        builder.appendQueryParameter(PARAM[2], units);
        // "?output=<JSON/XML>"
        builder.appendQueryParameter(PARAM[3], outputType);

        return builder.build().toString();
    }

    /**
     * Overloaded method to default to JSON output and units of FEET
     */
    public static String buildRequestUrl(Double[] latlon) {

        // Build up the request URL piece by piece
        // "http://http://ned.usgs.gov"
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
              .authority(AUTHORITY);
        // "http://ned.usgs.gov/epqs/pqs.php"
        for (String path : PATH) {
            builder.appendPath(path);
        }

        // "?x=<latitude>"
        builder.appendQueryParameter(PARAM[0], String.valueOf(latlon[1]));
        // "?y=<longitude>"
        builder.appendQueryParameter(PARAM[1], String.valueOf(latlon[0]));
        // "?units=<FT/METERS>"
        builder.appendQueryParameter(PARAM[2], DEFAULT_UNITS);
        // "?output=<JSON/XML>"
        builder.appendQueryParameter(PARAM[3], DEFAULT_OUTPUT_TYPE);

        return builder.build().toString();
    }
}
