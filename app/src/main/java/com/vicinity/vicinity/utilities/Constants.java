package com.vicinity.vicinity.utilities;

/**
 * Created by Jovch on 27-Mar-16.
 */
public final class Constants {
    public static final String S_PREFS_NOTIFICATION_JSON_ARRAY = "uasd8uasdjas89dja89sdjasda89";


    public static final String BROWSER_API_KEY = "AIzaSyCnNU--KrYAW2QARuei9p1mIDExPQwPcs4";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.vicinity.vicinity";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    /* Following are the Place types' constants, which values represent a valid
        "type" for building request URLs
     */
    public static final String TYPE_RESTAURANT = "restaurant";
    public static final String TYPE_BAR = "bar";
    public static final String TYPE_CAFE = "cafe";
    public static final String TYPE_HOTEL = "GoogleMapsAPI_missing_hotel";      // TODO: find hotels...
    public static final String TYPE_CASINO = "casino";
    public static final String TYPE_DELIVERY = "meal_delivery";                 // TODO: add "meal_takeaway" as well
    public static final String TYPE_FITNESS = "gym";
    public static final String TYPE_POOL = "spa";                               // TODO: find pools...
    public static final String TYPE_MOVIE = "movie_theater";

    public static final int STATUS_CODE_SUCCESS = 200;
    public static final int STATUS_CODE_NOT_FOUND = 404;

    public static final String GET_LOGIN_REQUEST_URL = "http://vicinity-vicinity.rhcloud.com/LogIn?id=USER_ID";

    public static final String POST_RESERVATION_REQUEST_URL = "http://vicinity-vicinity.rhcloud.com/Custumer";
    public static final String GET_RESERVATION_ANSWER_URL = "http://vicinity-vicinity.rhcloud.com/Custumer?id=CUSTOMER_ID";

    public static final String POST_RESERVATION_ANSWER_URL = "http://vicinity-vicinity.rhcloud.com/Business";
    public static final String GET_RESERVATION_REQUEST_URL = "http://vicinity-vicinity.rhcloud.com/Business?id=PLACE_ID";

    public static final String POST_REGISTER_NEW_BUSINESS_URL = "http://vicinity-vicinity.rhcloud.com/LogIn";
    public static final String POST_CONFIRMATION_CODE_URL = "http://vicinity-vicinity.rhcloud.com/SMSServlet";

    public static final String ANSWER_LISTENER_EXTRA_USER_ID = "ANSWER_LISTENER_SERVICE_EXTRA_ID";
    public static final String RESERVATION_LISTENER_EXTRA_PLACE_ID = "RESERVATION_LISTENER_SERVICE_EXTRA_ID";


    public static final String PREFS_USER_TYPE = "SHARED_PREFS_USER_TYPE_FIELD";
}