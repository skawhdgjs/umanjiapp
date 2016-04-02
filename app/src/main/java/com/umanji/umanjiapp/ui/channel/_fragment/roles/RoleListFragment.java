package com.umanji.umanjiapp.ui.channel._fragment.roles;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class RoleListFragment extends BaseChannelListFragment {
    private static final String TAG = "RoleListFragment";

    private TextView mRole ;


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

    }

    @Override
    public void loadMoreData() {

        String [] roles = mChannel.getRoles();

        if(roles != null) {
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
