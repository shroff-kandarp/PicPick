package com.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.picpick.R;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 04-07-2016.
 */
public class MyAddressRecyclerAdapter extends RecyclerView.Adapter<MyAddressRecyclerAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> list_item;
    Context mContext;
    AlertDialog.Builder alert;
    OnItemClickList onItemClickList;
    GeneralFunctions generalFunctions;
    MyAddressRecyclerAdapter adapter;
    HashMap<String, String> item;
    String iAddressId;

    boolean isFirstRun = true;

    public MyAddressRecyclerAdapter(Context mContext, ArrayList<HashMap<String, String>> list_item) {
        this.mContext = mContext;
        this.list_item = list_item;
        generalFunctions = new GeneralFunctions(mContext);
        this.adapter = this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_address, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof MyAddressRecyclerAdapter.ViewHolder) {
            item = list_item.get(position);

            String vName = item.get("vName");
            iAddressId = item.get("iAddressId");
            String vMobile = item.get("vMobile");
            String vAddress = item.get("vAddress");
            String vCity = item.get("vCity");
            String vCountry = item.get("vCountry");
            String vState = item.get("vState");
            String vPinCode = item.get("vPinCode");


            viewHolder.tv_name.setText(vName);
            viewHolder.tv_mobile.setText(vMobile);
            viewHolder.tv_address.setText(vAddress);
            viewHolder.tv_city.setText(vCity);
            viewHolder.tv_country.setText(vCountry);
            viewHolder.tv_state.setText(vState);
            viewHolder.tv_pincode.setText(vPinCode);

            viewHolder.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickList != null) {
                        onItemClickList.onItemClick(position, 0);
                    }
                }
            });

            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    alert = new AlertDialog.Builder(mContext);

                    final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog, null);
                    alert.setView(dialogView);

                    final EditText edittext = new EditText(mContext);
                    //alert.setMessage("Caption message");

                    alert.setTitle("Delete Address?");

                    alert.setView(edittext);

                    //final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {


                            HashMap<String, String> parameters = new HashMap<>();

                            parameters.put("type", "deleteUserAddress");
                            parameters.put("iAddressId", iAddressId);


                            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
                            exeWebServer.setLoaderConfig(mContext, true, generalFunctions);
                            exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
                                @Override
                                public void setResponse(String responseString) {

                                    Utils.printLog("Response", "::" + responseString);

                                    if (responseString != null && !responseString.equals("")) {

                                        if (generalFunctions.isDataAvail("Action", responseString)) {


                                            list_item.remove(item); //Actually change your list of items here
                                            adapter.notifyDataSetChanged();


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
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {


                        }
                    });

                    alert.show();


                }
            });


        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        View containerView;
        public Button delete, update;
        public TextView tv_name, tv_mobile, tv_address, tv_city, tv_state, tv_country, tv_pincode;


        public ViewHolder(View view) {
            super(view);

            containerView = view.findViewById(R.id.containerView);
            delete = (Button) view.findViewById(R.id.btn_delete_address);
            update = (Button) view.findViewById(R.id.btn_update_address);

            tv_name = (TextView) view.findViewById(R.id.tv_fullname);
            tv_mobile = (TextView) view.findViewById(R.id.tv_mobile);
            tv_address = (TextView) view.findViewById(R.id.tv_address);
            tv_city = (TextView) view.findViewById(R.id.tv_city);
            tv_state = (TextView) view.findViewById(R.id.tv_state);
            tv_country = (TextView) view.findViewById(R.id.tv_country);
            tv_pincode = (TextView) view.findViewById(R.id.tv_pincode);

        }
    }

    @Override
    public int getItemCount() {
        return list_item.size();
    }

    public interface OnItemClickList {
        void onItemClick(int position, int btn_id);
    }

    public void setOnItemClickList(OnItemClickList onItemClickList) {
        this.onItemClickList = onItemClickList;
    }

}
