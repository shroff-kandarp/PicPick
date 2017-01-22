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
import android.widget.ImageView;
import android.widget.TextView;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.picpick.R;
import com.squareup.picasso.Picasso;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 04-07-2016.
 */
public class MyCartRecyclerAdapter extends RecyclerView.Adapter<MyCartRecyclerAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> list_item;
    Context mContext;
    AlertDialog.Builder alert;
    OnItemClickList onItemClickList;
    GeneralFunctions generalFunctions;
    MyCartRecyclerAdapter adapter;
    HashMap<String, String> item;

    boolean isFirstRun = true;

    public MyCartRecyclerAdapter(Context mContext, ArrayList<HashMap<String, String>> list_item) {
        this.mContext = mContext;
        this.list_item = list_item;
        generalFunctions = new GeneralFunctions(mContext);
        this.adapter = this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof MyCartRecyclerAdapter.ViewHolder) {
             item = list_item.get(position);
            final String userId = item.get("UserId");
            final String imageId= item.get("ImgId");
            final String cartId = item.get("CartId");
            final String caption = item.get("Caption");



            Picasso.with(mContext)
                    .load(item.get("ImgPath"))
                    .into(viewHolder.myImage);

            viewHolder.tv_caption.setText(caption);

            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    alert = new AlertDialog.Builder(mContext);

                    final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog, null);
                    alert.setView(dialogView);

                    final EditText edittext = new EditText(mContext);
                    //alert.setMessage("Caption message");

                    alert.setTitle("Delete image from cart?");

                    alert.setView(edittext);

                    //final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {



                                HashMap<String, String> parameters = new HashMap<>();

                                parameters.put("type", "deleteUserCart");
                                parameters.put("iCartId",cartId);



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


        public ImageView myImage;
        public Button delete;
        public TextView tv_caption;


        public ViewHolder(View view) {
            super(view);

            myImage = (ImageView) view.findViewById(R.id.img_mycart);
            delete = (Button) view.findViewById(R.id.btn_delete);
            tv_caption = (TextView) view.findViewById(R.id.tv_caption);

        }
    }

    @Override
    public int getItemCount() {
        return list_item.size();
    }

    public interface OnItemClickList {
        void onItemClick(int position);
    }

    public void setOnItemClickList(OnItemClickList onItemClickList) {
        this.onItemClickList = onItemClickList;
    }

    public void clickOnItem(int position) {
        if (onItemClickList != null) {
            onItemClickList.onItemClick(position);
        }
    }

}
