package com.umanji.umanjiapp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.ui.IconGenerator;
import com.koushikdutta.ion.Ion;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.ui.auth.SigninActivity;
import com.umanji.umanjiapp.ui.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.channel.community.create.CommunityCreateActivity;
import com.umanji.umanjiapp.ui.channel.community.update.CommunityUpdateActivity;
import com.umanji.umanjiapp.ui.channel.complex.ComplexActivity;
import com.umanji.umanjiapp.ui.channel.complex.create.ComplexCreateActivity;
import com.umanji.umanjiapp.ui.channel.complex.update.ComplexUpdateActivity;
import com.umanji.umanjiapp.ui.channel.info.InfoActivity;
import com.umanji.umanjiapp.ui.channel.keyword.create.KeywordCreateActivity;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;
import com.umanji.umanjiapp.ui.channel.post.reply.ReplyActivity;
import com.umanji.umanjiapp.ui.channel.profile.ProfileActivity;
import com.umanji.umanjiapp.ui.channel.profile.update.ProfileUpdateActivity;
import com.umanji.umanjiapp.ui.channel.spot.SpotActivity;
import com.umanji.umanjiapp.ui.channel.spot.create.SpotCreateActivity;
import com.umanji.umanjiapp.ui.channel.spot.update.SpotUpdateActivity;
import com.umanji.umanjiapp.ui.channelInterface.ChannelInterfaceActivity;
import com.umanji.umanjiapp.ui.main.MainActivity;
import com.umanji.umanjiapp.ui.mainHome.localCommunity.StepTwoActivity;
import com.umanji.umanjiapp.ui.modal.imageview.ImageViewActivity;
import com.umanji.umanjiapp.ui.modal.map.MapActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;

public final class Helper implements AppConfig {
    private static final String TAG = "Helper";
    private String args;

    public static String [] levelBounds =  {
            "6Lev 국가전체",       // 0
            "7Lev 전국",          // 1
            "8Lev 도 2개",        // 2
            "9Lev 도전체",        // 3
            "10Lev 서울, 경기",    // 4
            "11Lev 서울전지역",    // 5
            "12Lev 서울절반",      // 6
            "13Lev 구전지역",      // 7
            "14Lev 동전체",       // 8
            "15Lev 1Km",        // 9
            "16Lev 400m",       // 10
            "17Lev 200m",       // 11
            "18Lev 동네지역"      // 12
    };

    public static String dictionaryHasKeyword(String inputKeyword) {

        String keyword = inputKeyword.replaceAll("\\s", "");
        inputKeyword = inputKeyword.replaceAll("\\s", "");

        String dictionary = "= /" +
                "환경=공해, 미세먼지 /" +
                "에너지=무한에너지, 풍력발전 /" +
                "철학=이즈니스, isness /" +
                "역사=한국사, 중국역사, 중국사, 일본역사, 고대사, 상고사 /" +
                "통일=통일연구회, 통일연구소 /" +
                "건강=병원, 약국, 한의원, 약방, 종합병원, 의원 /" +
                "정치=더민주당, 새누리당, 정의당, 국민의당 /" +
                "등산=산악, 산악회, 등반 /" +
                "골프=골프장, 골프연습장, 스크린골프 /" +
                "중국집=중화요리, 짱게집, 짱개집, 자장면, 짜장면, 짬뽕 /" +
                "커피=커피샵, 커피전문점, 스타벅스, 탐앤탐스, 카페 /" +
                "아이=유아, 어린이, 육아, 공동육아, 아기, 베이비 /" +
                "창업=스타트업, startup /" +
                "아파트=주상복합, 주상복합아파트, 빌라, 맨숀, 거주지 /" +
                "식당=초밥, 비빔밥, 한식, 한식집, 간장게장 /" +
                "축구=사커, soccer, 조기축구, 조기축구회, 동네축구 /" +
                "NGO=앤지오, 엔지오, ngo, 비영리재단, 비정부조직 /" +
                "프로그래머=프로그레머, 프로그래밍, programmer, programer, programing, 개발자";

        // 연습장일경우 야구인지 골프인지 모른다

        int position = 0;
        int wordStart = 0;
        int wordEnd = 0;

        char checkChar;

        if (dictionary.contains(inputKeyword)) {
            position = dictionary.indexOf(inputKeyword);          // 입력한 단어의 위치

            startLoop:
            for (int idx = position; idx >= 0; idx--) {

                checkChar = dictionary.charAt(idx);

                if (checkChar == '/') {
                    wordStart = idx + 1;       // 찾는 단어의 시작 위치
                    if (wordStart != 0) {
                        break startLoop;
                    }
                }

            }

            wordEnd = dictionary.indexOf("=", wordStart);       // 찾는 단어의 마지막 위치

            keyword = dictionary.substring(wordStart, wordEnd); // 찾는 단어

        }

        return keyword;
    }

