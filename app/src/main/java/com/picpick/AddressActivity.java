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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.adapter.MyAddressRecyclerAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ravi on 22-01-2017.
 */

public class AddressActivity extends AppCompatActivity {

    AlertDialog.Builder alert;

    private String memberId;
    GeneralFunctions generalFunctions;
    ArrayList<HashMap<String, String>> list_images;
    MyAddressRecyclerAdapter myAddressRecyclerAdapter;
    RecyclerView myAddresRecyclerView;
    ImageView backImgView,cartImage;
    Button btn_add;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_address);


        btn_add = (Button) findViewById(R.id.btn_add_address);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alert = new AlertDialog.Builder(getActContext());

                final View dialogView = LayoutInflater.from(getActContext()).inflate(R.layout.custom_address_dialog, null);
                alert.setView(dialogView);

                //final EditText edittext = new EditText(getActContext());
                //alert.setMessage("Caption message");

                alert.setTitle("Add New Address");

                //alert.setView(edittext);

                final EditText edt_fullname = (EditText) dialogView.findViewById(R.id.edt_name);
                final EditText edt_mob = (EditText) dialogView.findViewById(R.id.edt_mob);
                final EditText edt_address = (EditText) dialogView.findViewById(R.id.edt_address);
                final EditText edt_city = (EditText) dialogView.findViewById(R.id.edt_city);
                final EditText edt_state = (EditText) dialogView.findViewById(R.id.edt_state);
                final EditText edt_country = (EditText) dialogView.findViewById(R.id.edt_country);
                final EditText edt_pincode = (EditText) dialogView.findViewById(R.id.edt_pincode);

                alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {



                        HashMap<String, String> parameters = new HashMap<>();

                        parameters.put("type", "configUserAddress");
                        parameters.put("iMemberId",memberId);
                        parameters.put("vName",edt_fullname.getText().toString());
                        parameters.put("vMobile",edt_mob.getText().toString());
                        parameters.put("vAddress",edt_address.getText().toString());
                        parameters.put("vCity",edt_city.getText().toString());
                        parameters.put("vCountry",edt_country.getText().toString());
                        parameters.put("vState",edt_state.getText().toString());
                        parameters.put("vPinCode",edt_pincode.getText().toString());

                        String vName = edt_fullname.getText().toString();

                        String vMobile = edt_mob.getText().toString();
                        String vAddress = edt_address.getText().toString();
                        String vCity = edt_city.getText().toString();
                        String vCountry = edt_country.getText().toString();
                        String vState = edt_state.getText().toString();
                        String vPinCode = edt_pincode.getText().toString();


                        if(vName.length()>2&& vMobile.length()==10 && vAddress.length()>5 && vCity.length()>1 && vCountry.length()>1 && vState.length()>1 && vPinCode.length()>1)
                        {


                        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
                        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
                        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
                            @Override
                            public void setResponse(String responseString) {

                                Utils.printLog("Response", "::" + responseString);

                                if (responseString != null && !responseString.equals("")) {

                                    if (generalFunctions.isDataAvail("Action", responseString)) {

                                            String msg = generalFunctions.getJsonValue("message",responseString);
                                        Toast.makeText(getActContext(),msg,Toast.LENGTH_LONG).show();


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
                        else
                        {

                            Toast.makeText(getActContext(),"Add all details", Toast.LENGTH_LONG).show();

                        }






                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                    }
                });

                alert.show();







            }
        });

        generalFunctions = new GeneralFunctions(getActContext());
        list_images = new ArrayList<>();
        backImgView = (ImageView) findViewById(R.id.backImgView);
        cartImage = (ImageView) findViewById(R.id.imgCart);
        memberId = generalFunctions.getMemberId();
        Log.d("MEMBER ID",memberId);

        backImgView.setOnClickListener(new setOnClickList());
        cartImage.setOnClickListener(new setOnClickList());


        myAddresRecyclerView = (RecyclerView) findViewById(R.id.myAddressRecyclerView);


        myAddressRecyclerAdapter = new MyAddressRecyclerAdapter(getActContext(), list_images);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myAddresRecyclerView.setLayoutManager(mLayoutManager);
        myAddresRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myAddresRecyclerView.setAdapter(myAddressRecyclerAdapter);

        getAddressList();

    }

    private void getAddressList() {



        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "loadUserAddress");
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


                                list_images.add(map_data);

                            }

                            myAddressRecyclerAdapter.notifyDataSetChanged();

//

                        }
                    } else {
                        generalFunctions.showGeneralMessage("Opps!!", generalFunctions.getJsonValue("message", responseString));
                    }
                }
                else {
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
                    AddressActivity.super.onBackPressed();
                    break;
                case R.id.imgCart:
                    Intent cart = new Intent(AddressActivity.this, MyCartActivity.class);
                    startActivity(cart);

                    break;
            }
        }
    }

    public Context getActContext() {
        return AddressActivity.this;
    }

}
