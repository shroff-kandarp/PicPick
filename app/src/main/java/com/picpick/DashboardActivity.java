package com.picpick;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.DrawerAdapter;
import com.facebook.FacebookSdk;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.general.files.StartCropper;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    DrawerLayout mDrawerLayout;
    ImageView menuImgView, imgCart1;
    Uri mCropImageUri;
    ListView menuListView;
    DrawerAdapter drawerAdapter;
    public ArrayList<String[]> list_menu_items;
    ImageView img;
    TextView titleTxt;
    GeneralFunctions generalFunc;

    public static final String CLIENT_ID = "c78d0e33cbcb440db85ff8f77bdcde00";
    public static final String CLIENT_SECRET = "ee2c99883f2f4e378f4ab413dbad66ce ";
    public static final String CALLBACK_URL = "https://www.instagram.com/";

    StartCropper startCropper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActContext());
        FacebookSdk.setApplicationId(Utils.FACEBOOK_APPID);
        FacebookSdk.setWebDialogTheme(R.style.FBDialogtheme);
        setContentView(R.layout.activity_dashboard);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.appThemeColor));
        }

        generalFunc = new GeneralFunctions(getActContext());

        menuImgView = (ImageView) findViewById(R.id.menuImgView);
        menuListView = (ListView) findViewById(R.id.menuListView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        titleTxt = (TextView) findViewById(R.id.titleTxt);

        img = (ImageView) findViewById(R.id.imgCroped);

        menuImgView.setColorFilter(Color.parseColor("#FFFFFF"));
        menuImgView.setOnClickListener(new setOnClickList());

        imgCart1 = (ImageView) findViewById(R.id.menuCartView);
        imgCart1.setOnClickListener(new setOnClickList());


        new CreateRoundedView(getResources().getColor(R.color.appThemeColor), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 0),
                getResources().getColor(R.color.appThemeColor), (findViewById(R.id.toolbar_include)));

        buildMenu();

        if (generalFunc.isUserLoggedIn()) {
            (findViewById(R.id.header_area)).setVisibility(View.VISIBLE);
            (findViewById(R.id.header_area_noLogin)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.userNameTxt)).setText(generalFunc.retriveValue(Utils.name_key));

            if (generalFunc.retriveValue(Utils.LOGIN_TYPE_key).equals(Utils.SOCIAL_LOGIN_GOOGLE_key_value)) {
                //loadImageFromGoogle(generalFunc.retriveValue(Utils.SOCIAL_ID_key));
            } else if (generalFunc.retriveValue(Utils.LOGIN_TYPE_key).equals(Utils.SOCIAL_LOGIN_FACEBOOK_key_value)) {

                String uerProfileImageUrl = "https://graph.facebook.com/" + generalFunc.retriveValue(Utils.SOCIAL_ID_key) + "/picture?type=large";
                Picasso.with(getActContext())
                        .load(uerProfileImageUrl)
                        .placeholder(R.mipmap.ic_no_pic_user)
                        .error(R.mipmap.ic_no_pic_user)
                        .into((SelectableRoundedImageView) findViewById(R.id.userImgView));

                Utils.printLog(Utils.FACEBOOK_ACCESS_TOKEN_KEY, "::" + generalFunc.retriveValue(Utils.FACEBOOK_ACCESS_TOKEN_KEY));
            }
        }

    }
