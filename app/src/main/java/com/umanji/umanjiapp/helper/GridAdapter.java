package com.umanji.umanjiapp.helper;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by paul on 6/25/16.
 */
public class GridAdapter extends BaseAdapter {
    private Context context;
    protected Activity mActivity;
    protected Fragment mFragment;
    protected ApiHelper mApi;

    protected String mType;

    protected ChannelData mChannel;
    protected ArrayList<ChannelData> mChan;

    /****************************************************
     * ArrayList
     ****************************************************/
    protected ArrayList<ChannelData> mChannels;
    protected int mCurrentPage = 0;

    public GridAdapter(BaseActivity activity, ArrayList channelData) {
        this.mActivity = activity;
//        this.mFragment = fragment;
        this.mChannel = null;
//        mChannels   = new ArrayList<ChannelData>();
        mChan = channelData;
        mApi = new ApiHelper(activity);

//        EventBus.getDefault().register(this);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView = inflater.inflate(R.layout.grid_item, null);



                if (convertView == null) {
                // set value into textview
                TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
                textView.setText(mChan.get(position).getName());

                // set image based on selected text
                ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
                } else {
                    gridView = (View) convertView;

                }


        return gridView;
    }

    @Override
    public int getCount() {
        return mChan.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}