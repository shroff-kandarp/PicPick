package com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.picpick.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 04-07-2016.
 */
public class InstaPhotoRecyclerAdapter extends RecyclerView.Adapter<InstaPhotoRecyclerAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> list_item;
    Context mContext;

    OnItemClickList onItemClickList;

    boolean isFirstRun = true;

    public InstaPhotoRecyclerAdapter(Context mContext, ArrayList<HashMap<String, String>> list_item) {
        this.mContext = mContext;
        this.list_item = list_item;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_insta_photo, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        HashMap<String, String> map = list_item.get(position);

        String item = map.get("PHOTO_PATH");

        Picasso.with(mContext)
                .load(item)
                .into(viewHolder.imgView);

        viewHolder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickList != null) {
                    onItemClickList.onItemClick(position);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgView;

        public LinearLayout contentView;

        public ViewHolder(View view) {
            super(view);
            imgView = (ImageView) view.findViewById(R.id.imgView);
            contentView = (LinearLayout) view.findViewById(R.id.contentView);
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
