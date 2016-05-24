package com.umanji.umanjiapp.ui.channel._fragment.roles;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;

public class RoleListFragment extends BaseChannelListFragment {
    private static final String TAG = "RoleListFragment";

    private TextView mRole ;
    private LinearLayout mLayout ;

    private String [] mRoles = {
            "info_world",
            "info_country",
            "info_admin",
            "info_locality",
            "info_thoroughfare",

            "ad_world",
            "ad_country",
            "ad_admin",
            "ad_locality",
            "ad_thoroughfare",

            "umanji_cow",
            "umanji_citizon"
    };


    public static RoleListFragment newInstance(Bundle bundle) {
        RoleListFragment fragment = new RoleListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_roles, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new RoleListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {

        mRole = (TextView) view.findViewById(R.id.roles);
        mLayout = (LinearLayout) view.findViewById(R.id.emptyRole);

    }

    @Override
    public void loadMoreData() {

        if(mRoles != null) {
            mLayout.setBackgroundResource(R.color.feed_bg);


            String [] roles = mChannel.getRoles();

            for(int idx = 0; idx < mRoles.length; idx++) {

                String[] role = new String [] {mRoles[idx]};
                ChannelData doc = new ChannelData();
                doc.setId(mChannel.getId());
                doc.setRoles(role);

                for(int idx2 = 0; idx2 < roles.length; idx2++) {
                    if(TextUtils.equals(roles[idx2], role[0])) {
                        doc.setType("ACTIVE");
                    }
                }

                mAdapter.addBottom(doc);
            }



            updateView();
        }
    }

    @Override
    public void updateView() {
        mAdapter.notifyDataSetChanged();
    }
}
