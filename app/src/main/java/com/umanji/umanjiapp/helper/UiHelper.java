package com.umanji.umanjiapp.helper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class UiHelper implements AppConfig {
    private static final String TAG = "UiHelper";

    final static public int CODE_MAIN_ACTIVITY              = 0;
    final static public int CODE_AUTH_ACTIVITY              = 1;
    final static public int CODE_SPOT_ACTIVITY              = 2;
    final static public int CODE_COMMUNITY_ACTIVITY         = 3;

    final static public int CODE_CHANNEL_ACTIVITY           = 10;

    final static public int CODE_POST_CREATE_ACTIVITY       = 20;
    final static public int CODE_COMMUNITY_CREATE_ACTIVITY  = 21;


    final static public int CODE_GALLERY_ACTIVITY           = 30;
    final static public int CODE_CAMERA_ACTIVITY            = 31;

    final static public int CODE_PROFILE_SET_HOME_ACTIVITY  = 40;

    final static public int CODE_ABOUT_FRAGMENT             = 50;
    final static public int CODE_SPOTS_FRAGMENT             = 51;
    final static public int CODE_MEMBERS_FRAGMENT           = 52;
    final static public int CODE_POST_FRAGMENT              = 53;




    public static void callGallery(Fragment fragment) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, UiHelper.CODE_GALLERY_ACTIVITY);
    }

    public static String callCamera(Fragment fragment) {
        String folderName = "umanji";
        String fileName = "umanji-photo";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        // 폴더명 및 파일명
        String folderPath = path + File.separator + folderName;
        String filePath = path + File.separator + folderName + File.separator +  fileName + ".jpg";

        // 저장 폴더 지정 및 폴더 생성
        File fileFolderPath = new File(folderPath);
        fileFolderPath.mkdir();

        // 파일 이름 지정
        File file = new File(filePath);
        Uri outputFileUri = Uri.fromFile(file);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        fragment.startActivityForResult(cameraIntent, UiHelper.CODE_CAMERA_ACTIVITY);

        return filePath;
    }

    public static void showCustomToast(AppCompatActivity context, String message) {
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.widget_toast,
                (ViewGroup) context.findViewById(R.id.wtLinearLayout));

        TextView text = (TextView) layout.findViewById(R.id.wtText);
        text.setText(message);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


    public static String toPrettyDate(long timestamp) {

        long current = (new Date()).getTime();
        long diff = (current - timestamp)/1000;

        int	amount = 0;
        String	what = "";

        /**
         * Second counts
         * 3600: hour
         * 86400: day
         * 604800: week
         * 2592000: month
         * 31536000: year
         */

        if(diff > 31536000) {
            amount = (int)(diff/31536000);
            what = "year";
        }
        else if(diff > 31536000) {
            amount = (int)(diff/31536000);
            what = "month";
        }
        else if(diff > 604800) {
            amount = (int)(diff/604800);
            what = "week";
        }
        else if(diff > 86400) {
            amount = (int)(diff/86400);
            what = "day";
        }
        else if(diff > 3600) {
            amount = (int)(diff/3600);
            what = "hour";
        }
        else if(diff > 60) {
            amount = (int)(diff/60);
            what = "minute";
        }
        else {
            amount = (int)diff;
            what = "second";
            if(amount < 6) {
                return "Just now";
            }
        }

        if(amount == 1) {
            if(what.equals("day")) {
                return "Yesterday";
            }
            else if(what.equals("week") || what.equals("month") || what.equals("year")) {
                return "Last " + what;
            }
        }
        else {
            what += "s";
        }

        return amount + " " + what + " ago";
    }


    public static File imageUploadAndDisplay(Activity activity, ApiHelper apiHelper, File file, File resizedFile, ImageView photoView, boolean isFixedHeight) {
        try {

            // getBitmapFromFile
            ExifInterface exif = new ExifInterface(file.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;

            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file),
                    null, options);

            // getResizeRate
            int sourceImageWidth = bmp.getWidth();
            int sourceImageHeight = bmp.getHeight();

            int scaledWidth;
            int scaledHeight;
            if(sourceImageWidth > scaledImageLimit) {
                scaledWidth = scaledImageLimit;
                scaledHeight = (int)((float)scaledWidth * ((float)sourceImageHeight/(float)sourceImageWidth));

            } else {
                scaledWidth = sourceImageWidth;
                scaledHeight = sourceImageHeight;
            }

            float scaleWidth = ((float) scaledWidth) / bmp.getWidth();
            float scaleHeight = ((float) scaledHeight) / bmp.getHeight();

            // getResizedFile
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            matrix.postRotate(angle);

            Bitmap photo = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), matrix, true);

            resizedFile = new File(activity.getCacheDir() + file.getName());
            boolean result = resizedFile.createNewFile();
            FileOutputStream output = null;
            if(result == true) {
                output = new FileOutputStream(resizedFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.close();
            }
            ////



            Map<String, Object> params = new HashMap<String, Object>();
            params.put("photo", resizedFile);
            apiHelper.call(api_photo, params);


            // getReCalculatedHeight
            int deviceWidth = metrics.widthPixels;
            float rate;
            int height;
            if( angle == 90 || angle == 270) {
                rate = (float)deviceWidth / (float)scaledHeight;
                height = (int)(scaledWidth * rate);
            } else {
                rate = (float)deviceWidth / (float)scaledWidth;
                height = (int)(scaledHeight * rate);
            }

            Glide.with(activity).load(resizedFile).into(photoView);
            if(!isFixedHeight) {
                photoView.getLayoutParams().height = height;
            }

        } catch(IOException e) {
            Log.e("BaseChannelCreate", "error " + e.toString());
        }

        return resizedFile;
    }

    public static Intent getBaseIntent(Context context, String type, String id, int level, Class<?> cls) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("id", id);
        bundle.putInt("level", level);
        Intent intent = new Intent(context, cls);
        intent.putExtra("bundle", bundle);
        return intent;
    }

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }
    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
