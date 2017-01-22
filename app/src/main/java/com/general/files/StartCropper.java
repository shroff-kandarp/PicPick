package com.general.files;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

/**
 * Created by Shroff on 22-Jan-17.
 */
public class StartCropper {
    Context mContext;
    Activity act;
    Uri uri;
    GeneralFunctions generalFunc;

    public StartCropper(Context mContext, Uri uri) {
        this.mContext = mContext;
        this.uri = uri;
        act = (Activity) mContext;
        generalFunc = new GeneralFunctions(this.mContext);

        requestCropImage();
    }

    public void requestCropImage() {
        if (uri != null) {
            CropImage.activity(uri)
                    .start((Activity) mContext);
        } else {
            CropImage.startPickImageActivity((Activity) mContext);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(mContext, data);
            uri = imageUri;

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(mContext, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                act.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                requestCropImage();
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == act.RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), resultUri);

                    File file = generalFunc.saveImage(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
