package com.picpick;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapter.FacebookAlbumRecyclerAdapter;
import com.adapter.FacebookPhotoRecyclerAdapter;
import com.general.files.DownloadImage;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.GridAutofitLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FacebookPhotosActivity extends AppCompatActivity {

    ImageView backImgView;
    TextView titleTxt;

    RecyclerView albumRecyclerView;
    RecyclerView albumPhotoRecyclerView;

    FacebookAlbumRecyclerAdapter adapter;
    FacebookPhotoRecyclerAdapter adapter_albumPhoto;


    ArrayList<HashMap<String, String>> facebookAlbumsList;
    ArrayList<HashMap<String, String>> facebookPhotoList;

    GeneralFunctions generalFunctions;
    ProgressBar loading;

    String storedAccessToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_photos);

        generalFunctions = new GeneralFunctions(getActContext());

        storedAccessToken = generalFunctions.retriveValue(Utils.FACEBOOK_ACCESS_TOKEN_KEY);

        Utils.printLog("storedAccessToken", "::" + storedAccessToken);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        albumRecyclerView = (RecyclerView) findViewById(R.id.albumRecyclerView);
        albumPhotoRecyclerView = (RecyclerView) findViewById(R.id.albumPhotoRecyclerView);
        loading = (ProgressBar) findViewById(R.id.loading);

        albumRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActContext(), Utils.dipToPixels(getActContext(), 110)));
        albumPhotoRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActContext(), Utils.dipToPixels(getActContext(), 110)));
        facebookAlbumsList = new ArrayList<>();
        facebookPhotoList = new ArrayList<>();
        adapter = new FacebookAlbumRecyclerAdapter(getActContext(), facebookAlbumsList);
        adapter_albumPhoto = new FacebookPhotoRecyclerAdapter(getActContext(), facebookPhotoList);

        albumRecyclerView.setAdapter(adapter);
        albumPhotoRecyclerView.setAdapter(adapter_albumPhoto);
        backImgView.setOnClickListener(new setOnClickList());
        titleTxt.setText("Select Album");

        getFacebookAlbums();

        adapter.setOnItemClickList(new FacebookAlbumRecyclerAdapter.OnItemClickList() {
            @Override
            public void onItemClick(int position) {

                getFacebookAlbumPhotos(position);
            }
        });
        adapter_albumPhoto.setOnItemClickList(new FacebookPhotoRecyclerAdapter.OnItemClickList() {
            @Override
            public void onItemClick(int position) {
                HashMap<String, String> map = facebookPhotoList.get(position);

                String item = map.get("PHOTO_PATH");

                Utils.printLog("SelectedPhotoURL", item);

                downloadSelectedPhoto(item);
            }
        });
    }

    public void downloadSelectedPhoto(String url) {

        DownloadImage downloadImg = new DownloadImage(getActContext(), url);
        downloadImg.setImageDownloadList(new DownloadImage.ImageDownloadListener() {
            @Override
            public void onImageDownload(Bitmap bmp) {
                Utils.printLog("Image", "Download:Success");
            }
        });
        downloadImg.execute();
    }

    public void getFacebookAlbums() {

        loading.setVisibility(View.VISIBLE);

        facebookAlbumsList.clear();
        adapter.notifyDataSetChanged();

        String facebookAlbumUrl = "https://graph.facebook.com/me/albums?access_token=" + storedAccessToken;

        Utils.printLog("UrlBanner", facebookAlbumUrl);
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(facebookAlbumUrl, true);
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
                            map.put("ALBUM_ID", generalFunctions.getJsonValue("id", obj_temp.toString()));
                            map.put("ALBUM_NAME", generalFunctions.getJsonValue("name", obj_temp.toString()));

                            facebookAlbumsList.add(map);
                        }

                        adapter.notifyDataSetChanged();
                    }
                } else {
                }
            }
        });
        exeWebServer.execute();
    }

    public void getFacebookAlbumPhotos(int position) {

        loading.setVisibility(View.VISIBLE);

        facebookPhotoList.clear();
        adapter_albumPhoto.notifyDataSetChanged();

        String albumId = facebookAlbumsList.get(position).get("ALBUM_ID");
        String facebookAlbumPhotoUrl = "https://graph.facebook.com/" + albumId + "/photos?fields=album,height,icon,id,images,from,name,link,event,created_time,can_tag&access_token="+storedAccessToken;

        Utils.printLog("UrlBanner", facebookAlbumPhotoUrl);
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(facebookAlbumPhotoUrl, true);
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
                            map.put("PHOTO_ID", generalFunctions.getJsonValue("id", obj_temp.toString()));
                            JSONArray imgArr = generalFunctions.getJsonArr("images", obj_temp.toString());

                            JSONObject obj_img_temp = generalFunctions.getJsonObject(imgArr, 0);

                            map.put("PHOTO_PATH", generalFunctions.getJsonValue("source", obj_img_temp.toString()));

                            facebookPhotoList.add(map);
                        }

                        adapter_albumPhoto.notifyDataSetChanged();

                        albumRecyclerView.setVisibility(View.GONE);
                        albumPhotoRecyclerView.setVisibility(View.VISIBLE);

                        titleTxt.setText("Select Photo");
                    }
                } else {
                }
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return FacebookPhotosActivity.this;
    }

    @Override
    public void onBackPressed() {
        if (albumRecyclerView.getVisibility() != View.VISIBLE) {
            albumPhotoRecyclerView.setVisibility(View.GONE);
            albumRecyclerView.setVisibility(View.VISIBLE);
            titleTxt.setText("Select Album");
            facebookPhotoList.clear();
            adapter_albumPhoto.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    FacebookPhotosActivity.super.onBackPressed();
                    break;
            }
        }
    }
}
