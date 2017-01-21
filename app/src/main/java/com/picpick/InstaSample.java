package com.picpick;

/**
 * Created by Ravi on 21-01-2017.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.instagram.instagramapi.activities.InstagramAuthActivity;
import com.instagram.instagramapi.engine.InstagramEngine;
import com.instagram.instagramapi.engine.InstagramKitConstants;
import com.instagram.instagramapi.exceptions.InstagramException;
import com.instagram.instagramapi.interfaces.InstagramAPIResponseCallback;
import com.instagram.instagramapi.interfaces.InstagramLoginCallbackListener;
import com.instagram.instagramapi.objects.IGMedia;
import com.instagram.instagramapi.objects.IGPagInfo;
import com.instagram.instagramapi.objects.IGSession;
import com.instagram.instagramapi.objects.IGUser;
import com.instagram.instagramapi.utils.InstagramKitLoginScope;
import com.instagram.instagramapi.widgets.InstagramLoginButton;

import java.util.ArrayList;

public class InstaSample extends AppCompatActivity {

    InstagramLoginButton instagramLoginButton;
    Button loginButton;

    String[] scopes = {InstagramKitLoginScope.BASIC, InstagramKitLoginScope.COMMENTS};
    //String[] scopes = {InstagramKitLoginScope.BASIC};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insta_sample);

        //Implement via InstagramLoginButton
        instagramLoginButton = (InstagramLoginButton) findViewById(R.id.instagramLoginButton);
        instagramLoginButton.setOnClickListener(instagramOnClickListener);
        instagramLoginButton.setInstagramLoginCallback(instagramLoginCallbackListener);
        instagramLoginButton.setScopes(scopes);


        //Implement via intent
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(loginOnClickListener);

    }

    InstagramLoginCallbackListener instagramLoginCallbackListener = new InstagramLoginCallbackListener() {
        @Override
        public void onSuccess(IGSession session) {

            Toast.makeText(InstaSample.this, "Wow!!! User trusts you :) " + session.getAccessToken(),
                    Toast.LENGTH_LONG).show();
            InstagramEngine.getInstance(InstaSample.this).getUserDetails(instagramUserResponseCallback);

        }

        @Override
        public void onCancel() {
            Toast.makeText(InstaSample.this, "Oh Crap!!! Canceled.",
                    Toast.LENGTH_LONG).show();

        }

        @Override
        public void onError(InstagramException error) {
            Toast.makeText(InstaSample.this, "User does not trust you :(\n " + error.getMessage(),
                    Toast.LENGTH_LONG).show();

        }
    };

    View.OnClickListener instagramOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


//            Intent intent = new Intent(InstaSample.this, InstagramAuthActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//            intent.putExtra(InstagramEngine.TYPE, InstagramEngine.TYPE_LOGIN);
//            //add scopes if you want to have more than basic access
//            intent.putExtra(InstagramEngine.SCOPE, scopes);
//
//            startActivityForResult(intent, 0);

        }
    };

    View.OnClickListener loginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(InstaSample.this, InstagramAuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

            intent.putExtra(InstagramEngine.TYPE, InstagramEngine.TYPE_LOGIN);
            intent.putExtra(InstagramEngine.SCOPE, scopes);

            startActivityForResult(intent, InstagramEngine.REQUEST_CODE_LOGIN);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case InstagramEngine.REQUEST_CODE_LOGIN:

                if (resultCode == RESULT_OK) {

                    Bundle bundle = data.getExtras();

                    if (bundle.containsKey(InstagramKitConstants.kSessionKey)) {

                        IGSession session = (IGSession) bundle.getSerializable(InstagramKitConstants.kSessionKey);

                        Toast.makeText(InstaSample.this, "Woohooo!!! User trusts you :) " + session.getAccessToken(),
                                Toast.LENGTH_LONG).show();

                    }
                }
                break;
            case InstagramEngine.REQUEST_CODE_LOGOUT:
                if (resultCode == RESULT_OK) {

                    Toast.makeText(InstaSample.this, "Logged Out Successfully.",
                            Toast.LENGTH_LONG).show();
                }
            default:
                break;
        }

    }

    public void genericClickListener(View v) {



        switch (v.getId()) {
            case R.id.userDetail:
                //tested
                InstagramEngine.getInstance(InstaSample.this).getUserDetails(instagramUserResponseCallback, "c78d0e33cbcb440db85ff8f77bdcde00");
                break;
            case R.id.selfUserDetail:
                //tested
                InstagramEngine.getInstance(InstaSample.this).getUserDetails(instagramUserResponseCallback);
                break;
            case R.id.mediaForSelfUser:
                //tested
                InstagramEngine.getInstance(InstaSample.this).getMediaForUser(instagramMediaResponseCallback);
                break;
            case R.id.mediaForUser:
                //tested
                InstagramEngine.getInstance(InstaSample.this).getMediaForUser(instagramMediaResponseCallback, "c78d0e33cbcb440db85ff8f77bdcde00");
                break;
            case R.id.userLikedMedia:
                //tested
                InstagramEngine.getInstance(InstaSample.this).getUserLikedMedia(instagramMediaResponseCallback);
                break;
            case R.id.getMediaForUser:
                //tested
                InstagramEngine.getInstance(InstaSample.this).getMediaForUser(mediaListApiResponseCallback, 5, null);
                break;
            case R.id.getUserLikedMedia:
                //tested
                InstagramEngine.getInstance(InstaSample.this).getUserLikedMedia(likedMediaApiResponseCallback);
                break;
            case R.id.getMedia:
                InstagramEngine.getInstance(InstaSample.this).getMedia(mediaApiResponseCallback, "mediaId");
                break;
            case R.id.logout:
                InstagramEngine.getInstance(InstaSample.this).logout(InstaSample.this, InstagramEngine.REQUEST_CODE_LOGOUT);
        }

    }





    InstagramAPIResponseCallback<IGUser> instagramUserResponseCallback = new InstagramAPIResponseCallback<IGUser>() {
        @Override
        public void onResponse(IGUser responseObject, IGPagInfo pageInfo) {
            Log.v("SampleActivity", "User:" + responseObject.getUsername() + ", User Id: " + responseObject.getId());

            Toast.makeText(InstaSample.this, "Username: " + responseObject.getUsername(),
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(InstagramException exception) {
            Log.v("SampleActivity", "Exception:" + exception.getMessage());
        }
    };




    InstagramAPIResponseCallback<IGMedia> mediaApiResponseCallback = new InstagramAPIResponseCallback<IGMedia>() {
        @Override
        public void onResponse(IGMedia responseMedia, IGPagInfo pageInfo) {
            Log.v("SampleActivity", "Liked Media: " + responseMedia.getType());
        }

        @Override
        public void onFailure(InstagramException exception) {
            Log.v("SampleActivity", "Exception:" + exception.getMessage());
        }
    };




    InstagramAPIResponseCallback<ArrayList<IGMedia>> likedMediaApiResponseCallback = new InstagramAPIResponseCallback<ArrayList<IGMedia>>() {
        @Override
        public void onResponse(ArrayList<IGMedia> responseArray, IGPagInfo pageInfo) {
            Log.v("SampleActivity", "Liked Media: " + responseArray.size());

            if (responseArray.size() > 0) {

                for (IGMedia media : responseArray) {

                    Toast.makeText(InstaSample.this, "Media Caption: " + media.getCaption().getText(),
                            Toast.LENGTH_LONG).show();
                    Log.v("SampleActivity", "Media Caption: " + media.getCaption().getText());
                    Log.v("SampleActivity", "Media Type: " + media.getType());
                    if (media.getType().equals(InstagramKitConstants.kMediaTypeImage)) {
                        Log.v("SampleActivity", "Media Photo: " + media.getImages().getStandardResolution().getUrl() + "\n");
                    }
                }
            }

            if (null != pageInfo && null != pageInfo.getNextMaxId() && !pageInfo.getNextMaxId().isEmpty()) {
                InstagramEngine.getInstance(InstaSample.this).getUserLikedMedia(likedMediaApiResponseCallback, 5, pageInfo.getNextMaxId());
            }

        }

        @Override
        public void onFailure(InstagramException exception) {
            Log.v("SampleActivity", "Exception:" + exception.getMessage());
        }
    };

    InstagramAPIResponseCallback<ArrayList<IGMedia>> mediaListApiResponseCallback = new InstagramAPIResponseCallback<ArrayList<IGMedia>>() {
        @Override
        public void onResponse(ArrayList<IGMedia> responseArray, IGPagInfo pageInfo) {
            Log.v("SampleActivity", "Media: " + responseArray.size());

            if (responseArray.size() > 0) {

                for (IGMedia media : responseArray) {

                    Toast.makeText(InstaSample.this, "Media Caption: " + media.getCaption().getText(),
                            Toast.LENGTH_LONG).show();
                    Log.v("SampleActivity", "Media Caption: " + media.getCaption().getText());
                    Log.v("SampleActivity", "Media Type: " + media.getType());
                    if (media.getType().equals(InstagramKitConstants.kMediaTypeImage)) {
                        Log.v("SampleActivity", "Media Photo: " + media.getImages().getStandardResolution().getUrl() + "\n");
                    }
                }
            }

            if (null != pageInfo && null != pageInfo.getNextMaxId() && !pageInfo.getNextMaxId().isEmpty()) {
                InstagramEngine.getInstance(InstaSample.this).getMediaForUser(mediaListApiResponseCallback, 5, pageInfo.getNextMaxId());
            }

        }

        @Override
        public void onFailure(InstagramException exception) {
            Log.v("SampleActivity", "Exception:" + exception.getMessage());
        }
    };



    InstagramAPIResponseCallback<ArrayList<IGMedia>> instagramMediaResponseCallback = new InstagramAPIResponseCallback<ArrayList<IGMedia>>() {
        @Override
        public void onResponse(ArrayList<IGMedia> responseObject, IGPagInfo pageInfo) {
            Log.v("SampleActivity", "Id:" + responseObject.size());

            Toast.makeText(InstaSample.this, "Id: " + responseObject.size(),
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(InstagramException exception) {
            Log.v("SampleActivity", "Exception:" + exception.getMessage());
        }
    };



}