package com.umanji.umanjiapp.ui.channel.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseTabAdapter;
import com.umanji.umanjiapp.ui.channel.BaseChannelFragment;
import com.umanji.umanjiapp.ui.channel._fragment.about.AboutProfileFragment;
import com.umanji.umanjiapp.ui.channel._fragment.communities.CommunityListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.noties.NotyListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.spots.SpotListFragment;
import com.umanji.umanjiapp.ui.modal.imageview.ImageViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ProfileFragment extends BaseChannelFragment {
    private static final String TAG = "ProfileFragment";

    private File mResizedFile;
    private String mPhotoUri;

    public static ProfileFragment newInstance(Bundle bundle) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    protected void addFragmentToTabAdapter(BaseTabAdapter adapter) {
        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());

        if(AuthHelper.isLoginUser(mActivity, mChannel.getId())) {
            adapter.addFragment(NotyListFragment.newInstance(bundle), "NOTIES");
        }
        adapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
        adapter.addFragment(SpotListFragment.newInstance(bundle), "SPOTS");
        adapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
        adapter.addFragment(AboutProfileFragment.newInstance(bundle), "ABOUT");
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    @Override
    public void updateView() {
        super.updateView();

        mFab.setVisibility(View.GONE);

        setUserName(mActivity, mChannel, "프로필");
        setPhoto(mActivity, mChannel, R.drawable.multi_spot_background);
        setParentName(mActivity, mChannel.getParent());
        setUserPhoto(mActivity, mChannel);
        setPoint(mActivity, mChannel);
        setMemberCount(mActivity, mChannel);
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_photo:
                mProgress.hide();

                try{
                    mResizedFile.delete();
                    mResizedFile = null;
                    mPhotoUri = null;

                    JSONObject data = event.response.getJSONObject("data");
                    mPhotoUri = REST_S3_URL + data.optString("photo");


                    JSONObject params = new JSONObject();
                    params.put("id", mChannel.getId());

                    ArrayList<String> photos = new ArrayList<>();
                    photos.add(mPhotoUri);
                    params.put("photos", new JSONArray(photos));

                    mApi.call(api_profile_id_update, params);
                    mPhotoUri = null;
                }catch(JSONException e) {
                    Log.e("BaseChannelCreate", "error " + e.toString());
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.userPhoto:
                if(AuthHelper.isLoginUser(mActivity, mChannel.getId())) {
                    UiHelper.callGallery(this);
                }else {
                    Intent intent = new Intent(mActivity, ImageViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("channel", mChannel.getJsonObject().toString());
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(intent == null) return;

        switch (requestCode) {
            case UiHelper.CODE_GALLERY_ACTIVITY:
                mProgress.show();
                File file = FileHelper.getFileFromUri(mActivity, intent.getData());
                mResizedFile = UiHelper.imageUploadAndDisplay(mActivity, mApi, file, mResizedFile, mUserPhoto, true);
                break;
        }
    }
}