    public static String extractKeyword(String statement) {
        String inputStr = statement;

        int howlong = inputStr.length();

        char aaa;
        int startStr = 0;

        startLoop:
        for (int idx = 0; idx < howlong; idx++) {

            aaa = inputStr.charAt(idx);

            if (aaa == '/') {
                startStr = idx + 1;
                break startLoop;
            }
        }
        String preStr = inputStr.substring(startStr, howlong);

        String answer = preStr.substring(0, 1);
        answer = answer.toUpperCase();
        answer += preStr.substring(1);

        return answer;
    }


    public static boolean isInVisibleResion(GoogleMap map, LatLng point) {
        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
        LatLng farRight = visibleRegion.farRight;
        LatLng nearLeft = visibleRegion.nearLeft;

        double minLatitude = nearLeft.latitude;
        double maxLatitude = farRight.latitude;
        double minLongitude = nearLeft.longitude;
        double maxLongitude = farRight.longitude;

        if (point.latitude > minLatitude && point.latitude <= maxLatitude && point.longitude > minLongitude && point.longitude <= maxLongitude) {
            return true;
        } else {
            return false;
        }

    }

    public static int dpToPixel(Activity activity, int dp) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }

    public static boolean isAuthError(Activity activity) {

        boolean isAuthError = !AuthHelper.isLogin(activity);
        if (isAuthError) {
            GoogleMap map = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.mMapFragment))
                    .getMap();

            double latitude = 0.0f;
            double longitude = 0.0f;
            Bundle bundle = new Bundle();

            if (map != null) {
                latitude = map.getMyLocation().getLatitude();
                longitude = map.getMyLocation().getLongitude();
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
            }

        }

        return isAuthError;
    }


    public static LatLng getAdjustedPoint(GoogleMap map, LatLng point) {

        LatLng tmpPoint;
        int zoom = (int) map.getCameraPosition().zoom;
        switch (zoom) {
            case 14:
                tmpPoint = new LatLng(point.latitude - 0.0006, point.longitude);
                break;
            case 15:
                tmpPoint = new LatLng(point.latitude - 0.0005, point.longitude);
                break;
            case 16:
                tmpPoint = new LatLng(point.latitude - 0.0004, point.longitude);
                break;
            case 17:
                tmpPoint = new LatLng(point.latitude - 0.0003, point.longitude);
                break;
            case 18:
                tmpPoint = new LatLng(point.latitude - 0.0002, point.longitude);
                break;
            case 19:
                tmpPoint = new LatLng(point.latitude - 0.0001, point.longitude);
                break;
            default:
                tmpPoint = new LatLng(point.latitude - 0.00005, point.longitude);
                break;
        }

        return tmpPoint;
    }

    public static Marker addNewMarkerToMap(GoogleMap map, ChannelData channelData) {
        LatLng point = new LatLng(channelData.getLatitude(), channelData.getLongitude());
        Marker marker;
        marker = map.addMarker(new MarkerOptions().position(point)
                .title("스팟생성")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_aqua))
                .alpha(0.8f)  // default 1.0
                .anchor(0.45f, 1.0f));

        return marker;
    }

