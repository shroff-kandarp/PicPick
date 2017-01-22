package com.general.files;

import android.content.Context;
import android.os.AsyncTask;

import com.utils.Utils;
import com.view.MyProgressDialog;

import java.util.ArrayList;

/**
 * Created by Admin on 08-07-2016.
 */
public class UploadImage extends AsyncTask<String, String, String> {

    String selectedImagePath;
    String responseString = "";

    String temp_File_Name = "";
    ArrayList<String[]> paramsList;

    Context mContext;
    MyProgressDialog myPDialog;
    GeneralFunctions generalFunc;

    public UploadImage(Context mContext, String selectedImagePath, String temp_File_Name, ArrayList<String[]> paramsList) {
        this.selectedImagePath = selectedImagePath;
        this.temp_File_Name = temp_File_Name;
        this.paramsList = paramsList;
        this.mContext = mContext;

        generalFunc = new GeneralFunctions(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        myPDialog = new MyProgressDialog(mContext, false, "Loading");
        myPDialog.show();
    }


    @Override
    protected String doInBackground(String... strings) {
//        String filePath = generalFunc.decodeFile(selectedImagePath, Utils.ImageUpload_DESIREDWIDTH,
//                Utils.ImageUpload_DESIREDHEIGHT, temp_File_Name);
        responseString = new ExecuteResponse().uploadImageAsFile(selectedImagePath, temp_File_Name, "vImage", paramsList);

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        myPDialog.close();

        Utils.printLog("ImageUpload", "::" + responseString);

        if (responseString != null && !responseString.equals("")) {

            if (generalFunc.isDataAvail("Action", responseString)) {

                generalFunc.showGeneralMessage("", generalFunc.getJsonValue("message", responseString));

            } else {
                generalFunc.showGeneralMessage("Error", generalFunc.getJsonValue("message", responseString));
            }
        } else {
            generalFunc.showGeneralMessage("Error", "Please try again later.");
        }
    }
}
