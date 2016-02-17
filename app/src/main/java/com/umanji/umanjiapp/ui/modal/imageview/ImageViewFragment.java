package com.umanji.umanjiapp.ui.modal.imageview;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;

public class ImageViewFragment extends BaseFragment {
    private static final String TAG = "SignupFragment";


    /****************************************************
     *  View
     ****************************************************/
    ImageView mPhoto;

    ChannelData mChannel;

    public static ImageViewFragment newInstance(Bundle bundle) {
        ImageViewFragment fragment = new ImageViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            String jsonString = getArguments().getString("channel");
            if(jsonString != null) {
                mChannel = new ChannelData(jsonString);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        updateView();

        return view;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_image_view, container, false);
    }

    @Override
    public void initWidgets(View view) {
        mPhoto = (ImageView) view.findViewById(R.id.photo);
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


    /****************************************************
     *  Event Bus
     ****************************************************/

    public void onEvent(SuccessData event){

    }

}