/*
* MainFragment show marker
*
*
* */
    public static Marker addMarkerToMap(GoogleMap map, ChannelData channelData, int index, Activity activity, boolean isDraggable) {
        LatLng point = new LatLng(channelData.getLatitude(), channelData.getLongitude());
        Marker marker;

        IconGenerator tc = new IconGenerator(activity);

        tc.setColor(Color.parseColor("#0d0d0d"));           // background color yellow : ffff33
        tc.setTextAppearance(R.style.keywordCommunityText);          // text design
        Bitmap bmp = tc.makeIcon();

//        keywordCommunityText

        String name = channelData.getName();
        String end = "";
        int stringLength = name.length();
        int endOfString ;
        if(stringLength > 10){
            endOfString = 10;
            end = "...";
        } else {
            endOfString = stringLength;
        }
        String title = name.substring(0, endOfString) + end;
        if (TextUtils.isEmpty(name)) {
            name = "일반장소";
        }

        int overOne = 0;
        String strOverOne = null;

        ArrayList<SubLinkData> subLinks = channelData.getSubLinks(TYPE_COMMUNITY);
        String thisType = channelData.getType();
        if (subLinks != null && subLinks.size() > 0) {
            if (subLinks.size() > 1) {
                overOne = subLinks.size() - 1;
                strOverOne = String.valueOf(overOne);

                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(subLinks.get(0).getName() + "외 " + strOverOne + "개")))
                        .alpha(0.9f)  // default 1.0
                        .anchor(0.45f, 1.0f));

            } else {
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(subLinks.get(0).getName())))
                        .alpha(0.9f)  // default 1.0
                        .anchor(0.45f, 1.0f));
            }
        } else if (thisType != null && thisType.equals(TYPE_POST)) {
            // here is default poi marker !! It was hard to find :(
            marker = map.addMarker(new MarkerOptions().position(point)
                    .title(name)
                    .snippet(String.valueOf(index))
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(title)))     // String
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.poi2))
                    .draggable(isDraggable)
                    .alpha(0.7f)  // default 1.0
                    .anchor(0.45f, 1.0f));
        } else {
            switch (channelData.getLevel()) {
                case LEVEL_COMPLEX:
                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.complex))
                            .draggable(isDraggable)
                            .alpha(0.9f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;
                case LEVEL_DONG:
                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dong))
                            .draggable(isDraggable)
                            .alpha(0.9f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;
                case LEVEL_GUGUN:
                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gugun))
                            .draggable(isDraggable)
                            .alpha(0.9f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;
                case LEVEL_DOSI:
                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.city))
                            .draggable(isDraggable)
                            .alpha(0.9f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;
                case LEVEL_COUNTRY:
                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.kr))
                            .draggable(isDraggable)
                            .alpha(0.9f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;

                default:

                    if (TextUtils.isEmpty(channelData.getName())) {
                        ArrayList<SubLinkData> inSpots = channelData.getSubLinks(TYPE_SPOT_INNER);
                        if (inSpots != null && inSpots.size() > 0) {
                            name = inSpots.get(0).getName();
                        }
                    }

                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.poi2))
                            .draggable(isDraggable)
                            .alpha(0.8f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;

            }
        }


        return marker;
    }

    public static int myColor(int argb) {
        return Color.rgb(
                Color.red(argb),
                Color.green(argb),
                Color.blue(argb)
        );
    }

    public static Marker addStaffToMap(GoogleMap map, ChannelData channelData, int index, Activity activity) {
        return addStaffToMap(map, channelData, index, activity, false);
    }

    public static Marker addStaffToMap(GoogleMap map, ChannelData channelData, int index, Activity activity, boolean isDraggable) {
        LatLng point = new LatLng(channelData.getLatitude(), channelData.getLongitude());
        Marker marker;

        IconGenerator tc = new IconGenerator(activity);
        tc.setColor(Color.parseColor("#ffff33"));           // background color yellow : ffff33
        tc.setTextAppearance(R.style.keywordCommunityText);          // text design
        Bitmap bmp = tc.makeIcon();

//        keywordCommunityText

        String name = channelData.getName();
        if (TextUtils.isEmpty(name)) {
            name = "in Helper";
        }

        int overOne = 0;
        String strOverOne = null;

        ArrayList<SubLinkData> subLinks = channelData.getSubLinks(TYPE_COMMUNITY);
        if (subLinks != null && subLinks.size() > 0) {
            if (subLinks.size() > 1) {
                overOne = subLinks.size() - 1;
                strOverOne = String.valueOf(overOne);

                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(subLinks.get(0).getName() + "외 " + strOverOne + "개")))
                        .alpha(0.8f)  // default 1.0
                        .anchor(0.45f, 1.0f));

            } else {
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(subLinks.get(0).getName())))
                        .alpha(0.8f)  // default 1.0
                        .anchor(0.45f, 1.0f));
            }
        } else {
            String userPhoto = null;
            if (channelData.getOwner() != null) {
                userPhoto = channelData.getOwner().getPhoto();
            }
            Bitmap bmImg = BitmapFactory.decodeResource(activity.getResources(), activity.getResources().getIdentifier("user_default", "drawable", activity.getPackageName()));
            try {

                bmImg = Ion.with(activity).load(userPhoto).asBitmap().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            switch (channelData.getLevel()) {
                case LEVEL_DONG:
                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(userPhoto, 100, 100, activity)))
                            .draggable(isDraggable)
                            .alpha(0.9f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;
                case LEVEL_GUGUN:
                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(userPhoto, 100, 100, activity)))
                            .draggable(isDraggable)
                            .alpha(0.9f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;
                case LEVEL_DOSI:
                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromBitmap(myResizeMapIcons(bmImg, 100, 100, activity)))
                            .draggable(isDraggable)
                            .alpha(0.9f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;
                case LEVEL_COUNTRY:
                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromBitmap(myResizeMapIcons(bmImg, 100, 100, activity)))
                            .draggable(isDraggable)
                            .alpha(0.9f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;

                default:

                    if (TextUtils.isEmpty(channelData.getName())) {
                        ArrayList<SubLinkData> inSpots = channelData.getSubLinks(TYPE_SPOT_INNER);
                        if (inSpots != null && inSpots.size() > 0) {
                            name = inSpots.get(0).getName();
                        }
                    }

                    marker = map.addMarker(new MarkerOptions().position(point)
                            .title(name)
                            .snippet(String.valueOf(index))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.poi2))
                            .draggable(isDraggable)
                            .alpha(0.9f)  // default 1.0
                            .anchor(0.45f, 1.0f));
                    break;

            }
        }
        return marker;
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static Bitmap myResizeMapIcons(Bitmap iconName, int width, int height, Activity activity) {
        Bitmap imageBitmap = iconName;
        Bitmap resizedBitmap;
        Bitmap finalBitmap;

        if (imageBitmap.getWidth() >= imageBitmap.getHeight()) {

            resizedBitmap = Bitmap.createBitmap(
                    imageBitmap,
                    imageBitmap.getWidth() / 2 - imageBitmap.getHeight() / 2,
                    0,
                    imageBitmap.getHeight(),
                    imageBitmap.getHeight()
            );

        } else {

            resizedBitmap = Bitmap.createBitmap(
                    imageBitmap,
                    0,
                    imageBitmap.getHeight() / 2 - imageBitmap.getWidth() / 2,
                    imageBitmap.getWidth(),
                    imageBitmap.getWidth()
            );
        }
        finalBitmap = Bitmap.createScaledBitmap(resizedBitmap, width, height, false);

        return finalBitmap;
    }

    public static Bitmap resizeMapIcons(String iconName, int width, int height, Activity activity) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(activity.getResources(), activity.getResources().getIdentifier(iconName, "drawable", activity.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


    public static Marker addMarkerToMapOnStepOne(GoogleMap map, ChannelData channelData, int index, Activity activity) {

        LatLng point = new LatLng(channelData.getLatitude(), channelData.getLongitude());
        Marker marker = null;

        IconGenerator tc = new IconGenerator(activity);
        tc.setColor(Color.parseColor("#ffff33"));           // background color yellow : ffff33
        tc.setTextAppearance(R.style.keywordCommunityText);          // text design
        Bitmap bmp = tc.makeIcon();

        String name = channelData.getName();
        if (TextUtils.isEmpty(name)) {
            name = "일반장소";
        }

        int overOne = 0;
        String strOverOne = null;

        ArrayList<SubLinkData> subLinks = channelData.getSubLinks(TYPE_COMMUNITY);
//        String[] communityKeyword = channelData.getKeywords();                      //키워드로 검색
        if (subLinks != null && subLinks.size() > 0) {
            if (subLinks.size() > 1) {
                overOne = subLinks.size() - 1;
                strOverOne = String.valueOf(overOne);

                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(subLinks.get(0).getName() + "외 " + strOverOne + "개")))
                        .alpha(0.8f)  // default 1.0
                        .anchor(0.45f, 1.0f));

            } else {
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(subLinks.get(0).getName())))
                        .alpha(0.8f)  // default 1.0
                        .anchor(0.45f, 1.0f));
            }


        }
        return marker;
    }

    public static Marker addMarkerToMapOnKeyword(GoogleMap map, ChannelData channelData, int index, Activity activity) {
        LatLng point = new LatLng(channelData.getLatitude(), channelData.getLongitude());
        Marker marker = null;

        IconGenerator tc = new IconGenerator(activity);
        tc.setColor(Color.parseColor("#0d0d0d"));           // background color yellow : ffff33
        tc.setTextAppearance(R.style.keywordCommunityText);          // text design
        Bitmap bmp = tc.makeIcon();

        // to modify marker size
        Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.election);
        Bitmap smallMarker = Bitmap.createScaledBitmap(icon, 100, 100, false);

        Bitmap icon2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.community_marker);
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(icon2, 100, 100, false);

        Bitmap flag = BitmapFactory.decodeResource(activity.getResources(), R.drawable.flag);
        Bitmap smallflag = Bitmap.createScaledBitmap(flag, 100, 100, false);

        String name = channelData.getName();
        String end = "";
        int stringLength = name.length();
        int endOfString ;
        if(stringLength > 10){
            endOfString = 10;
            end = "...";
        } else {
            endOfString = stringLength;
        }
        String title = name.substring(0, endOfString) + end;

        if (TextUtils.isEmpty(name)) {
            name = "어떤곳";
        }

        String keyword = channelData.getName();

        if (channelData.getType().equals(TYPE_COMMUNITY)) {                // 실제 커뮤니티 : 만든 장소
            marker = map.addMarker(new MarkerOptions().position(point)
                    .title(name)
                    .snippet(String.valueOf(index))
//                    .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(keyword)))
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.community_marker))
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker2))
                    .alpha(1.0f)  // default 1.0
                    .anchor(0.45f, 1.0f));
        } else if (channelData.getType().equals(TYPE_SPOT) || channelData.getType().equals(TYPE_COMPLEX) || channelData.getType().equals(TYPE_SPOT_INNER)) {
            tc.setColor(Color.parseColor("#ffff33"));
            tc.setTextAppearance(R.style.keywordSpotText);
            marker = map.addMarker(new MarkerOptions().position(point)
                    .title(name)
                    .snippet(String.valueOf(index))
//                    .icon(BitmapDescriptorFactory.fromBitmap(smallflag))
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(title)))     // String
                    .anchor(0.45f, 1.0f));
