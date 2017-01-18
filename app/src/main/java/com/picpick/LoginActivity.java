package com.picpick;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.general.files.GeneralFunctions;
import com.general.files.RegisterFbLoginResCallBack;
import com.general.files.StartActProcess;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.utils.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Ravi on 08-12-2016.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    Button googleLoginBtn;
    // GoogleApiClient mGoogleApiClient;

    GeneralFunctions generalFunc;
    CallbackManager callbackManager;
    Button fbLoginBtn;
    LoginButton loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        FacebookSdk.setWebDialogTheme(R.style.FBDialogtheme);
        setContentView(R.layout.login_screen);


        FacebookSdk.setApplicationId(Utils.FACEBOOK_APPID);
        generalFunc = new GeneralFunctions(getActContext());
        fbLoginBtn = (Button) findViewById(R.id.fblogin);
        fbLoginBtn.setOnClickListener(new setOnClickList());

        loginButton = new LoginButton(getActContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_about_me","user_photos"));
        loginButton.registerCallback(callbackManager, new RegisterFbLoginResCallBack(getActContext()));

        //buildGoogleApi();


        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.picpick", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }


    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fblogin:
                    loginButton.performClick();
                    break;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    public class setOnClickList implements View.OnClickListener {
//
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.googleLoginBtn:
//                    startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), Utils.GOOGLE_SIGN_IN_REQ_CODE);
//                    break;
//            }
//        }
//    }

    public Context getActContext() {
        return LoginActivity.this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Utils.GOOGLE_SIGN_IN_REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();

                Utils.printLog("Login", "::" + acct.getDisplayName());
                Utils.printLog("email", "::" + acct.getEmail());
                Utils.printLog("Googleid", "::" + acct.getId());
                Utils.printLog("tok", "::" + acct.getIdToken());
                Utils.printLog("photo", "::" + acct.getPhotoUrl());
                HashMap<String, String> userData = new HashMap<>();
                userData.put(Utils.email_key, acct.getEmail());
                userData.put(Utils.name_key, acct.getDisplayName());
                userData.put(Utils.SOCIAL_ID_key, acct.getId());
                userData.put(Utils.LOGIN_TYPE_key, Utils.SOCIAL_LOGIN_GOOGLE_key_value);


                generalFunc.setMemberData(userData);

                (new StartActProcess(getActContext())).startAct(DashboardActivity.class);
                ActivityCompat.finishAffinity(LoginActivity.this);

//                login("google", acct.getId(), acct.getEmail(), "");
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
