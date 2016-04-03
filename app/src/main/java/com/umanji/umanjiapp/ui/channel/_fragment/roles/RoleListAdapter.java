package com.umanji.umanjiapp.ui.channel._fragment.roles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.distribution.DistributionActivity;
import com.umanji.umanjiapp.ui.modal.WebViewActivity;


public class RoleListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "RoleListAdapter";


    public RoleListAdapter(BaseActivity activity, Fragment fragment) {
        super(activity, fragment);
    }

    public RoleListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
        super(activity, fragment, channelData);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_role, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChannelData channelData       = mChannels.get(position);
        setRole(holder, channelData);
    }

    protected void setRole(final ViewHolder holder, final ChannelData channelData){

        String[] roles ;
        String role = null;

        roles = mChannel.getRoles();
        if(roles != null) {
            role = roles[0];
        }

        String roleName ="역할명";

        switch(role){
            case "umanji_cow":
                roleName = "우만지 일꾼";
                break;
            case "ad_admin":
                roleName = "시도단위 광고 권한자";
                break;
            case "ad_locality":
                roleName = "구군단위 광고 권한자";
                break;
            case "ad_thoroughfare":
                roleName = "읍면동단위 광고 권한자";
                break;

        }

        holder.mRole.setText(roleName);
        holder.mRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AuthHelper.isLoginUser(mActivity, mChannel.getId())) {
                    Intent roleIntent = new Intent(mActivity, DistributionActivity.class);
                    Bundle roleBundle = new Bundle();
                    roleBundle.putString("channel", mChannel.getJsonObject().toString());
                    roleIntent.putExtra("bundle", roleBundle);
                    mActivity.startActivity(roleIntent);
                } else {

                    Toast.makeText(mActivity, "역할 설명창으로 이동합니다.", Toast.LENGTH_LONG).show();
                    Intent webIntent = new Intent(mActivity, WebViewActivity.class);
                    mActivity.startActivity(webIntent);
                }

            }
        });
    }
}