// doing now
        } else if (channelData.getType().equals(TYPE_POST)) {
            tc.setTextAppearance(R.style.keywordPostText);
            marker = map.addMarker(new MarkerOptions().position(point)
                    .title(name)
                    .snippet(String.valueOf(index))
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(title)))     // String
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.poi2))
                    .alpha(0.7f)  // default 1.0
                    .anchor(0.45f, 1.0f));

        }
//        block reason :: do not Info center (keyword community) mark on keyword community mode

        /*
        else if (channelData.getType().equals(TYPE_KEYWORD_COMMUNITY)) {     // like Info Center
            String thoroughfare = channelData.getParent().getThoroughfare();
            String locality = channelData.getParent().getLocality();
            String admin = channelData.getParent().getAdminArea();
            String coutry = channelData.getParent().getCountryName();

            String typeName = name + " 연합";
            Bitmap myBitmap = null;
//            doing
            switch (channelData.getParent().getLevel()) {
                case LEVEL_DONG:
                    typeName = thoroughfare + " " + name + "연합";
                    myBitmap = myResizeMapIcons(smallMarker, 75, 75, activity);
                    break;
                case LEVEL_GUGUN:
                    typeName = locality + " " + name + "연합";
                    myBitmap = myResizeMapIcons(smallMarker, 90, 90, activity);
                    break;
                case LEVEL_DOSI:
                    typeName = admin + " " + name + "연합";
                    myBitmap = myResizeMapIcons(smallMarker, 105, 105, activity);
                    break;
                case LEVEL_COUNTRY:
                    typeName = coutry + " " + name + "연합";
                    myBitmap = myResizeMapIcons(smallMarker, 120, 120, activity);
                    break;

            }
//            tc.setTextAppearance(R.style.keywordSpotText);
            marker = map.addMarker(new MarkerOptions().position(point)
                    .title(typeName)
                    .snippet(String.valueOf(index))
                    .icon(BitmapDescriptorFactory.fromBitmap(myBitmap))
                    .anchor(0.45f, 1.0f));

        } else {
            *//*
            marker = map.addMarker(new MarkerOptions().position(point)
                    .title(name)
                    .snippet(String.valueOf(index))
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(keyword)))
                    .alpha(0.8f)  // default 1.0
                    .anchor(0.45f, 1.0f));
            *//*
        }
*/
        return marker;

