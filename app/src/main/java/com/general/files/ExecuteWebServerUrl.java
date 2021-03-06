package com.general.files;

import android.content.Context;
import android.os.AsyncTask;

import com.utils.CommonUtilities;
import com.view.MyProgressDialog;

import java.util.HashMap;

/**
 * Created by Admin on 22-02-2016.
 */
public class ExecuteWebServerUrl extends AsyncTask<String, String, String> {

    SetDataResponse setDataRes;

    HashMap<String, String> parameters;

    GeneralFunctions generalFunc;

    String responseString = "";

    boolean directUrl_value = false;
    String directUrl = "";

    boolean isLoaderShown = false;
    Context mContext;

    MyProgressDialog myPDialog;

    boolean isGenerateDeviceToken = false;
    String key_DeviceToken_param;

    String requestURL = CommonUtilities.SERVER_URL_WEBSERVICE;

    public ExecuteWebServerUrl(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public ExecuteWebServerUrl(HashMap<String, String> parameters,String requestURL) {
        this.parameters = parameters;
        this.requestURL = requestURL;
    }

    public ExecuteWebServerUrl(String directUrl, boolean directUrl_value) {
        this.directUrl = directUrl;
        this.directUrl_value = directUrl_value;
    }

    public void setLoaderConfig(Context mContext, boolean isLoaderShown, GeneralFunctions generalFunc) {
        this.isLoaderShown = isLoaderShown;
        this.generalFunc = generalFunc;
        this.mContext = mContext;
    }

    public void setIsDeviceTokenGenerate(boolean isGenerateDeviceToken, String key_DeviceToken_param) {
        this.isGenerateDeviceToken = isGenerateDeviceToken;
        this.key_DeviceToken_param = key_DeviceToken_param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (isLoaderShown == true) {
            myPDialog = new MyProgressDialog(mContext, true, "Loading");
            myPDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        if (isGenerateDeviceToken == true && generalFunc != null) {

            String vDeviceToken = generalFunc.generateDeviceToken();

            if (vDeviceToken.equals("")) {
                return "";
            }

            if (parameters != null) {
                parameters.put(key_DeviceToken_param, "" + vDeviceToken);
            }
        }
        if (directUrl_value == false) {
            responseString = OutputStreamReader.performPostCall(requestURL, parameters);
        } else {
            responseString = new ExecuteResponse().getResponse(directUrl);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (myPDialog != null) {
            myPDialog.close();
        }

        if (setDataRes != null) {
            setDataRes.setResponse(responseString);
        }
    }

    public interface SetDataResponse {
        void setResponse(String responseString);
    }

    public void setDataResponseListener(SetDataResponse setDataRes) {
        this.setDataRes = setDataRes;
    }
}