//
//    InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {
//
//        @Override
//        public void onSuccess() {
//
//            Log.e("Userid", instaObj.getId());
//            Log.e("Name", instaObj.getName());
//            Log.e("UserName", instaObj.getUserName());
//
//        }
//
//        @Override
//        public void onFail(String error) {
//            Toast.makeText(DashboardActivity.this, error, Toast.LENGTH_SHORT)
//                    .show();
//        }
//    };


    public void onFacebookImageClick(View v) {
        (new StartActProcess(getActContext())).startActForResult(FacebookPhotosActivity.class, Utils.ACT_REQ_CODE_FACEBOO_PHOTO_SELECT);
    }

    public void onSelectImageClick(View view) {
        startCropper = new StartCropper(getActContext(), null);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void startCropImageActivity(Uri imageUri) {
        startCropper = new StartCropper(getActContext(), imageUri);
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == menuImgView.getId()) {
                loadDrawer();
            }
            if (view.getId() == imgCart1.getId()) {
                Intent cart = new Intent(DashboardActivity.this, MyCartActivity.class);
                startActivity(cart);
            }
        }
    }

    public void loadDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START) == true) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void buildMenu() {
        if (list_menu_items == null) {
            list_menu_items = new ArrayList();
            drawerAdapter = new DrawerAdapter(list_menu_items, getActContext());

            menuListView.setAdapter(drawerAdapter);
            menuListView.setOnItemClickListener(this);
        } else {
            list_menu_items.clear();
        }

        list_menu_items.add(new String[]{"" + R.mipmap.ic_launcher, "About Us", "" + Utils.MENU_ABOUT_US});
        list_menu_items.add(new String[]{"" + R.mipmap.ic_launcher, "My Images", "" + Utils.MENU_IMAGES});
        list_menu_items.add(new String[]{"" + R.mipmap.ic_launcher, "My Address", "" + Utils.MENU_ADDRESS});

        if (generalFunc.isUserLoggedIn()) {
            list_menu_items.add(new String[]{"" + R.mipmap.ic_launcher, "Sign Out", "" + Utils.MENU_SIGN_OUT});
        }
        drawerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        int itemId = Integer.parseInt(list_menu_items.get(position)[2]);
        switch (itemId) {
            case Utils.MENU_SIGN_OUT:
                generalFunc.signOut();

                break;

            case Utils.MENU_ADDRESS:

                Intent i = new Intent(DashboardActivity.this, AddressActivity.class);
                startActivity(i);

                break;

            case Utils.MENU_IMAGES:

                Intent i1 = new Intent(DashboardActivity.this, MyImagesActivity.class);
                startActivity(i1);

                break;

        }
    }


    public Context getActContext() {
        return DashboardActivity.this;
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        } else {
            super.onBackPressed();
        }
    }


    public void onInstagramClick(View view) {


//        Intent i = new Intent(DashboardActivity.this, InstaSample.class);
//        startActivity(i);
//
//        instaObj = new InstagramApp(this, CLIENT_ID,
//                CLIENT_SECRET, CALLBACK_URL);
//        instaObj.setListener(listener);
//
//
//        instaObj.authorize();

        String insta_access_token = generalFunc.retriveValue(Utils.INSTA_USER_ACCESS_TOKEN_KEY);
        String insta_user_id = generalFunc.retriveValue(Utils.INSTA_USER_ID_KEY);
        if (!insta_access_token.equals("") && !insta_user_id.equals("")) {
            (new StartActProcess(getActContext())).startActForResult(InstaPhotosActivity.class, Utils.ACT_REQ_CODE_INSTA_PHOTO_SELECT);

        } else {
            InstagramDialog instaDialog = new InstagramDialog(getActContext(), CommonUtilities.INSTA_AUTH_URL,
                    CommonUtilities.INSTA_CALLBACK_URL, new InstagramDialog.InstagramDialogListener() {
                @Override
                public void onSuccess(String code) {
                    Utils.printLog("CODE:", "::" + code);
                    getUserAccessToken(code);
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(String error) {

                }
            });

            instaDialog.show();
        }


    }

    public void getUserAccessToken(String auth_code) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("client_id", CommonUtilities.INSTA_CLIENT_ID);
        parameters.put("client_secret", CommonUtilities.INSTA_CLIENT_SECRET);
        parameters.put("grant_type", "authorization_code");
        parameters.put("code", auth_code);
        parameters.put("redirect_uri", CommonUtilities.INSTA_CALLBACK_URL);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters, CommonUtilities.INSTA_TOKEN_URL);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {


                    String access_token = generalFunc.getJsonValue("access_token", responseString);
                    String id = generalFunc.getJsonValue("id", generalFunc.getJsonValue("user", responseString));

                    if (!access_token.trim().equals("") && !id.trim().equals("")) {
                        generalFunc.storedata(Utils.INSTA_USER_ID_KEY, id);
                        generalFunc.storedata(Utils.INSTA_USER_ACCESS_TOKEN_KEY, access_token);

                        (new StartActProcess(getActContext())).startActForResult(InstaPhotosActivity.class, Utils.ACT_REQ_CODE_INSTA_PHOTO_SELECT);

                    }

                } else {
                    generalFunc.showGeneralMessage("Error", "Please try again later.");
                }
            }
        });
        exeWebServer.execute();
    }

//    InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {
//
//        @Override
//        public void onSuccess() {
//
//            Log.e("Userid", instaObj.getId());
//            Log.e("Name", instaObj.getName());
//            Log.e("UserName", instaObj.getUserName());
//
//        }
//
//        @Override
//        public void onFail(String error) {
//            Toast.makeText(DashboardActivity.this, error, Toast.LENGTH_SHORT)
//                    .show();
//        }
//    };


    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (startCropper != null) {
            startCropper.onActivityResult(requestCode, resultCode, data);
        }
    }
}
