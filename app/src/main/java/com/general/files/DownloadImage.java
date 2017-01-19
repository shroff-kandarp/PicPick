package com.general.files;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.view.MyProgressDialog;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Admin on 15-07-2016.
 */
public class DownloadImage extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    Bitmap myBitmap = null;

    ImageDownloadListener imgDownloadList = null;
    MyProgressDialog myPDialog;
    Context mContext;
    public DownloadImage(Context mContext,String url) {
        this.mContext = mContext;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        myPDialog = new MyProgressDialog(mContext, true, "Loading");
        myPDialog.show();
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {

            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            myBitmap = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        myPDialog.close();

        if (result == null) {

        } else {

            if (imgDownloadList != null) {
                imgDownloadList.onImageDownload(result);
            }
        }
    }

    public interface ImageDownloadListener {
        void onImageDownload(Bitmap bmp);
    }

    public void setImageDownloadList(ImageDownloadListener imgDownloadList) {
        this.imgDownloadList = imgDownloadList;
    }
}