/*

        else if(channelData.getKeywords() != null && channelData.getType().equals(TYPE_SPOT)){
            marker = map.addMarker(new MarkerOptions().position(point)
                    .title(name)
                    .snippet(String.valueOf(index))
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp = tc.makeIcon(keyword)))
                    .alpha(0.8f)  // default 1.0
                    .anchor(0.45f, 1.0f));

        }
        */

    }


    public static Marker addMarkerToMap(GoogleMap map, ChannelData channelData, int index, Activity activity) {
        return addMarkerToMap(map, channelData, index, activity, false);
    }


    public static void callAuthErrorEvent() {
        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
    }

    public static void startSigninActivity(Activity activity, LatLng position) {
        Intent intent = new Intent(activity, SigninActivity.class);
        Bundle bundle = new Bundle();

        if (position != null) {
            bundle.putDouble("latitude", position.latitude);
            bundle.putDouble("longitude", position.longitude);
        }
        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static void startMapActivity(Activity activity, ChannelData channelData, String mapType) {
        Bundle bundle = new Bundle();
        bundle.putString("channel", channelData.getJsonObject().toString());
        bundle.putString("mapType", mapType);
        Intent intent = new Intent(activity, MapActivity.class);
        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static void startImageViewActivity(Activity activity, ChannelData channelData) {
        Intent intent = new Intent(activity, ImageViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("channel", channelData.getJsonObject().toString());
        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static void startPostCreateActivity(Activity activity, ChannelData channelData) {
        Intent intent = new Intent(activity, PostCreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("channel", channelData.getJsonObject().toString());
        bundle.putString("type", "POST");
        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static void startCreateActivity(Activity activity, ChannelData channelData, String type) {
        Intent intent = null;
        Bundle bundle = new Bundle();
        bundle.putString("channel", channelData.getJsonObject().toString());

        switch (type) {
            case TYPE_POST:
                intent = new Intent(activity, PostCreateActivity.class);
                break;
            case TYPE_COMMUNITY:
                intent = new Intent(activity, CommunityCreateActivity.class);
                break;
            case TYPE_KEYWORD:
                intent = new Intent(activity, KeywordCreateActivity.class);
                break;
            case TYPE_COMPLEX:
                intent = new Intent(activity, ComplexCreateActivity.class);
                break;
            case TYPE_SPOT:
                intent = new Intent(activity, SpotCreateActivity.class);
                break;

        }

        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static void startUpdateActivity(Activity activity, ChannelData channelData) {
        Intent intent = null;
        Bundle bundle = new Bundle();
        bundle.putString("channel", channelData.getJsonObject().toString());

        switch (channelData.getType()) {
            case TYPE_USER:
                intent = new Intent(activity, ProfileUpdateActivity.class);
                break;
            case TYPE_COMPLEX:
                intent = new Intent(activity, ComplexUpdateActivity.class);
                break;
            case TYPE_COMMUNITY:
                intent = new Intent(activity, CommunityUpdateActivity.class);
                break;
            case TYPE_INFO_CENTER:
                intent = new Intent(activity, CommunityUpdateActivity.class);
                break;
            case TYPE_SPOT_INNER:
            case TYPE_SPOT:
                intent = new Intent(activity, SpotUpdateActivity.class);
                break;
        }

        if (intent != null) {
            intent.putExtra("bundle", bundle);
            activity.startActivity(intent);
        }
    }

    public static void startKeywordMapActivity(Activity activity, ChannelData channelData) {
        Intent intent = null;
        Bundle bundle = new Bundle();
        bundle.putString("channel", channelData.getJsonObject().toString());
        bundle.putString("tabType", TAB_POSTS);
        bundle.putString("type", "keywordCommunityMode");

//        intent = new Intent(activity, KeywordCommunityActivity.class);
        intent = new Intent(activity, MainActivity.class);
        intent.putExtra("enterAnim", R.anim.zoom_out);
        intent.putExtra("exitAnim", R.anim.zoom_in);

        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static void startActivity(Activity activity, ChannelData channelData, String tabType) {
        Intent intent = null;
        Bundle bundle = new Bundle();

        bundle.putString("channel", channelData.getJsonObject().toString());

        if(tabType != null) {
            if (tabType.equals("BottomList")) {
                bundle.putString("extraData", tabType);
            } else {
                bundle.putString("tabType", tabType);
            }
        }

        switch (channelData.getType()) {
            case TYPE_SPOT:
                intent = new Intent(activity, SpotActivity.class);
                intent.putExtra("enterAnim", R.anim.zoom_out);
                intent.putExtra("exitAnim", R.anim.zoom_in);
                break;
            case TYPE_COMPLEX:
                bundle.putString("keyword", tabType);
                intent = new Intent(activity, ComplexActivity.class);
                intent.putExtra("enterAnim", R.anim.zoom_out);
                intent.putExtra("exitAnim", R.anim.zoom_in);
                break;
            case TYPE_SPOT_INNER:
                intent = new Intent(activity, SpotActivity.class);
                break;

            case TYPE_USER:
                intent = new Intent(activity, ProfileActivity.class);
                break;
            case TYPE_KEYWORD:
            case TYPE_COMMUNITY:
            case TYPE_KEYWORD_COMMUNITY:
                intent = new Intent(activity, CommunityActivity.class);
                if (tabType != null) {
                    bundle.putString("keyword", tabType);
                }
                break;
            case TYPE_INFO_CENTER:
                intent = new Intent(activity, InfoActivity.class);
                intent.putExtra("enterAnim", R.anim.zoom_out);
                intent.putExtra("exitAnim", R.anim.zoom_in);
                break;

            case TYPE_MEMBER:
                intent = new Intent(activity, ProfileActivity.class);
                bundle.putString("fromType", "whatever");
                break;
            case TYPE_LIKE:
                intent = new Intent(activity, ProfileActivity.class);
            case TYPE_POST:
                intent = new Intent(activity, ReplyActivity.class);
                break;
            case TYPE_LOCAL_SPOT:
                intent = new Intent(activity, StepTwoActivity.class);
                bundle.putString("localType", "local_spot");
                break;
            case TYPE_LOCAL_COMPLEX:
                intent = new Intent(activity, StepTwoActivity.class);
                bundle.putString("localType", "local_complex");
                break;
            case TYPE_INTERFACE:
                intent = new Intent(activity, ChannelInterfaceActivity.class);
                break;
        }

        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static void startKeywordActivity(Activity activity, ChannelData channelData, String tabType, String fromType) {
        Intent intent = null;
        Bundle bundle = new Bundle();

        bundle.putString("channel", channelData.getJsonObject().toString());
        bundle.putString("extraData", fromType);
        bundle.putString("keyword", tabType);

        switch (channelData.getType()) {
            case TYPE_SPOT:
                intent = new Intent(activity, SpotActivity.class);
                intent.putExtra("enterAnim", R.anim.zoom_out);
                intent.putExtra("exitAnim", R.anim.zoom_in);
                break;
            case TYPE_COMPLEX:
                intent = new Intent(activity, ComplexActivity.class);
                intent.putExtra("enterAnim", R.anim.zoom_out);
                intent.putExtra("exitAnim", R.anim.zoom_in);
                break;
            case TYPE_SPOT_INNER:
                intent = new Intent(activity, SpotActivity.class);
                break;

            case TYPE_USER:
                intent = new Intent(activity, ProfileActivity.class);
                break;
            case TYPE_KEYWORD:
            case TYPE_COMMUNITY:
            case TYPE_KEYWORD_COMMUNITY:
                intent = new Intent(activity, CommunityActivity.class);
                if (tabType != null) {
                    bundle.putString("keyword", tabType);
                }
                break;
            case TYPE_INFO_CENTER:
                intent = new Intent(activity, InfoActivity.class);
                intent.putExtra("enterAnim", R.anim.zoom_out);
                intent.putExtra("exitAnim", R.anim.zoom_in);
                break;

            case TYPE_MEMBER:
                intent = new Intent(activity, ProfileActivity.class);
                bundle.putString("fromType", "whatever");
                break;
            case TYPE_LIKE:
                intent = new Intent(activity, ProfileActivity.class);
            case TYPE_POST:
                intent = new Intent(activity, ReplyActivity.class);
                break;
            case TYPE_LOCAL_SPOT:
                intent = new Intent(activity, StepTwoActivity.class);
                bundle.putString("localType", "local_spot");
                break;
            case TYPE_LOCAL_COMPLEX:
                intent = new Intent(activity, StepTwoActivity.class);
                bundle.putString("localType", "local_complex");
                break;
            case TYPE_INTERFACE:
                intent = new Intent(activity, ChannelInterfaceActivity.class);
                break;
        }

        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static void startActivity(Activity activity, ChannelData channelData) {
        startActivity(activity, channelData, TAB_POSTS);
    }

    public static String getShortenString(String str) {

        if (str.length() > 10) {
            return str.substring(0, 10) + "..";
        } else {
            return str;
        }
    }

    public static String getShortenString(String str, int size) {

        if (str.length() > size) {
            return str.substring(0, size) + "..";
        } else {
            return str;
        }
    }

    public static String getFullAddress(ChannelData channelData) {
        return channelData.getCountryName() + " " + channelData.getAdminArea() + " " + channelData.getLocality() + " " + channelData.getThoroughfare() + " " + channelData.getFeatureName();
    }

    public static String getMiddleAddress(ChannelData channelData) {
        return channelData.getCountryName() + " " + channelData.getAdminArea() + " " + channelData.getLocality() + " " + channelData.getThoroughfare();
    }

    public static String getShortAddress(ChannelData channelData) {
        return channelData.getCountryName() + " " + channelData.getAdminArea() + " " + channelData.getLocality();
    }

    public static JSONObject getZoomMinMaxLatLngParams(GoogleMap map) {
        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
        LatLng farRight = visibleRegion.farRight;
        LatLng nearLeft = visibleRegion.nearLeft;

        JSONObject params = new JSONObject();

        try {

            params.put("minLatitude", nearLeft.latitude);
            params.put("maxLatitude", farRight.latitude);
            params.put("minLongitude", nearLeft.longitude);
            params.put("maxLongitude", farRight.longitude);

        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
        return params;
    }

    public static void callGallery(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, CODE_GALLERY_ACTIVITY);
    }

    public static String callCamera(Fragment fragment) {
        String folderName = "umanji";
        String fileName = "umanji-photo";

        Random rd = new Random();
        int randomNum = rd.nextInt(10000);
        fileName = fileName + "-" + randomNum;

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        // 폴더명 및 파일명
        String folderPath = path + File.separator + folderName;
        String filePath = path + File.separator + folderName + File.separator + fileName + ".jpg";

        // 저장 폴더 지정 및 폴더 생성
        File fileFolderPath = new File(folderPath);
        fileFolderPath.mkdir();

        // 파일 이름 지정
        File file = new File(filePath);
        Uri outputFileUri = Uri.fromFile(file);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        fragment.startActivityForResult(cameraIntent, CODE_CAMERA_ACTIVITY);

        return filePath;
    }

    public static File getFileFromBitmap(Bitmap bitmap) {
        String folderName = "umanji";
        String fileName = "umanji-photo";

        Random rd = new Random();
        int randomNum = rd.nextInt(10000);
        fileName = fileName + "-" + randomNum;

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        // 폴더명 및 파일명
        String folderPath = path + File.separator + folderName;
        String filePath = path + File.separator + folderName + File.separator + fileName + ".jpg";

        // 저장 폴더 지정 및 폴더 생성
        File fileFolderPath = new File(folderPath);
        fileFolderPath.mkdir();

        // 파일 이름 지정
        File file = new File(filePath);

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e(TAG, "Error " + e.toString());
        }

        return file;
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
        long diff = (current - timestamp) / 1000;

        int amount = 0;
        String what = "";

        /**
         * Second counts
         * 3600: hour
         * 86400: day
         * 604800: week
         * 2592000: month
         * 31536000: year
         */

        if (diff > 31536000) {
            amount = (int) (diff / 31536000);
            what = "year";
        } else if (diff > 31536000) {
            amount = (int) (diff / 31536000);
            what = "month";
        } else if (diff > 604800) {
            amount = (int) (diff / 604800);
            what = "week";
        } else if (diff > 86400) {
            amount = (int) (diff / 86400);
            what = "day";
        } else if (diff > 3600) {
            amount = (int) (diff / 3600);
            what = "hour";
        } else if (diff > 60) {
            amount = (int) (diff / 60);
            what = "minute";
        } else {
            amount = (int) diff;
            what = "second";
            if (amount < 6) {
                return "Just now";
            }
        }

        if (amount == 1) {
            if (what.equals("day")) {
                return "Yesterday";
            } else if (what.equals("week") || what.equals("month") || what.equals("year")) {
                return "Last " + what;
            }
        } else {
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
            if (sourceImageWidth > scaledImageLimit) {
                scaledWidth = scaledImageLimit;
                scaledHeight = (int) ((float) scaledWidth * ((float) sourceImageHeight / (float) sourceImageWidth));

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

            resizedFile = new File(activity.getCacheDir() + "/" + file.getName());
            boolean result = resizedFile.createNewFile();
            FileOutputStream output = null;
            if (result == true) {
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
            if (angle == 90 || angle == 270) {
                rate = (float) deviceWidth / (float) scaledHeight;
                height = (int) (scaledWidth * rate);
            } else {
                rate = (float) deviceWidth / (float) scaledWidth;
                height = (int) (scaledHeight * rate);
            }

            Glide.with(activity)
                    .load(resizedFile)
                    .into(photoView);
            photoView.setTag("done");

            if (!isFixedHeight) {
                photoView.getLayoutParams().height = height;
            }

        } catch (IOException e) {
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


    public static void showNoticePanel(final Activity activity, final View notiPanel, String message) {
        TextView noticeMessage = (TextView) notiPanel.findViewById(R.id.noticeMessage);

        Animation ani = AnimationUtils.loadAnimation(activity, R.anim.notice_down);
        notiPanel.setVisibility(View.VISIBLE);
        notiPanel.startAnimation(ani);
        noticeMessage.setText(message);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation ani = AnimationUtils.loadAnimation(activity, R.anim.notice_up);
                notiPanel.startAnimation(ani);
                notiPanel.setVisibility(View.GONE);
            }
        }, 3000);
    }


/*****  custom marker
 *
 Bitmap.Config conf = Bitmap.Config.ARGB_8888;
 Bitmap bmp = Bitmap.createBitmap(80, 80, conf);     // marker size
 Canvas canvas1 = new Canvas(bmp);

 // paint defines the text color, stroke width and size
 Paint color = new Paint();
 color.setTextSize(26);
 color.setColor(Color.BLACK);
 color.setFakeBoldText(true);

 // modify canvas
 canvas1.drawBitmap(BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher), 100,10, color);
 canvas1.drawText("Set Text Here", 0, 0, color);                 // string , Left, Top, color

 */

}
