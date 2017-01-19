package com.picpick;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adapter.FacebookAlbumRecyclerAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.GridAutofitLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;

public class FacebookPhotosActivity extends AppCompatActivity {

    ImageView backImgView;
    TextView titleTxt;

    RecyclerView albumRecyclerView;

    FacebookAlbumRecyclerAdapter adapter;


    ArrayList<HashMap<String,String>> facebookAlbumsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_photos);

        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        albumRecyclerView = (RecyclerView) findViewById(R.id.albumRecyclerView);

        albumRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActContext(), Utils.dipToPixels(getActContext(), 110)));
        facebookAlbumsList = new ArrayList<>();
        adapter = new FacebookAlbumRecyclerAdapter(getActContext(),facebookAlbumsList);

        albumRecyclerView.setAdapter(adapter);
        backImgView.setOnClickListener(new setOnClickList());
        titleTxt.setText("Select Photos");
    }

    public void getFacebookAlbums(){
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "home_slider");

        Utils.printLog("UrlBanner", "::" + CommonUtilities.SERVER_URL_WEBSERVICE + "" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

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
