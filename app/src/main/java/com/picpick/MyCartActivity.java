package com.picpick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapter.MyCartRecyclerAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GenerateAlertBox;
import com.general.files.StartActProcess;
import com.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ravi on 22-01-2017.
 */

public class MyCartActivity extends AppCompatActivity implements MyCartRecyclerAdapter.OnItemClickList {

    TextView titleTxt;

    GeneralFunctions generalFunctions;
    ArrayList<HashMap<String, String>> list_cart_items;
    MyCartRecyclerAdapter mycartRecyclerAdapter;
    RecyclerView myCartRecyclerView;
    ImageView backImgView, cartImage;

    ProgressBar loading;
    Button placeOrderBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);


        generalFunctions = new GeneralFunctions(getActContext());
        list_cart_items = new ArrayList<>();
        backImgView = (ImageView) findViewById(R.id.backImgView);
        cartImage = (ImageView) findViewById(R.id.imgCart);
        loading = (ProgressBar) findViewById(R.id.loading);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        placeOrderBtn = (Button) findViewById(R.id.placeOrderBtn);

        placeOrderBtn.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        cartImage.setOnClickListener(new setOnClickList());
        cartImage.setVisibility(View.GONE);
        titleTxt.setText("My Cart");
        myCartRecyclerView = (RecyclerView) findViewById(R.id.myCartRecyclerView);


        mycartRecyclerAdapter = new MyCartRecyclerAdapter(getActContext(), list_cart_items);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myCartRecyclerView.setLayoutManager(mLayoutManager);
        myCartRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myCartRecyclerView.setAdapter(mycartRecyclerAdapter);

        mycartRecyclerAdapter.setOnItemClickList(this);

        getMyCart();
    }

    public void getMyCart() {

        list_cart_items.clear();
        mycartRecyclerAdapter.notifyDataSetChanged();
        loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "loadUserCart");
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

                                String cartId = generalFunctions.getJsonValue("iCartId", generalFunctions.getJsonObject(arr, i).toString());
                                String caption = generalFunctions.getJsonValue("tCaption", generalFunctions.getJsonObject(arr, i).toString());

                                HashMap<String, String> map_data = new HashMap<String, String>();
                                map_data.put("ImgId", imageId);
                                map_data.put("ImgPath", image_url);
                                map_data.put("UserId", userId);
                                map_data.put("CartId", cartId);
                                map_data.put("Caption", caption);

                                list_cart_items.add(map_data);

                            }

                            mycartRecyclerAdapter.notifyDataSetChanged();

                            placeOrderBtn.setVisibility(View.VISIBLE);
                        }
                    } else {
                        generalFunctions.showGeneralMessage("", generalFunctions.getJsonValue("message", responseString));

                        placeOrderBtn.setVisibility(View.GONE);
                    }
                } else {
                    generalFunctions.showGeneralMessage("Error", "Please try again later.");
                    placeOrderBtn.setVisibility(View.GONE);
                }
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onItemClick(final int position, int btn_id) {

        if (btn_id == 0) {
            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
            generateAlert.setCancelable(false);
            generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                @Override
                public void handleBtnClick(int btn_id) {
                    generateAlert.closeAlertBox();

                    if (btn_id == 1) {
                        deleteItem(position);
                    }
                }
            });
            generateAlert.setContentMessage("", "Are you sure, you want to delete?");
            generateAlert.setPositiveBtn("Ok");
            generateAlert.setNegativeBtn("Cancel");

            generateAlert.showAlertBox();
        }

    }

    public void deleteItem(int position) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "deleteUserCart");
        parameters.put("iMemberId", generalFunctions.getMemberId());
        parameters.put("iCartId", list_cart_items.get(position).get("CartId"));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    if (generalFunctions.isDataAvail("Action", responseString)) {
                        getMyCart();
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
                    MyCartActivity.super.onBackPressed();
                    break;
                case R.id.placeOrderBtn:
                    selectAddress();
                    break;
            }
        }
    }

    public void selectAddress() {
        (new StartActProcess(getActContext())).startActForResult(AddressActivity.class, Utils.ACT_REQ_CODE_ADDRESS_SELECT);
//        placeOrder("0");
    }

    public String getCartIds() {

        String cartIds = "";
        for (int i = 0; i < list_cart_items.size(); i++) {

            cartIds = (i == 0) ? list_cart_items.get(i).get("CartId") : (cartIds +"," + list_cart_items.get(i).get("CartId"));
        }
        return cartIds;
    }

    public void placeOrder(String iAddressId) {
        Utils.printLog("getCartIds", "::" + getCartIds());
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "createOrder");
        parameters.put("iMemberId", generalFunctions.getMemberId());
        parameters.put("iCartId", getCartIds());
        parameters.put("iAddressId", iAddressId);

        Utils.printLog("parameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    if (generalFunctions.isDataAvail("Action", responseString)) {
//                        getMyCart();

                        GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                        alertBox.setContentMessage("", generalFunctions.getJsonValue("message", responseString));
                        alertBox.setCancelable(false);
                        alertBox.setPositiveBtn("Ok");
                        alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                            @Override
                            public void handleBtnClick(int btn_id) {

                                if (btn_id == 1) {
                                    backImgView.performClick();
                                }
                            }
                        });
                        alertBox.showAlertBox();
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

    public Context getActContext() {
        return MyCartActivity.this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.ACT_REQ_CODE_ADDRESS_SELECT && resultCode == RESULT_OK && data != null) {
            Utils.printLog("iAddressId", data.getStringExtra("iAddressId"));
            placeOrder(data.getStringExtra("iAddressId"));
        }
    }
}
