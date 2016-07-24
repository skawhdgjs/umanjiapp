package com.umanji.umanjiapp.ui.modal.imageview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageViewFragment extends BaseFragment {
    private static final String TAG = "SigninFragment";


    /****************************************************
     *  View
     ****************************************************/
    ImageView mPhoto;
    PhotoViewAttacher mPhotoAttacher;

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



        return view;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_image_view, container, false);
    }

    @Override
    public void initWidgets(View view) {
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        updateView();
    }

    @Override
    public void loadData() {
    }

    @Override
    public void updateView() {
        final String photo = mChannel.getPhoto();
        if(!TextUtils.isEmpty(photo)) {
            Glide.with(mActivity)
                    .load(photo)
                    .placeholder(R.drawable.empty)
//                    .animate(R.anim.abc_fade_in)
                    .fitCenter()
//                    .centerCrop()
                    .into(mPhoto);
            /*
            Glide.with(mActivity).load(photo).asBitmap().fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(new BitmapImageViewTarget(mPhoto) {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                    super.onResourceReady(bitmap, anim);
                    Glide.with(mActivity)
                            .load(photo)
                            .placeholder(R.drawable.empty)
                            .dontAnimate()
//                            .animate(R.anim.move_base)
                            .fitCenter()
                            .into(mPhoto);
                }
            });
*/

            mPhoto.setVisibility(View.VISIBLE);
        }else {
            mPhoto.setVisibility(View.GONE);
        }

        mPhotoAttacher = new PhotoViewAttacher(mPhoto);
    }


    /****************************************************
     *  Event Bus
     ****************************************************/

    public void onEvent(SuccessData event){

    }

}
