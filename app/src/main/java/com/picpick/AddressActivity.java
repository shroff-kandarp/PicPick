package com.picpick;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapter.MyAddressRecyclerAdapter;
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

public class AddressActivity extends AppCompatActivity implements MyAddressRecyclerAdapter.OnItemClickList {

    TextView titleTxt;
    GeneralFunctions generalFunctions;
    ArrayList<HashMap<String, String>> list_address_items;
    MyAddressRecyclerAdapter myAddressRecyclerAdapter;
    RecyclerView myAddresRecyclerView;
    ImageView backImgView, cartImage;
    Button btn_add_address;

    android.support.v7.app.AlertDialog alertDialog;

    ProgressBar loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        titleTxt = (TextView) findViewById(R.id.titleTxt);

        generalFunctions = new GeneralFunctions(getActContext());
        list_address_items = new ArrayList<>();
        backImgView = (ImageView) findViewById(R.id.backImgView);
        cartImage = (ImageView) findViewById(R.id.imgCart);
        btn_add_address = (Button) findViewById(R.id.btn_add_address);
        loading = (ProgressBar) findViewById(R.id.loading);

        titleTxt.setText("My Addresses");
        backImgView.setOnClickListener(new setOnClickList());
        cartImage.setOnClickListener(new setOnClickList());
        btn_add_address.setOnClickListener(new setOnClickList());
        myAddresRecyclerView = (RecyclerView) findViewById(R.id.myAddressRecyclerView);

        myAddressRecyclerAdapter = new MyAddressRecyclerAdapter(getActContext(), list_address_items);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myAddresRecyclerView.setLayoutManager(mLayoutManager);
        myAddresRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myAddresRecyclerView.setAdapter(myAddressRecyclerAdapter);

