package com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.general.files.GeneralFunctions;
import com.picpick.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 04-07-2016.
 */
public class MyImagesRecyclerAdapter extends RecyclerView.Adapter<MyImagesRecyclerAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> list_item;
    Context mContext;
    OnItemClickList onItemClickList;
    GeneralFunctions generalFunctions;

    public MyImagesRecyclerAdapter(Context mContext, ArrayList<HashMap<String, String>> list_item) {
        this.mContext = mContext;
        this.list_item = list_item;
        generalFunctions = new GeneralFunctions(mContext);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_images, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof MyImagesRecyclerAdapter.ViewHolder) {
            final HashMap<String, String> item = list_item.get(position);
            final String userId = item.get("UserId");
            final String imageId = item.get("ImgId");

            Picasso.with(mContext)
                    .load(item.get("ImgPath"))
                    .into(viewHolder.myImage);

            viewHolder.addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onItemClickList != null) {
                        onItemClickList.onItemClick(position, 0);
                    }

                }
            });


        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ImageView myImage;
        public Button addToCart;


        public ViewHolder(View view) {
            super(view);

            myImage = (ImageView) view.findViewById(R.id.my_image);
            addToCart = (Button) view.findViewById(R.id.btn_addToCart);

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
