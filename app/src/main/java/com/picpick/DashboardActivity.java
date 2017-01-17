package com.picpick;

import android.content.Context;
import android.graphics.Color;
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

import com.adapter.DrawerAdapter;
import com.general.files.GeneralFunctions;
import com.squareup.picasso.Picasso;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    DrawerLayout mDrawerLayout;
    ImageView menuImgView;

    ListView menuListView;
    DrawerAdapter drawerAdapter;
    public ArrayList<String[]> list_menu_items;

    TextView titleTxt;

    GeneralFunctions generalFunc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        menuImgView.setColorFilter(Color.parseColor("#FFFFFF"));
        menuImgView.setOnClickListener(new setOnClickList());

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
            }
        }

    }



    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == menuImgView.getId()) {
                loadDrawer();
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
        list_menu_items.add(new String[]{"" + R.mipmap.ic_launcher, "Gallery", "" + Utils.MENU_VIDEOS});
        list_menu_items.add(new String[]{"" + R.mipmap.ic_launcher, "My Account", "" + Utils.MENU_BLOG});

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
}