        myAddressRecyclerAdapter.setOnItemClickList(this);
        getAddressList();
    }

    public void openAddAddress() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("Add New Address");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);


        final MaterialEditText nameBox = (MaterialEditText) dialogView.findViewById(R.id.editBox);
        final MaterialEditText mobileBox = (MaterialEditText) inflater.inflate(R.layout.editbox_form_design, null);
        final MaterialEditText addressBox = (MaterialEditText) inflater.inflate(R.layout.editbox_form_design, null);
        final MaterialEditText cityBox = (MaterialEditText) inflater.inflate(R.layout.editbox_form_design, null);
        final MaterialEditText stateBox = (MaterialEditText) inflater.inflate(R.layout.editbox_form_design, null);
        final MaterialEditText countryBox = (MaterialEditText) inflater.inflate(R.layout.editbox_form_design, null);
        final MaterialEditText pinCodeBox = (MaterialEditText) inflater.inflate(R.layout.editbox_form_design, null);

        mobileBox.setId(Utils.generateViewId());
        addressBox.setId(Utils.generateViewId());
        cityBox.setId(Utils.generateViewId());
        stateBox.setId(Utils.generateViewId());
        countryBox.setId(Utils.generateViewId());
        pinCodeBox.setId(Utils.generateViewId());

        mobileBox.setLayoutParams(nameBox.getLayoutParams());
        addressBox.setLayoutParams(nameBox.getLayoutParams());
        cityBox.setLayoutParams(nameBox.getLayoutParams());
        stateBox.setLayoutParams(nameBox.getLayoutParams());
        countryBox.setLayoutParams(nameBox.getLayoutParams());
        pinCodeBox.setLayoutParams(nameBox.getLayoutParams());

        nameBox.setBothText("Name", "Enter your name");
        mobileBox.setBothText("Mobile", "Enter your mobile number");
        addressBox.setBothText("Address", "Enter your address");
        cityBox.setBothText("City", "Enter your city");
        stateBox.setBothText("State", "Enter your state");
        countryBox.setBothText("Country", "Enter your country");
        pinCodeBox.setBothText("PinCode", "Enter pincode");


        ((LinearLayout) dialogView.findViewById(R.id.container)).addView(mobileBox);
        ((LinearLayout) dialogView.findViewById(R.id.container)).addView(addressBox);
        ((LinearLayout) dialogView.findViewById(R.id.container)).addView(cityBox);
        ((LinearLayout) dialogView.findViewById(R.id.container)).addView(stateBox);
        ((LinearLayout) dialogView.findViewById(R.id.container)).addView(countryBox);
        ((LinearLayout) dialogView.findViewById(R.id.container)).addView(pinCodeBox);

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


        alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean nameEntered = Utils.checkText(nameBox) ? true : Utils.setErrorFields(nameBox, "Required");
                boolean mobileEntered = Utils.checkText(mobileBox) ? true : Utils.setErrorFields(mobileBox, "Required");
                boolean addressEntered = Utils.checkText(addressBox) ? true : Utils.setErrorFields(addressBox, "Required");
                boolean cityEntered = Utils.checkText(cityBox) ? true : Utils.setErrorFields(cityBox, "Required");
                boolean stateEntered = Utils.checkText(stateBox) ? true : Utils.setErrorFields(stateBox, "Required");
                boolean countryEntered = Utils.checkText(countryBox) ? true : Utils.setErrorFields(countryBox, "Required");
                boolean pincodeEntered = Utils.checkText(pinCodeBox) ? true : Utils.setErrorFields(pinCodeBox, "Required");
                if (nameEntered == false || mobileEntered == false || addressEntered == false || cityEntered == false ||
                        stateEntered == false || countryEntered == false || pincodeEntered == false) {
                    return;
                }
                alertDialog.dismiss();
                HashMap<String, String> parameters = new HashMap<String, String>();

                parameters.put("type", "configUserAddress");
                parameters.put("iMemberId", generalFunctions.getMemberId());
                parameters.put("vName", Utils.getText(nameBox));
                parameters.put("vMobile", Utils.getText(mobileBox));
                parameters.put("vAddress", Utils.getText(addressBox));
                parameters.put("vCity", Utils.getText(cityBox));
                parameters.put("vCountry", Utils.getText(countryBox));
                parameters.put("vState", Utils.getText(stateBox));
                parameters.put("vPinCode", Utils.getText(pinCodeBox));
                addAddress(parameters);
            }
        });
    }

    public void addAddress(HashMap<String, String> parameters) {

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    if (generalFunctions.isDataAvail("Action", responseString)) {

                        generalFunctions.showGeneralMessage("", generalFunctions.getJsonValue("message", responseString));
                        getAddressList();
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

    private void getAddressList() {

        loading.setVisibility(View.VISIBLE);
        list_address_items.clear();
        myAddressRecyclerAdapter.notifyDataSetChanged();
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "loadUserAddress");
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


                                String address_id = generalFunctions.getJsonValue("iAddressId", generalFunctions.getJsonObject(arr, i).toString());
                                String user_id = generalFunctions.getJsonValue("iUserId", generalFunctions.getJsonObject(arr, i).toString());
                                String name = generalFunctions.getJsonValue("vName", generalFunctions.getJsonObject(arr, i).toString());
                                String mobile = generalFunctions.getJsonValue("vMobile", generalFunctions.getJsonObject(arr, i).toString());
                                String address = generalFunctions.getJsonValue("vAddress", generalFunctions.getJsonObject(arr, i).toString());
                                String country = generalFunctions.getJsonValue("vCountry", generalFunctions.getJsonObject(arr, i).toString());
                                String state = generalFunctions.getJsonValue("vState", generalFunctions.getJsonObject(arr, i).toString());
                                String city = generalFunctions.getJsonValue("vCity", generalFunctions.getJsonObject(arr, i).toString());
                                String pincode = generalFunctions.getJsonValue("vPinCode", generalFunctions.getJsonObject(arr, i).toString());


                                HashMap<String, String> map_data = new HashMap<String, String>();
                                map_data.put("iAddressId", address_id);
                                map_data.put("iUserId", user_id);
                                map_data.put("vName", name);
                                map_data.put("vMobile", mobile);
                                map_data.put("vAddress", address);
                                map_data.put("vCountry", country);
                                map_data.put("vState", state);
                                map_data.put("vCity", city);
                                map_data.put("vPinCode", pincode);


                                Utils.printLog("iAddressId:loop", "::" + map_data.get("iAddressId"));
                                list_address_items.add(map_data);

                            }

                            myAddressRecyclerAdapter.notifyDataSetChanged();

                        }
                    } else {
                        generalFunctions.showGeneralMessage("Opps!!", generalFunctions.getJsonValue("message", responseString));
                    }
                } else {
                    generalFunctions.showGeneralMessage("Error", "Please try again later.");
                }
            }
        });
        exeWebServer.execute();


    }

    @Override
    public void onItemClick(int position, int btn_id) {
        switch (btn_id) {
            case 0:
                Bundle bn = new Bundle();
                bn.putString("iAddressId", list_address_items.get(position).get("iAddressId"));
                (new StartActProcess(getActContext())).setOkResult(bn);
                backImgView.performClick();
                break;
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    AddressActivity.super.onBackPressed();
                    break;
                case R.id.imgCart:
                    Intent cart = new Intent(AddressActivity.this, MyCartActivity.class);
                    startActivity(cart);
                    break;
                case R.id.btn_add_address:
                    openAddAddress();
                    break;
            }
        }
    }

    public Context getActContext() {
        return AddressActivity.this;
    }

}
