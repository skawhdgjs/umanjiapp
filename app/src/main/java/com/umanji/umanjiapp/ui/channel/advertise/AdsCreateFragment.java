package com.umanji.umanjiapp.ui.channel.advertise;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;
import com.umanji.umanjiapp.ui.modal.calendar.AdsCalendarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AdsCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "AdsCreateFragment";


    protected TextView mStartDay;
    protected TextView mCurrentPoint;
    protected TextView mEndDay;
    protected RadioGroup mRadio;
    protected String startDay   = "2016 04 09";
    protected String endDay     = "2016 04 10";


    protected int mAdLevel = 18;

    int intPoint;
    String mPoint;


    public static AdsCreateFragment newInstance(Bundle bundle) {
        AdsCreateFragment fragment = new AdsCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_ads_create, container, false);

    }


    @Override
    public void initWidgets(View view) {
        super.initWidgets(view);

        try {
            JSONObject params = new JSONObject();
            params.put("id", AuthHelper.getUserId(mActivity));
            mApi.call(api_channels_get, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    ChannelData mLogUserData = new ChannelData(object);
                    intPoint = mLogUserData.getPoint();
                    mPoint = Integer.toString(intPoint);

                    mCurrentPoint.setText(mPoint + " p");
                }
            });
            updateView();
        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }


        mStartDay = (TextView) view.findViewById(R.id.startDay);
        mStartDay.setOnClickListener(this);

        mCurrentPoint = (TextView) view.findViewById(R.id.currentPoint);
        mCurrentPoint.setText(mPoint + " p");

        mEndDay = (TextView) view.findViewById(R.id.endDay);
        mEndDay.setOnClickListener(this);

        mRadio = (RadioGroup) view.findViewById(R.id.radioGroup);
        mRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.radioButton1:
                        mAdLevel = 18;
                        break;
                    case R.id.radioButton2:
                        mAdLevel = 15;
                        break;
                    case R.id.radioButton3:
                        mAdLevel = 12;
                        break;
                    case R.id.radioButton4:
                        mAdLevel = 8;
                        break;
                }
            }
        });



        mStartDay.setText(startDay);
        mEndDay.setText(endDay);

    }

    protected void calculateDate(){
        // calculate Date
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy MM dd");

        try {
            Date date1 = myFormat.parse(startDay);
            Date date2 = myFormat.parse(endDay);
            long diff = date2.getTime() - date1.getTime();
            // System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void submit() {
        if (intPoint > 3000) {
            request();
        } else {
            Toast.makeText(mActivity, "포인트가 부족합니다.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void request() {
        try {
            JSONObject params = mChannel.getAddressJSONObject();
            params.put("parent", mChannel.getId());
            // params.put("level", mChannel.getLevel());
            params.put("level", mAdLevel);
            params.put("name", mName.getText().toString());
            params.put("startDay", mStartDay.getText().toString());
            params.put("endDay", mEndDay.getText().toString());
            params.put("type", TYPE_ADS);


            if (mPhotoUri != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mPhotoUri);
                params.put("photos", new JSONArray(photos));
                mPhotoUri = null;
            } else if (mChannel.getPhoto() != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mChannel.getPhoto());
                params.put("photos", new JSONArray(photos));
            }

            mApi.call(api_channels_create, params);

        } catch (JSONException e) {
            Log.e("BaseChannelCreate", "error " + e.toString());
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_create:
                mActivity.finish();
                break;
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.startDay:
                Intent i = new Intent(mActivity, AdsCalendarActivity.class);
                startActivityForResult(i, 0);
                break;
            case R.id.endDay:
                Intent iEnd = new Intent(mActivity, AdsCalendarActivity.class);
                startActivityForResult(iEnd, 1);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        File file = null;
        switch (requestCode) {
            case CODE_CAMERA_ACTIVITY:
                mProgress.show();
                file = new File(mFilePath);
                mResizedFile = Helper.imageUploadAndDisplay(mActivity, mApi, file, mResizedFile, mPhoto, false);
                break;
            case CODE_GALLERY_ACTIVITY:
                mProgress.show();
                file = FileHelper.getFileFromUri(mActivity, data.getData());
                mResizedFile = Helper.imageUploadAndDisplay(mActivity, mApi, file, mResizedFile, mPhoto, false);
                break;

            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    String result=data.getStringExtra("result");
                    mStartDay.setText(result);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // Handle cancel
                }
                break;

            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String result=data.getStringExtra("result");
                    mEndDay.setText(result);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // Handle cancel
                }
                break;
        }

    }

}


