package com.umanji.umanjiapp.ui.page.util;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.AuthData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageViewFragment extends BaseFragment {
    private static final String TAG = "ImageViewFragment";


    /****************************************************
     *  View
     ****************************************************/
    ImageView mPhoto;

    /****************************************************
     *  onCreate
     ****************************************************/


    public static ImageViewFragment newInstance(Bundle bundle) {
        ImageViewFragment fragment = new ImageViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image_view, container, false);
        super.onCreateView(view);

        mPhoto = (ImageView) view.findViewById(R.id.photo);

        updateView();
        return view;
    }

    @Override
    public void loadData() {

    }

    @Override
    public void updateView() {
        String photo = mChannel.getPhoto();
        if(!TextUtils.isEmpty(photo)) {
            Glide.with(mActivity)
                    .load(photo)
                    .placeholder(R.drawable.empty)
                    .animate(R.anim.abc_fade_in)
                    .into(mPhoto);

            mPhoto.setVisibility(View.VISIBLE);
        }else {
            mPhoto.setVisibility(View.GONE);
        }

    }

    @Override
    public void onEvent(SuccessData event){

    }
}
