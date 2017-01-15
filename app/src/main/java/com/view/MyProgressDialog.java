package com.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.picpick.R;


/**
 * Created by Admin on 29-01-2016.
 */
public class MyProgressDialog {
    Context mContext;
    boolean cancelable;
    Dialog my_progress_dialog;

    public MyProgressDialog(Context mContext, boolean cancelable, String message_str) {
        this.mContext = mContext;
        this.cancelable = cancelable;

        build();
        setMessage(message_str);
    }

    public void build() {
        my_progress_dialog = new Dialog(mContext, R.style.theme_my_progress_dialog);

        my_progress_dialog.setContentView(R.layout.my_progress_dilalog_design);

        Window window = my_progress_dialog.getWindow();
        window.setGravity(Gravity.CENTER);

        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        my_progress_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        my_progress_dialog.setCanceledOnTouchOutside(false);
        my_progress_dialog.setCancelable(cancelable);

    }

    public void setMessage(String msg_str) {
        TextView msgTxt = (TextView) my_progress_dialog.findViewById(R.id.msgTxt);
        msgTxt.setText(msg_str);
    }

    public void show() {

        my_progress_dialog.show();
    }

    public void close() {

        my_progress_dialog.dismiss();
    }

}