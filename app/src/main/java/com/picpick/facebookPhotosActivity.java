package com.picpick;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FacebookPhotosActivity extends AppCompatActivity {

    ImageView backImgView;
    TextView titleTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_photos);

        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (TextView) findViewById(R.id.titleTxt);

        backImgView.setOnClickListener(new setOnClickList());
        titleTxt.setText("Select Photos");
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    FacebookPhotosActivity.super.onBackPressed();
                    break;
            }
        }
    }
}
