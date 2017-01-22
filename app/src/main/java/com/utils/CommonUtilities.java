package com.utils;

/**
 * Created by Shroff on 16-Dec-16.
 */
public class CommonUtilities {

    public static final String SERVER_URL_WEBSERVICE = "http://wpflindia.com/test/webservice/webservices.php";

    public static final String INSTA_CLIENT_ID = "de4ac87ffa7a4f3392aa789f07b9672e";
    public static final String INSTA_CLIENT_SECRET = "0bc1aecef8c54a0ea4e1cc99387ce563";

    //Used for getting token and User details.
    public static final String INSTA_API_URL = "https://api.instagram.com/v1";
    //Used to specify the API version which we are going to use.
    public static String INSTA_CALLBACK_URL = "http://www.selfwork.com";
    //The callback url that we have used while registering the application.

    public static final String INSTA_AUTH_URL = "https://api.instagram.com/oauth/authorize"
            + "?client_id=" + INSTA_CLIENT_ID + "&redirect_uri=" + INSTA_CALLBACK_URL +
            "&response_type=code&display=touch&scope=basic+likes+comments+relationships";

    public static final String INSTA_TOKEN_URL = "https://api.instagram.com/oauth/access_token";

    /* +
            "?client_id=" + INSTA_CLIENT_ID + "&client_secret=" + INSTA_CLIENT_SECRET + "&redirect_uri="
            + INSTA_CALLBACK_URL + "&grant_type=authorization_code";*/
}
