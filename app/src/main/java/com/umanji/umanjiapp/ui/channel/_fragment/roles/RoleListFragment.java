package com.umanji.umanjiapp.ui.channel._fragment.roles;

import android.os.Bundle;
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

        String [] roles = mChannel.getRoles();

        if(roles != null) {
            mLayout.setBackgroundResource(R.color.feed_bg);

            for(int idx = 0; idx < roles.length; idx++) {
                String[] role = new String [] {roles[idx]};
                ChannelData doc = new ChannelData();
                doc.setId(mChannel.getId());
                doc.setRoles(role);
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
