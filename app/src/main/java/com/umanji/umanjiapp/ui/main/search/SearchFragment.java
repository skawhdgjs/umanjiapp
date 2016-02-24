package com.umanji.umanjiapp.ui.main.search;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static java.util.Locale.getDefault;

public class SearchFragment extends BaseFragment {
    private static final String TAG = "SearchFragment";


    /****************************************************
     *  View
     ****************************************************/
    protected AutoCompleteTextView mSearchText;
    protected TextView mAddress;

    protected ChannelData mChannelBySearch;

    protected BaseChannelListAdapter mAdapter;

    /****************************************************
     *  For Etc.
     ****************************************************/
    protected boolean isLoading = false;
    protected int mPreFocusedItem = 0;
    protected int mLoadCount = 0;

    public static SearchFragment newInstance(Bundle bundle) {
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);
        rView.setItemViewCacheSize(iItemViewCacheSize);

        addOnScrollListener(rView);
        mAdapter = new SearchAdapter(mActivity, this);
        rView.setAdapter(mAdapter);

        return view;
    }


    protected void addOnScrollListener(RecyclerView rView) {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(rView.getContext());
        rView.setLayoutManager(mLayoutManager);

        rView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    ArrayList<ChannelData> channels = mAdapter.getDocs();

                    int currentFocusedIndex = visibleItemCount + pastVisiblesItems;
                    if (currentFocusedIndex == mPreFocusedItem) return;
                    mPreFocusedItem = currentFocusedIndex;
                    if (channels.size() <= mPreFocusedItem) return;

                    if (!isLoading) {
                        if (mPreFocusedItem == (totalItemCount - 3)) {
                            loadMoreData();
                        }
                    }
                }
            }
        });
    }



    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_search, container, false);
    }

    @Override
    public void initWidgets(View view) {

        mAddress = (TextView) view.findViewById(R.id.address);
        mSearchText = (AutoCompleteTextView) view.findViewById(R.id.searchText);
        mSearchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Geocoder geoCoder = new Geocoder(mActivity, getDefault());

                    List<Address> addresses;
                    try {
                        addresses = geoCoder.getFromLocationName(mSearchText.getText().toString(), 1);

                        if (addresses.size() > 0) {
                            final Double latitude = (double) (addresses.get(0).getLatitude());
                            final Double longitude = (double) (addresses.get(0).getLongitude());

                            String fullName = addresses.get(0).getAddressLine(0);
                            mAddress.setText(fullName);

                            mAddress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        JSONObject params = new JSONObject();
                                        params.put("latitude", latitude);
                                        params.put("longitude", longitude);

                                        ChannelData channelData = new ChannelData(params);

                                        EventBus.getDefault().post(new SuccessData(EVENT_LOOK_AROUND, channelData.getJsonObject()));
                                    } catch (JSONException e) {
                                        Log.e(TAG, "Error " + e.toString());
                                    }

                                }
                            });


                            mAdapter.resetDocs();
                            mAdapter.setCurrentPage(0);

                            loadMoreData();

                        } else {
                            Toast.makeText(mActivity, "주소 검색 실패.", Toast.LENGTH_SHORT).show();
                        }



                    } catch (IOException e) {
                        Log.e(TAG, "Error " + e.toString());
                    }

                    mSearchText.setText("");
                }

                return true;
            }
        });
    }

    @Override
    public void loadData() {
    }


    public void loadMoreData() {
        isLoading = true;

        try {
            JSONObject params = new JSONObject();
            params.put("name", mSearchText.getText().toString());
            params.put("sort", "point DESC");

            mApi.call(api_main_findMarkers, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    super.callback(url, object, status);
                    try {
                        JSONArray jsonArray = object.getJSONArray("data");
                        for (int idx = 0; idx < jsonArray.length(); idx++) {
                            JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                            ChannelData doc = new ChannelData(jsonDoc);
                            mAdapter.addBottom(doc);
                        }

                        isLoading = false;
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error " + e.toString());
                    }

                }
            });

            mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
    }

    @Override
    public void updateView() {

    }


    /****************************************************
     *  Event Bus
     ****************************************************/

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case EVENT_LOOK_AROUND:
                mActivity.finish();
                break;
        }
    }

}
