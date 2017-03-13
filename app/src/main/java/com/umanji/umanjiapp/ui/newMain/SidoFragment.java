package com.umanji.umanjiapp.ui.newMain;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.ui.BaseFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by nam on 2017. 2. 17..
 */

public class SidoFragment extends BaseFragment {

    private static final String TAG = "SidoFragment";
    private static final String KEY_LAYOU_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    private boolean isFirst = true;

    private JSONObject mParams;
    private int Level;

    private RecyclerView mRecyclerView;
    private RecycleViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    protected ArrayList<ChannelData> mChannels;

    private JSONArray jsonArrayBottom;
    protected ChannelData mChannel;

    protected boolean isLoading = false;
    protected int mPreFocusedItem = 0;
    protected int mLoadCount = 0;

    public static SidoFragment newInstance(Bundle bundle){
        SidoFragment fragment = new SidoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApi = new ApiHelper(getContext());

    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void updateView() {
        mAdapter.notifyDataSetChanged();
        mProgress.hide();
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        View v =  inflater.inflate(R.layout.fragment_sido_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView_sido);
        addOnScrollListener(mRecyclerView);
        //mAdapter = new RecycleViewAdapter(getActivity(), getActivity().getApplicationContext(), mChannels);
        mAdapter = new RecycleViewAdapter(getActivity(), getActivity().getApplicationContext(), mChannels);
        mRecyclerView.setAdapter(mAdapter);

        loadData();

        return v;
    }

    @Override
    public void loadData() {
        mAdapter.resetDocs();
        mAdapter.setCurrentPage(0);

        loadMoreData(mParams);

        isFirst = false;


    }

    @Override
    public void initWidgets(View view) {

    }


    protected void addOnScrollListener(RecyclerView rView) {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        final GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 2);

        rView.setLayoutManager(mGridLayoutManager);

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
                        if (mPreFocusedItem != (totalItemCount - 1)) {  // -1 : origin / -3 : too many
                            loadMoreData(mParams);
                        }
                    }
                }
            }
        });
    }


    public void showProgress(){
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("데이터를 불러오고 있습니다. 잠시만 기다려주세요...");
        mProgress.setCancelable(true);

        mProgress.show();
    }

    private void loadMoreData(JSONObject params){

        isLoading = true;
        mLoadCount = mLoadCount + 1;

        if(isFirst) {
            showProgress();
        }

        try{
            // mParams = new JSONObject(getData);
            mParams = new JSONObject();
            mParams.put("minLatitude", 37.227384404109785);
            mParams.put("maxLatitude",37.67364796417896);
            mParams.put("minLongitude",126.73921667039394);
            mParams.put("maxLongitude",127.17866979539394);
            mParams.put("type","COMMUNITY");
            mParams.put("limit",10);
            mParams.put("access_token","");
            mParams.put("page",mAdapter.getCurrentPage());

        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("sido_Tester", mParams.toString());
        mApi.call(api_main_findPosts, mParams, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (status.getCode() == 500) {
                    EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                } else {
                    try {
                        JSONArray jsonArray = json.getJSONArray("data");
                        jsonArrayBottom = jsonArray;
                        if (jsonArray.length() != 0) {
                            //isTalkFlag = true;
                            //mTalk.setImageResource(R.drawable.button_kakao);
                            //mTalk.startAnimation(talkAnimation);
                            String mData = jsonArray.toString();
                            mChannels = new ArrayList<ChannelData>();
                            String getData = mData;

                            for (int idx = 0; idx < jsonArray.length(); idx++) {
                                JSONObject jsonDoc = null;
                                try {
                                    jsonDoc = jsonArray.getJSONObject(idx);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ChannelData doc = new ChannelData(jsonDoc);
                                mAdapter.addBottom(doc);
                                //Log.d("sido_tester", doc.getName());
                                updateView();
                            }

                        } else {
                            // isTalkFlag = false;
                            // mTalk.setImageResource(R.drawable.button_kakao_black);
                            //mTalk.clearAnimation();
                        }
                        mProgress.dismiss();
                        isLoading = false;
                        //mTalkAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error " + e.toString());
                    }
                }
            }
        });

        mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
    }



}
