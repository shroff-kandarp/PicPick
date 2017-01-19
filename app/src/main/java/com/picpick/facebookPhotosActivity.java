package com.picpick;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapter.FacebookAlbumRecyclerAdapter;
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

    FacebookAlbumRecyclerAdapter adapter;


    ArrayList<HashMap<String, String>> facebookAlbumsList;

    GeneralFunctions generalFunctions;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_photos);

        generalFunctions = new GeneralFunctions(getActContext());
        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        albumRecyclerView = (RecyclerView) findViewById(R.id.albumRecyclerView);
        loading = (ProgressBar) findViewById(R.id.loading);

        albumRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActContext(), Utils.dipToPixels(getActContext(), 110)));
        facebookAlbumsList = new ArrayList<>();
        adapter = new FacebookAlbumRecyclerAdapter(getActContext(), facebookAlbumsList);

        albumRecyclerView.setAdapter(adapter);
        backImgView.setOnClickListener(new setOnClickList());
        titleTxt.setText("Select Photos");

        getFacebookAlbums();
    }

    public void getFacebookAlbums() {

        loading.setVisibility(View.VISIBLE);

        String facebookAlbumUrl = "https://graph.facebook.com/me/albums?access_token=EAACEdEose0cBAPjZAhTw7qkIxaXrajnOP2ZCEGKN690EoSVN9cu0o2OxJCDRrLyCBB0ZAhZBTLKLtG9tX9NcLPY93F4m69ZCQLNusCGPWmrYEkkif6dITKbqGWrhbKb8EEbDBNoFZBIUBnZA1pl9ZAmeb2ZBGJaV6kI3U8xaL7xPERQZDZD";

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

    public Context getActContext() {
        return FacebookPhotosActivity.this;
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
