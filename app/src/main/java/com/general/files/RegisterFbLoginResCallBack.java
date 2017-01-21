package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.picpick.DashboardActivity;
import com.utils.Utils;
import com.view.MyProgressDialog;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Admin on 29-06-2016.
 */
public class RegisterFbLoginResCallBack implements FacebookCallback<LoginResult> {
    Context mContext;
    GeneralFunctions generalFunc;

    MyProgressDialog myPDialog;

    public RegisterFbLoginResCallBack(Context mContext) {
        this.mContext = mContext;

        generalFunc = new GeneralFunctions(mContext);
    }

    @Override
    public void onSuccess(final LoginResult loginResult) {
        myPDialog = new MyProgressDialog(mContext, false, "Loading");
        myPDialog.show();

        final String accessToken = loginResult.getAccessToken().getToken();
        generalFunc.storedata("AT", accessToken);
        Utils.printLog("AccessToken", "::" + loginResult.getAccessToken().getToken());
        Utils.printLog("AccessTokenToString", "::" + loginResult.getAccessToken().toString());
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject me,
                            GraphResponse response) {
                        // Application code
                        myPDialog.close();
                        Utils.printLog("responseObj:", "::" + me.toString());
                        Utils.printLog("response:", "::" + response.toString());
                        if (response.getError() != null) {
                            // handle error
                            Log.d("onError", "onError:" + response.getError());

                            generalFunc.showGeneralMessage("Error", "Please try again");
                        } else {

                            String email_str = generalFunc.getJsonValue("email", me.toString());
                            String name_str = generalFunc.getJsonValue("name", me.toString());
                            String first_name_str = generalFunc.getJsonValue("first_name", me.toString());
                            String last_name_str = generalFunc.getJsonValue("last_name", me.toString());
                            String fb_id_str = generalFunc.getJsonValue("id", me.toString());

                            registerFbUser(email_str, first_name_str, last_name_str, fb_id_str, accessToken);

//                            generalFunc.logOUTFrmFB();
                        }
                    }


                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,email,photos");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {
        generalFunc.showGeneralMessage("Facebook Error", "Please try again:");
    }

    public void registerFbUser(final String email, final String fName, final String lName, final String fbId, final String accessToken) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "login");
        parameters.put("vName", fName + " " + lName);
        parameters.put("vEmail", email);
        parameters.put("iFbId", fbId);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(mContext, true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    if (generalFunc.isDataAvail("Action", responseString)) {

                        generalFunc.storedata("FNAME", fName);
                        generalFunc.storedata("LNAME", lName);
                        generalFunc.storedata(Utils.FACEBOOK_ACCESS_TOKEN_KEY, accessToken);
                        generalFunc.storedata(Utils.MEMBER_ID_key, generalFunc.getJsonValue("message", responseString));

                        Bundle bn = new Bundle();
                        bn.putString("FNAME", fName);
                        bn.putString("LNAME", lName);
                        bn.putString("EMAIL", email);
                        bn.putString("FBID", fbId);

                        HashMap<String, String> userData = new HashMap<>();
                        userData.put(Utils.email_key, email);
                        userData.put(Utils.name_key, fName + " " + lName);
                        userData.put(Utils.SOCIAL_ID_key, fbId);
                        // userData.put(Utils.SOCIAL_ID_key, acct.getId());
                        userData.put(Utils.LOGIN_TYPE_key, Utils.SOCIAL_LOGIN_FACEBOOK_key_value);


                        generalFunc.setMemberData(userData);

                        new StartActProcess(mContext).startActWithData(DashboardActivity.class, bn);

                        ActivityCompat.finishAffinity((Activity) mContext);

                    } else {
                        generalFunc.showGeneralMessage("Error", generalFunc.getJsonValue("message", responseString));
                    }
                } else {
                    generalFunc.showGeneralMessage("Error", "Please try again later.");
                }
            }
        });
        exeWebServer.execute();
    }

    /*public void registerFbUser(final String email, final String fName, final String lName, final String fbId, String accessToken) {

        generalFunc.storeUserData(fbId);
        generalFunc.storedata("FNAME", fName);
        generalFunc.storedata("LNAME", lName);
        generalFunc.storedata(Utils.FACEBOOK_ACCESS_TOKEN_KEY, accessToken);

        Bundle bn = new Bundle();
        bn.putString("FNAME", fName);
        bn.putString("LNAME", lName);
        bn.putString("EMAIL", email);
        bn.putString("FBID", fbId);

        HashMap<String, String> userData = new HashMap<>();
        userData.put(Utils.email_key, email);
        userData.put(Utils.name_key, fName + " " + lName);
        userData.put(Utils.SOCIAL_ID_key, fbId);
        // userData.put(Utils.SOCIAL_ID_key, acct.getId());
        userData.put(Utils.LOGIN_TYPE_key, Utils.SOCIAL_LOGIN_FACEBOOK_key_value);


        generalFunc.setMemberData(userData);

        new StartActProcess(mContext).startActWithData(DashboardActivity.class, bn);

        ActivityCompat.finishAffinity((Activity) mContext);
    }*/
}
