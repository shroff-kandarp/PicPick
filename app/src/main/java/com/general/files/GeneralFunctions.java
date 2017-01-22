package com.general.files;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.facebook.login.LoginManager;
import com.picpick.LauncherActivity;
import com.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Created by Shroff on 08-Dec-16.
 */
public class GeneralFunctions {
    Context mContext;
    public static final int MY_PERMISSIONS_REQUEST = 51;

    public GeneralFunctions(Context mContext) {
        this.mContext = mContext;
    }

    public void storedata(String key, String data) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, data);
        editor.commit();
    }

    public String retriveValue(String key) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String value_str = mPrefs.getString(key, "");

        return value_str;
    }

    public boolean isFirstLaunchFinished() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String isFirstLaunchFinished_str = mPrefs.getString(Utils.isFirstLaunchFinished, "");

        if (!isFirstLaunchFinished_str.equals("")) {
            return true;
        }

        return false;
    }

    public void setMemberData(HashMap<String, String> data) {
        storedata(Utils.userLoggedIn_key, "1");
        storedata(Utils.SOCIAL_ID_key, data.get(Utils.SOCIAL_ID_key));
        storedata(Utils.name_key, data.get(Utils.name_key));
        storedata(Utils.email_key, data.get(Utils.email_key));
        storedata(Utils.LOGIN_TYPE_key, data.get(Utils.LOGIN_TYPE_key));
    }

    public void signOut() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();

        editor.commit();

        storedata(Utils.isFirstLaunchFinished, "Yes");
        logOUTFrmFB();

        restartApp();
    }

    public boolean isUserLoggedIn() {

        if (!retriveValue(Utils.userLoggedIn_key).equals("") && retriveValue(Utils.userLoggedIn_key).equals("1")) {
            return true;
        }

        return false;
    }

    public String getMemberId() {
        return retriveValue(Utils.MEMBER_ID_key);
    }

    public String generateDeviceToken() {
//        if (checkPlayServices() == false) {
//            return "";
//        }
//        InstanceID instanceID = InstanceID.getInstance(mContext);
//        String GCMregistrationId = "";
//        try {
//            GCMregistrationId = instanceID.getToken(retrieveValue(CommonUtilities.APP_GCM_SENDER_ID_KEY), GoogleCloudMessaging.INSTANCE_ID_SCOPE,
//                    null);
//        } catch (IOException e) {
//            e.printStackTrace();
//            GCMregistrationId = "";
//        }
//
//        return GCMregistrationId;

        return "";
    }

    public boolean checkPlayServices() {
//        final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//        final int result = googleAPI.isGooglePlayServicesAvailable(mContext);
//        if (result != ConnectionResult.SUCCESS) {
//            if (googleAPI.isUserResolvableError(result)) {
//
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    public void run() {
//                        googleAPI.getErrorDialog(((Activity) mContext), result,
//                                Utils.PLAY_SERVICES_RESOLUTION_REQUEST).show();
//                    }
//                });
//
//            }
//
//            return false;
//        }

        return true;
    }

    public void restartApp() {
        (new StartActProcess(mContext)).startAct(LauncherActivity.class);
        ActivityCompat.finishAffinity((Activity) mContext);
    }


    public void showGeneralMessage(String title, String message) {
        GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
        generateAlert.setContentMessage(title, message);
        generateAlert.setPositiveBtn("Ok");
        generateAlert.showAlertBox();
    }

    public boolean isDataAvail(String key, String responseString) {
        String actionValue = getJsonValue(key, responseString);

        if (actionValue != null && !actionValue.equals("") && actionValue.trim().equals("1")) {
            return true;
        }
        return false;
    }

    public String getJsonValue(String key, String response) {

        try {
            JSONObject obj_temp = new JSONObject(response);

            String value_str = obj_temp.getString(key);

            if (value_str != null && !value_str.equals("null") && !value_str.equals("")) {
                return value_str;
            }

        } catch (JSONException e) {
            e.printStackTrace();

            return "";
        }

        return "";
    }

    public JSONArray getJsonArr(String key, String response) {

        try {
            JSONObject obj_temp = new JSONObject(response);

            JSONArray arr = obj_temp.getJSONArray(key);

            if (arr != null) {
                return arr;
            }

        } catch (JSONException e) {
            e.printStackTrace();

            return (new JSONArray());
        }

        return (new JSONArray());
    }

    public boolean isJsonArrayKeyAvail(String key, String responseString) {

        try {
            JSONObject obj = new JSONObject(responseString);
            JSONArray arr = obj.getJSONArray(key);

            if (arr != null) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public JSONObject getJsonObject(JSONArray arr, int position) {
        try {
            JSONObject obj_temp = arr.getJSONObject(position);

            return obj_temp;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }

    public void logOUTFrmFB() {
        LoginManager.getInstance().logOut();
    }

    public boolean isAllPermissionGranted() {
        int permissionCheck_fine = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_coarse = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_storage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_camera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

        if (permissionCheck_fine != PackageManager.PERMISSION_GRANTED || permissionCheck_coarse != PackageManager.PERMISSION_GRANTED
                || permissionCheck_storage != PackageManager.PERMISSION_GRANTED || permissionCheck_camera != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST);


            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            return false;
        }

        return true;
    }

    public File saveImage(Bitmap bmp) {

        int byteSize = bmp.getRowBytes() * bmp.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
        bmp.copyPixelsToBuffer(byteBuffer);

//                    byte[] byteArray = byteBuffer.array();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ByteArrayInputStream bs = new ByteArrayInputStream(byteArray);


        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + Utils.storedImageFolderName);
        myDir.mkdirs();

        File file = new File(myDir, Utils.storedImageName);

        try {

            FileOutputStream out = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = bs.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            bs.close();
            out.close();
            Utils.printLog("Image", "Saving:SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            Utils.printLog("Image", "Saving:Error");
        }

        return file;
    }
}
