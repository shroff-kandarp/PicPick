package com.picpick;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapter.InstaPhotoRecyclerAdapter;
import com.general.files.DownloadImage;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartCropper;
import com.utils.Utils;
import com.view.GridAutofitLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class InstaPhotosActivity extends AppCompatActivity {

    ImageView backImgView;
    TextView titleTxt;

    RecyclerView instaPhotoRecyclerView;

    InstaPhotoRecyclerAdapter adapter;


    ArrayList<HashMap<String, String>> instaPhotoList;

    GeneralFunctions generalFunctions;
    ProgressBar loading;

    StartCropper startCropper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_photos);

        generalFunctions = new GeneralFunctions(getActContext());

        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        instaPhotoRecyclerView = (RecyclerView) findViewById(R.id.instaPhotoRecyclerView);
        loading = (ProgressBar) findViewById(R.id.loading);

        instaPhotoRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActContext(), Utils.dipToPixels(getActContext(), 110)));
        instaPhotoRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActContext(), Utils.dipToPixels(getActContext(), 110)));

        instaPhotoList = new ArrayList<>();
        adapter = new InstaPhotoRecyclerAdapter(getActContext(), instaPhotoList);

        instaPhotoRecyclerView.setAdapter(adapter);
        backImgView.setOnClickListener(new setOnClickList());
        titleTxt.setText("Select Photos");

        getInstaPhotos();

        adapter.setOnItemClickList(new InstaPhotoRecyclerAdapter.OnItemClickList() {
            @Override
            public void onItemClick(int position) {
                HashMap<String, String> map = instaPhotoList.get(position);

                String item = map.get("PHOTO_PATH");

                Utils.printLog("SelectedPhotoURL", item);

                downloadSelectedPhoto(item);
            }
        });
    }

    public void downloadSelectedPhoto(String url) {
        Utils.printLog("Image", "url:" + url);
        DownloadImage downloadImg = new DownloadImage(getActContext(), url);
        downloadImg.setImageDownloadList(new DownloadImage.ImageDownloadListener() {
            @Override
            public void onImageDownload(Bitmap bmp) {
                Utils.printLog("Image", "Download:Success");

                if (generalFunctions.isAllPermissionGranted()) {
                    Utils.printLog("Image", "Saving");

                    File file = generalFunctions.saveImage(bmp);

                    startCropper = new StartCropper(getActContext(), Uri.fromFile(file));
                }
            }
        });
        downloadImg.execute();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    InstaPhotosActivity.super.onBackPressed();
                    break;
            }
        }
    }

    public void getInstaPhotos() {
        loading.setVisibility(View.VISIBLE);

        instaPhotoList.clear();
        adapter.notifyDataSetChanged();

        String instaPhotosUrl = "https://api.instagram.com/v1/users/" +
                generalFunctions.retriveValue(Utils.INSTA_USER_ID_KEY) + "/media/recent/?access_token=" +
                generalFunctions.retriveValue(Utils.INSTA_USER_ACCESS_TOKEN_KEY);

        Utils.printLog("instaPhotosUrl", instaPhotosUrl);
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(instaPhotosUrl, true);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                loading.setVisibility(View.GONE);
                if (responseString != null && !responseString.equals("")) {

                    if (generalFunctions.isJsonArrayKeyAvail("data", responseString)) {


                        JSONArray arr = generalFunctions.getJsonArr("data", responseString);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj_temp = generalFunctions.getJsonObject(arr, i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("PHOTO_ID", generalFunctions.getJsonValue("created_time", obj_temp.toString()));
                            map.put("PHOTO_PATH", generalFunctions.getJsonValue("url",
                                    generalFunctions.getJsonValue("standard_resolution",
                                            generalFunctions.getJsonValue("images", obj_temp.toString()))));

                            Utils.printLog("PHOTO_PATH", "::" + map.get("PHOTO_PATH"));
                            instaPhotoList.add(map);
                        }

                        adapter.notifyDataSetChanged();
                    }
                } else {
                    generalFunctions.showGeneralMessage("", "Please try again later");
                }
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return InstaPhotosActivity.this;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (startCropper != null) {
            startCropper.onActivityResult(requestCode, resultCode, data);
        }
    }
}
