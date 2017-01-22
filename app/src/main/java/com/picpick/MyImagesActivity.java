package com.picpick;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapter.MyImagesRecyclerAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ravi on 22-01-2017.
 */

public class MyImagesActivity extends AppCompatActivity implements MyImagesRecyclerAdapter.OnItemClickList {

    GeneralFunctions generalFunctions;
    ArrayList<HashMap<String, String>> list_images;
    MyImagesRecyclerAdapter myImagesRecyclerAdapter;
    RecyclerView myImagesRecyclerView;
    ImageView backImgView, cartImage;

    ProgressBar loading;
    TextView titleTxt;

    android.support.v7.app.AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_images);

        generalFunctions = new GeneralFunctions(getActContext());
        list_images = new ArrayList<>();
        backImgView = (ImageView) findViewById(R.id.backImgView);
        cartImage = (ImageView) findViewById(R.id.imgCart);

        loading = (ProgressBar) findViewById(R.id.loading);
        titleTxt = (TextView) findViewById(R.id.titleTxt);

        backImgView.setOnClickListener(new setOnClickList());
        cartImage.setOnClickListener(new setOnClickList());

        titleTxt.setText("My Images");

        myImagesRecyclerView = (RecyclerView) findViewById(R.id.myImagesRecyclerView);


        myImagesRecyclerAdapter = new MyImagesRecyclerAdapter(getActContext(), list_images);

        myImagesRecyclerView.setLayoutManager(new LinearLayoutManager(getActContext()));
        myImagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myImagesRecyclerView.setAdapter(myImagesRecyclerAdapter);


        getMyImages();

        myImagesRecyclerAdapter.setOnItemClickList(this);
    }

    public void getMyImages() {

        list_images.clear();
        myImagesRecyclerAdapter.notifyDataSetChanged();
        loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "loadUserImages");
        parameters.put("iMemberId", generalFunctions.getMemberId());


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
//        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);
                loading.setVisibility(View.GONE);
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

    @Override
    public void onItemClick(final int position, int btn_id) {
        if (btn_id == 0) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
            builder.setTitle("Add caption to image");

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.input_box_view, null);


            final MaterialEditText captionBox = (MaterialEditText) dialogView.findViewById(R.id.editBox);

            captionBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            captionBox.setGravity(Gravity.TOP);
            captionBox.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
            captionBox.setBothText("", "Enter your caption");


            builder.setView(dialogView);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setNeutralButton("Skip", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addImgToCart(position, "");
                }
            });


            alertDialog = builder.create();
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Utils.checkText(captionBox) == false) {
                        captionBox.setError("Required");
                        return;
                    }
                    alertDialog.dismiss();
                    addImgToCart(position, Utils.getText(captionBox));
                }
            });
        }
    }

    public void addImgToCart(int position, String caption) {
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "addToCart");
        parameters.put("iMemberId", generalFunctions.getMemberId());
        parameters.put("iImgId", list_images.get(position).get("ImgId"));
        parameters.put("tCaption", caption);

        Utils.printLog("Params", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    if (generalFunctions.isDataAvail("Action", responseString)) {

                        generalFunctions.showGeneralMessage("", generalFunctions.getJsonValue("message", responseString));

                    } else {
                        generalFunctions.showGeneralMessage("Error", generalFunctions.getJsonValue("message", responseString));
                    }
                } else {
                    generalFunctions.showGeneralMessage("Error", "Please try again later.");
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
                    (new StartActProcess(getActContext())).startAct(MyCartActivity.class);
                    break;
            }
        }
    }

    public Context getActContext() {
        return MyImagesActivity.this;
    }
}
