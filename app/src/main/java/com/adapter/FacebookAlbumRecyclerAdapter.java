package com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.picpick.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 04-07-2016.
 */
public class FacebookAlbumRecyclerAdapter extends RecyclerView.Adapter<FacebookAlbumRecyclerAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> list_item;
    Context mContext;

    OnItemClickList onItemClickList;

    boolean isFirstRun = true;

    public FacebookAlbumRecyclerAdapter(Context mContext, ArrayList<HashMap<String, String>> list_item) {
        this.mContext = mContext;
        this.list_item = list_item;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facebook_album, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        HashMap<String, String> map = list_item.get(position);

        String item = map.get("ALBUM_NAME");


        if (item.length() >= 1) {
            viewHolder.albumNameHTxt.setText(item.substring(0, 1));
        } else {
            viewHolder.albumNameHTxt.setText("--");
        }
        viewHolder.albumNameTxt.setText(item);
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

        public TextView albumNameHTxt;
        public TextView albumNameTxt;

        public LinearLayout contentView;

        public ViewHolder(View view) {
            super(view);
            albumNameHTxt = (TextView) view.findViewById(R.id.albumNameHTxt);
            albumNameTxt = (TextView) view.findViewById(R.id.albumNameTxt);
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
