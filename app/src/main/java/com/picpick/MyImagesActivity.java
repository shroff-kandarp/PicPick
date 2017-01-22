package com.picpick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.adapter.MyImagesRecyclerAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ravi on 22-01-2017.
 */

public class MyImagesActivity extends AppCompatActivity {

    private String memberId;
    GeneralFunctions generalFunctions;
    ArrayList<HashMap<String, String>> list_images;
    MyImagesRecyclerAdapter myImagesRecyclerAdapter;
    RecyclerView myImagesRecyclerView;
    ImageView backImgView,cartImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_images);

        generalFunctions = new GeneralFunctions(getActContext());
        list_images = new ArrayList<>();
        backImgView = (ImageView) findViewById(R.id.backImgView);
        cartImage = (ImageView) findViewById(R.id.imgCart);
        memberId = generalFunctions.getMemberId();
        Log.d("MEMBER ID",memberId);

        backImgView.setOnClickListener(new setOnClickList());
        cartImage.setOnClickListener(new setOnClickList());


        myImagesRecyclerView = (RecyclerView) findViewById(R.id.myImagesRecyclerView);


        myImagesRecyclerAdapter = new MyImagesRecyclerAdapter(getActContext(), list_images);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myImagesRecyclerView.setLayoutManager(mLayoutManager);
        myImagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myImagesRecyclerView.setAdapter(myImagesRecyclerAdapter);




        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "loadUserImages");
        parameters.put("iMemberId",memberId);





        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    if (generalFunctions.isDataAvail("Action", responseString)) {





                        JSONArray arr = generalFunctions.getJsonArr("message", responseString);
                        Utils.printLog("arr", "::" + arr);
                        if (arr != null) {
                            for (int i = 0; i < arr.length(); i++) {


                                String image_url = generalFunctions.getJsonValue("vImage", generalFunctions.getJsonObject(arr, i).toString());
                                Utils.printLog("image_url", "::" + image_url);
                                String imageId = generalFunctions.getJsonValue("iImgId", generalFunctions.getJsonObject(arr, i).toString());
                                String userId = generalFunctions.getJsonValue("iUserId", generalFunctions.getJsonObject(arr, i).toString());


                                HashMap<String, String> map_data = new HashMap<String, String>();
                                map_data.put("ImgId", imageId);
                                map_data.put("ImgPath", image_url);
                                map_data.put("UserId", userId);

                                list_images.add(map_data);

                            }

                            myImagesRecyclerAdapter.notifyDataSetChanged();

//

                    } else {
                        generalFunctions.showGeneralMessage("Error", generalFunctions.getJsonValue("message", responseString));
                    }
                } else {
                    generalFunctions.showGeneralMessage("Error", "Please try again later.");
                }
            }
            }
        });
        exeWebServer.execute();




    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    MyImagesActivity.super.onBackPressed();
                    break;
                case R.id.imgCart:
                    Intent cart = new Intent(MyImagesActivity.this, MyCartActivity.class);
                    startActivity(cart);

                    break;
            }
        }
    }

    public Context getActContext() {
        return MyImagesActivity.this;
    }
}
